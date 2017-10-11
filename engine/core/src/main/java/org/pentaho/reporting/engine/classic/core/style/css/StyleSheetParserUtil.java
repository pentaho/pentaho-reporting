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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.DefaultNamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.DefaultNamespaceDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.SelectorList;

import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

public class StyleSheetParserUtil {
  private static final StyleSheetParserUtil instance = new StyleSheetParserUtil();
  private DefaultNamespaceCollection namespaceCollection;

  public static StyleSheetParserUtil getInstance() {
    return instance;
  }

  private StyleSheetParserUtil() {
    namespaceCollection = new DefaultNamespaceCollection();

    addNamespaceDefinition( AttributeNames.Html.NAMESPACE, "html" );
    addNamespaceDefinition( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_CLASS, "core" );
    addNamespaceDefinition( AttributeNames.Pdf.NAMESPACE, "pdf" );
    addNamespaceDefinition( AttributeNames.Crosstab.NAMESPACE, "crosstab" );
    addNamespaceDefinition( AttributeNames.Excel.NAMESPACE, "excel" );
    addNamespaceDefinition( AttributeNames.Swing.NAMESPACE, "action" );
    addNamespaceDefinition( AttributeNames.Internal.NAMESPACE, "internal" );
    addNamespaceDefinition( AttributeNames.Wizard.NAMESPACE, "wizard" );
    addNamespaceDefinition( AttributeNames.Pentaho.NAMESPACE, "pentaho" );
    addNamespaceDefinition( AttributeNames.Table.NAMESPACE, "table" );
    addNamespaceDefinition( AttributeNames.Xml.NAMESPACE, "xml" );
    namespaceCollection.setDefaultNamespaceURI( AttributeNames.Core.NAMESPACE );
  }

  public void addNamespaceDefinition( final String uri, final String preferredPrefix ) {
    namespaceCollection.addDefinition( new DefaultNamespaceDefinition( uri, null, null, null, preferredPrefix ) );
  }

  public void addNamespaceDefinition( final String uri, final String classAttribute, final String preferredPrefix ) {
    namespaceCollection
        .addDefinition( new DefaultNamespaceDefinition( uri, null, classAttribute, null, preferredPrefix ) );
  }

  public void addNamespaceDefinition( final String uri, final ResourceKey defaultStyleSheet,
      final String classAttribute, final String styleAttribute, final String preferredPrefix ) {
    namespaceCollection.addDefinition( new DefaultNamespaceDefinition( uri, defaultStyleSheet, classAttribute,
        styleAttribute, preferredPrefix ) );
  }

  public NamespaceCollection getNamespaceCollection() {
    return namespaceCollection;
  }

  /**
   * Parses a single namespace identifier. This simply splits the given attribute name when a namespace separator is
   * encountered ('|').
   *
   * @param attrName
   *          the attribute name
   * @return the parsed attribute.
   */
  public String[] parseNamespaceIdent( final String attrName, final NamespaceCollection namespaceCollection ) {
    final String name;
    final String namespace;
    final StringTokenizer strtok = new StringTokenizer( attrName, "|" );

    // explicitly undefined is different from default namespace..
    // With that construct I definitely violate the standard, but
    // most stylesheets are not yet written with namespaces in mind
    // (and most tools dont support namespaces in CSS).
    //
    // by acknowledging the explicit rule but redefining the rule where
    // no namespace syntax is used at all, I create compatibility. Still,
    // if the stylesheet does not carry a @namespace rule, this is the same
    // as if the namespace was omitted.
    if ( strtok.countTokens() == 2 ) {
      final String tkNamespace = strtok.nextToken();
      if ( tkNamespace.length() == 0 ) {
        namespace = null;
      } else if ( "*".equals( tkNamespace ) ) {
        namespace = "*";
      } else {
        namespace = namespaceCollection.lookupNamespaceURI( tkNamespace );
      }
      name = strtok.nextToken();
    } else {
      name = strtok.nextToken();
      namespace = null;
    }
    return new String[] { namespace, name };
  }

  public SelectorList parseSelector( final NamespaceCollection nc, final String selectorText ) throws CSSParseException {
    try {
      return CSSParserFactory.getInstance().createCSSParser( nc ).parseSelectors(
          new InputSource( new StringReader( selectorText ) ) );
    } catch ( CSSException e ) {
      throw new CSSParseException( "Failed to parse selector", e );
    } catch ( InstantiationException e ) {
      throw new CSSParseException( "Failed to instantiate CSS parser", e );
    } catch ( IOException e ) {
      throw new CSSParseException( "IO error while parsing selector", e );
    }
  }
}
