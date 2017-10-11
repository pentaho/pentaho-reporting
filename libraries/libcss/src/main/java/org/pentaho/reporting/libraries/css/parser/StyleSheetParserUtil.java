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

package org.pentaho.reporting.libraries.css.parser;

import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.model.StyleSheet;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A helper class that simplifies the parsing of stylesheets.
 *
 * @author Thomas Morgner
 */
public final class StyleSheetParserUtil {
  private static StyleSheetParserUtil singleton;
  private Parser parser;

  public StyleSheetParserUtil() {
  }

  public static synchronized StyleSheetParserUtil getInstance() {
    if ( singleton == null ) {
      singleton = new StyleSheetParserUtil();
    }
    return singleton;
  }

  private void setupNamespaces( final Map namespaces,
                                final StyleSheetHandler handler ) {
    if ( namespaces == null ) {
      return;
    }

    final Iterator entries = namespaces.entrySet().iterator();
    while ( entries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) entries.next();
      final String prefix = (String) entry.getKey();
      final String uri = (String) entry.getValue();
      handler.registerNamespace( prefix, uri );
    }
  }

  /**
   * Parses a single style value for the given key. Returns <code>null</code>, if the key denotes a compound definition,
   * which has no internal representation.
   *
   * @param namespaces an optional map of known namespaces (prefix -> uri)
   * @param key        the stylekey to which the value should be assigned.
   * @param value      the value text
   * @param baseURL    an optional base url
   * @return the parsed value or null, if the value was not valid.
   */
  public CSSValue parseStyleValue( final Map namespaces,
                                   final StyleKey key,
                                   final String value,
                                   final ResourceKey baseURL,
                                   final ResourceManager resourceManager,
                                   final StyleKeyRegistry styleKeyRegistry ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }

    try {
      final Parser parser = getParser();
      synchronized( parser ) {
        final StyleSheetHandler handler = new StyleSheetHandler();
        setupNamespaces( namespaces, handler );

        handler.init( styleKeyRegistry, resourceManager, baseURL, -1, null );

        final InputSource source = new InputSource();
        source.setCharacterStream( new StringReader( value ) );

        handler.initParseContext( source );
        handler.setStyleRule( new CSSStyleRule( new StyleSheet(), null ) );
        parser.setDocumentHandler( handler );
        final LexicalUnit lu = parser.parsePropertyValue( source );
        handler.property( key.getName(), lu, false );
        final CSSStyleRule rule = (CSSStyleRule) handler.getStyleRule();

        CSSParserContext.getContext().destroy();

        return rule.getPropertyCSSValue( key );
      }
    } catch ( Exception e ) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses a style rule.
   *
   * @param namespaces an optional map of known namespaces (prefix -> uri)
   * @param styleText  the css text that should be parsed
   * @param baseURL    an optional base url
   * @param baseRule   an optional base-rule to which the result gets added.
   * @return the CSS-Style-Rule that contains all values for the given text.
   */
  public CSSDeclarationRule parseStyleRule( final Map namespaces,
                                            final String styleText,
                                            final ResourceKey baseURL,
                                            final CSSDeclarationRule baseRule,
                                            final ResourceManager resourceManager,
                                            final StyleKeyRegistry styleKeyRegistry ) {
    if ( styleText == null ) {
      throw new NullPointerException( "Name is null" );
    }
    if ( resourceManager == null ) {
      throw new NullPointerException( "ResourceManager must not be null" );
    }
    if ( styleKeyRegistry == null ) {
      throw new NullPointerException( "Style-Key Registry must not be null" );
    }

    try {
      final Parser parser = getParser();
      synchronized( parser ) {
        final StyleSheetHandler handler = new StyleSheetHandler();
        setupNamespaces( namespaces, handler );
        handler.init( styleKeyRegistry, resourceManager, baseURL, -1, null );

        final InputSource source = new InputSource();
        source.setCharacterStream( new StringReader( styleText ) );

        handler.initParseContext( source );
        if ( baseRule != null ) {
          handler.setStyleRule( baseRule );
        } else {
          handler.setStyleRule( new CSSStyleRule( new StyleSheet(), null ) );
        }
        parser.setDocumentHandler( handler );
        parser.parseStyleDeclaration( source );
        final CSSDeclarationRule rule = handler.getStyleRule();
        CSSParserContext.getContext().destroy();
        return rule;
      }
    } catch ( Exception e ) {
      e.printStackTrace();
      return null;
    }
  }


  /**
   * Parses a style value. If the style value is a compound key, the corresonding style entries will be added to the
   * style rule.
   *
   * @param namespaces an optional map of known namespaces (prefix -> uri)
   * @param name       the stylekey-name to which the value should be assigned.
   * @param value      the value text
   * @param baseURL    an optional base url
   * @return the CSS-Style-Rule that contains all values for the given text.
   */
  public CSSStyleRule parseStyles( final Map namespaces,
                                   final String name,
                                   final String value,
                                   final ResourceKey baseURL,
                                   final ResourceManager resourceManager,
                                   final StyleKeyRegistry styleKeyRegistry ) {
    final CSSStyleRule cssStyleRule = new CSSStyleRule( new StyleSheet(), null );
    return parseStyles( namespaces, name, value, baseURL, cssStyleRule, resourceManager, styleKeyRegistry );
  }


  /**
   * Parses a style value. If the style value is a compound key, the corresonding style entries will be added to the
   * style rule.
   *
   * @param namespaces an optional map of known namespaces (prefix -> uri)
   * @param name       the stylekey-name to which the value should be assigned.
   * @param value      the value text
   * @param baseURL    an optional base url
   * @param baseRule   an optional base-rule to which the result gets added.
   * @return the CSS-Style-Rule that contains all values for the given text.
   */
  public CSSStyleRule parseStyles( final Map namespaces,
                                   final String name,
                                   final String value,
                                   final ResourceKey baseURL,
                                   final CSSDeclarationRule baseRule,
                                   final ResourceManager resourceManager,
                                   final StyleKeyRegistry styleKeyRegistry ) {
    if ( name == null ) {
      throw new NullPointerException( "Name is null" );
    }
    if ( value == null ) {
      throw new NullPointerException( "Value is null" );
    }

    try {
      final Parser parser = getParser();
      synchronized( parser ) {
        final StyleSheetHandler handler = new StyleSheetHandler();
        handler.init( styleKeyRegistry, resourceManager, baseURL, -1, null );

        setupNamespaces( namespaces, handler );
        final InputSource source = new InputSource();
        source.setCharacterStream( new StringReader( value ) );

        handler.initParseContext( source );
        handler.setStyleRule( baseRule );
        parser.setDocumentHandler( handler );
        final LexicalUnit lu = parser.parsePropertyValue( source );
        handler.property( name, lu, false );
        final CSSStyleRule rule = (CSSStyleRule) handler.getStyleRule();

        CSSParserContext.getContext().destroy();
        return rule;
      }
    } catch ( Exception e ) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the initialized parser.
   *
   * @return the parser's local instance.
   * @throws CSSParserInstantiationException if the parser cannot be instantiated.
   */
  private synchronized Parser getParser()
    throws CSSParserInstantiationException {
    if ( parser == null ) {
      parser = CSSParserFactory.getInstance().createCSSParser();
    }
    return parser;
  }

  /**
   * Parses a single namespace identifier. This simply splits the given attribute name when a namespace separator is
   * encountered ('|').
   *
   * @param attrName the attribute name
   * @return the parsed attribute.
   */
  public static String[] parseNamespaceIdent( final String attrName ) {
    final String name;
    final String namespace;
    final StringTokenizer strtok = new StringTokenizer( attrName, "|" );
    final CSSParserContext context = CSSParserContext.getContext();
    // explicitly undefined is different from default namespace..
    // With that construct I definitly violate the standard, but
    // most stylesheets are not yet written with namespaces in mind
    // (and most tools dont support namespaces in CSS).
    //
    // by acknowledging the explicit rule but redefining the rule where
    // no namespace syntax is used at all, I create compatiblity. Still,
    // if the stylesheet does not carry a @namespace rule, this is the same
    // as if the namespace was omited.
    if ( strtok.countTokens() == 2 ) {
      final String tkNamespace = strtok.nextToken();
      if ( tkNamespace.length() == 0 ) {
        namespace = null;
      } else if ( "*".equals( tkNamespace ) ) {
        namespace = "*";
      } else {
        namespace = (String)
          context.getNamespaces().get( tkNamespace );
      }
      name = strtok.nextToken();
    } else {
      name = strtok.nextToken();
      namespace = context.getDefaultNamespace();
    }
    return new String[] { namespace, name };
  }
}
