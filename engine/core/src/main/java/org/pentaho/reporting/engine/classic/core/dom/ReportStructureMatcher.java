/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.dom;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ReportStructureMatcher {
  private static class NodeIterator {
    private boolean singleResult;
    private ArrayList<ReportElement> nodes;
    private NodeMatcher matcher;
    private MatcherContext context;

    private NodeIterator() {
      nodes = new ArrayList<ReportElement>();
    }

    public ReportElement get( final MatcherContext context, final ReportElement node, final NodeMatcher matcher ) {
      if ( node == null ) {
        throw new NullPointerException();
      }
      if ( matcher == null ) {
        throw new NullPointerException();
      }

      this.context = context;
      this.matcher = matcher;
      this.nodes.clear();
      this.singleResult = true;
      startProcessing( node, true );
      if ( this.nodes.isEmpty() ) {
        return null;
      }
      return this.nodes.get( 0 );
    }

    private void startProcessing( final ReportElement node, final boolean first ) {
      if ( node instanceof Section ) {
        final Section s = (Section) node;
        if ( first == true || startBox( node ) ) {
          final int elementCount = s.getElementCount();
          for ( int i = 0; i < elementCount; i += 1 ) {
            startProcessing( s.getElement( i ), false );
          }
        }
      } else {
        processOtherNode( node );
      }
    }

    public ReportElement[] getAll( final MatcherContext context, final ReportElement node, final NodeMatcher matcher ) {
      if ( node == null ) {
        throw new NullPointerException();
      }
      if ( matcher == null ) {
        throw new NullPointerException();
      }

      this.context = context;
      this.matcher = matcher;
      this.nodes.clear();
      this.singleResult = false;
      startProcessing( node, true );
      return this.nodes.toArray( new ReportElement[this.nodes.size()] );
    }

    protected boolean startBox( final ReportElement box ) {
      if ( singleResult && nodes.isEmpty() == false ) {
        return false;
      }

      if ( context.isMatchSubReportChilds() == false ) {
        if ( box.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.SUBREPORT ) {
          return false;
        }
      }
      if ( matcher.matches( context, box ) ) {
        nodes.add( box );
        if ( singleResult ) {
          return false;
        }
      }
      return true;
    }

    protected void processOtherNode( final ReportElement node ) {
      if ( singleResult && nodes.isEmpty() == false ) {
        return;
      }

      if ( matcher.matches( context, node ) ) {
        nodes.add( node );
      }
    }
  }

  public enum Type {
    Start, Descendant, Child, Id, Class, Element
  }

  private static ElementMatcher createMatcher( final StreamTokenizer tokenizer ) {
    if ( tokenizer.ttype == '*' ) {
      return new AnyNodeMatcher();
    }
    return new ElementMatcher( tokenizer.sval );
  }

  public static NodeMatcher parse( final String s ) throws IOException {
    final StreamTokenizer tokenizer = new StreamTokenizer( new StringReader( s ) );
    tokenizer.wordChars( '0', '9' );
    tokenizer.ordinaryChar( '.' );
    tokenizer.ordinaryChar( ',' );
    tokenizer.ordinaryChars( 0, ' ' );

    ElementMatcher elementMatcher = null;
    NodeMatcher n = null;
    Type selectorType = Type.Start;
    int token;
    while ( ( token = tokenizer.nextToken() ) != StreamTokenizer.TT_EOF ) {
      if ( token == StreamTokenizer.TT_WORD || token == '*' ) {
        NodeMatcher matcher = null;

        switch ( selectorType ) {
          case Start:
            elementMatcher = createMatcher( tokenizer );
            matcher = elementMatcher;
            break;
          case Child:
            n = new ChildMatcher( n );
            elementMatcher = createMatcher( tokenizer );
            matcher = elementMatcher;
            break;
          case Descendant:
            n = new DescendantMatcher( n );
            elementMatcher = createMatcher( tokenizer );
            matcher = elementMatcher;
            break;
          case Id:
            if ( elementMatcher == null ) {
              if ( n != null ) {
                n = new DescendantMatcher( n );
              }
              elementMatcher = createMatcher( tokenizer );
              matcher = elementMatcher;
            }
            elementMatcher.add( new AttributeMatcher( AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID,
                tokenizer.sval ) );
            break;
          case Class:
            if ( elementMatcher == null ) {
              if ( n != null ) {
                n = new DescendantMatcher( n );
              }
              elementMatcher = createMatcher( tokenizer );
              matcher = elementMatcher;
            }
            elementMatcher.add( new AttributeMatcher( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_CLASS,
                tokenizer.sval ) );
            break;
          default:
            throw new IOException();
        }

        selectorType = Type.Element;

        if ( matcher != null ) {
          if ( n != null ) {
            n = new AndMatcher( matcher, n );
          } else {
            n = matcher;
          }
        }
      } else {
        if ( token == '>' ) {
          selectorType = Type.Child;
        }
        if ( token == '.' ) {
          selectorType = Type.Class;
        }
        if ( token == '#' ) {
          selectorType = Type.Id;
        }
        if ( Character.isWhitespace( token ) ) {
          if ( selectorType == Type.Class || selectorType == Type.Id ) {
            throw new IllegalStateException();
          }

          if ( selectorType != Type.Child ) {
            selectorType = Type.Descendant;
          }
        }
      }
    }
    return n;
  }

  public static ReportElement match( final MatcherContext context, final ReportElement base, final NodeMatcher parse ) {
    if ( parse == null ) {
      throw new NullPointerException();
    }
    context.setSingleSelectionHint( true );
    return new NodeIterator().get( context, base, parse );
  }

  public static ReportElement[] matchAll( final MatcherContext context, final ReportElement base,
      final NodeMatcher parse ) {
    if ( parse == null ) {
      throw new NullPointerException();
    }
    context.setSingleSelectionHint( false );
    return new NodeIterator().getAll( context, base, parse );
  }

  public static ReportElement match( final MatcherContext context, final ReportElement base, final String selector )
    throws IOException {
    final NodeMatcher parse = parse( selector );
    return match( context, base, parse );
  }

  public static ReportElement[]
    matchAll( final MatcherContext context, final ReportElement base, final String selector ) throws IOException {
    final NodeMatcher parse = parse( selector );
    return matchAll( context, base, parse );
  }

  public static ReportElement[] findElementsByName( final ReportElement section, final String name ) {
    return findElementsByAttribute( section, AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, name );
  }

  public static ReportElement[]
    findElementsByAttribute( final ReportElement section, final String ns, final String name ) {
    return findElementsByAttribute( section, ns, name, null );
  }

  public static ReportElement[] findElementsByAttribute( final ReportElement section, final String ns,
      final String name, final Object value ) {
    final MatcherContext context = new MatcherContext();
    context.setMatchSubReportChilds( false );

    return matchAll( context, section, new AttributeMatcher( ns, name, value ) );
  }

  public static ReportElement[] findElementsByType( final ReportElement section, final ElementType type ) {
    final MatcherContext context = new MatcherContext();
    context.setMatchSubReportChilds( false );

    final String name = type.getMetaData().getName();
    return matchAll( context, section, new ElementMatcher( name ) );
  }

  public static ReportElement findElementByType( final Element element, final ElementType type ) {
    final MatcherContext context = new MatcherContext();
    context.setMatchSubReportChilds( false );

    final String name = type.getMetaData().getName();
    return match( context, element, new ElementMatcher( name ) );
  }
}
