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

package org.pentaho.reporting.engine.classic.core.testsupport.selector;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

public class MatchFactory {
  private static class NodeIterator extends IterateSimpleStructureProcessStep {
    private boolean singleResult;
    private ArrayList<RenderNode> nodes;
    private NodeMatcher matcher;

    private NodeIterator() {
      nodes = new ArrayList<RenderNode>();
    }

    public RenderNode get( final RenderNode node, final NodeMatcher matcher ) {
      if ( node == null ) {
        throw new NullPointerException();
      }
      if ( matcher == null ) {
        throw new NullPointerException();
      }

      this.matcher = matcher;
      this.nodes.clear();
      this.singleResult = true;
      startProcessing( node );
      if ( this.nodes.isEmpty() ) {
        return null;
      }
      return this.nodes.get( 0 );
    }

    public RenderNode[] getAll( final RenderNode node, final NodeMatcher matcher ) {
      if ( node == null ) {
        throw new NullPointerException();
      }
      if ( matcher == null ) {
        throw new NullPointerException();
      }

      this.matcher = matcher;
      this.nodes.clear();
      this.singleResult = false;
      startProcessing( node );
      return this.nodes.toArray( new RenderNode[this.nodes.size()] );
    }

    protected boolean startBox( final RenderBox box ) {
      if ( singleResult && nodes.isEmpty() == false ) {
        return false;
      }

      if ( matcher.matches( box ) ) {
        nodes.add( box );
        if ( singleResult ) {
          return false;
        }
      }
      return true;
    }

    protected void processOtherNode( final RenderNode node ) {
      if ( singleResult && nodes.isEmpty() == false ) {
        return;
      }

      if ( matcher.matches( node ) ) {
        nodes.add( node );
      }
    }
  }

  public enum Type {
    Start, Descendant, Child, Id, Class, Element
  }

  private static ElementMatcher createMatcher( StreamTokenizer tokenizer ) {
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

  public static RenderNode match( final RenderNode base, final NodeMatcher parse ) {
    if ( parse == null ) {
      throw new NullPointerException();
    }
    return new NodeIterator().get( base, parse );
  }

  public static RenderNode[] matchAll( final RenderNode base, final NodeMatcher parse ) {
    if ( parse == null ) {
      throw new NullPointerException();
    }
    return new NodeIterator().getAll( base, parse );
  }

  public static RenderNode match( final RenderNode base, final String selector ) throws IOException {
    final NodeMatcher parse = parse( selector );
    return match( base, parse );
  }

  public static RenderNode[] matchAll( final RenderNode base, final String selector ) throws IOException {
    final NodeMatcher parse = parse( selector );
    return matchAll( base, parse );
  }

  public static RenderNode findElementByName( final RenderNode section, final String name ) {
    final RenderNode[] retval =
        findElementsByAttribute( section, AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, name );
    if ( retval.length == 0 ) {
      return null;
    }
    return retval[0];
  }

  public static RenderNode[] findElementsByName( final RenderNode section, final String name ) {
    return findElementsByAttribute( section, AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, name );
  }

  public static RenderNode[] findElementsByAttribute( final RenderNode section, final String ns, final String name,
      final Object value ) {
    return matchAll( section, new AttributeMatcher( ns, name, value ) );
  }

  public static RenderNode[] findElementsByElementType( final RenderNode section, final ElementType type ) {
    return matchAll( section, new ElementTypeMatcher( type.getMetaData().getName() ) );
  }

  public static RenderNode[] findElementsByNodeType( final RenderNode section, final int type ) {
    return matchAll( section, new RenderNodeTypeMatcher( type ) );
  }

  /*
   * public static void main(final String[] args) throws IOException { System.out.println(new
   * MatchFactory().parse("Test Element > test .c#i"));
   * 
   * final AndMatcher a1 = new AndMatcher(new ElementMatcher("p"), new DescendantMatcher(new AndMatcher(new
   * ElementMatcher("div"), new DescendantMatcher(new ElementMatcher("body"))))); System.out.println(a1); }
   */
}
