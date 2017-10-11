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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.css.PageAreaType;
import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.CSSFontFaceRule;
import org.pentaho.reporting.libraries.css.model.CSSMediaRule;
import org.pentaho.reporting.libraries.css.model.CSSPageAreaRule;
import org.pentaho.reporting.libraries.css.model.CSSPageRule;
import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.model.StyleRule;
import org.pentaho.reporting.libraries.css.model.StyleSheet;
import org.pentaho.reporting.libraries.css.selectors.CSSSelector;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Creation-Date: 23.11.2005, 13:06:06
 *
 * @author Thomas Morgner
 */
public class StyleSheetHandler implements DocumentHandler, ErrorHandler {
  private Log logger = LogFactory.getLog( StyleSheetHandler.class );
  private HashMap namespaces;
  private StyleSheet styleSheet;
  private FastStack parentRules;
  private CSSDeclarationRule styleRule;
  private StyleKeyRegistry styleKeyRegistry;
  private ResourceManager resourceManager;
  private ResourceKey source;
  private DependencyCollector dependencies;
  private String defaultNamespace;

  public StyleSheetHandler() {
    this.namespaces = new HashMap();
    this.parentRules = new FastStack();
  }

  public void init( final StyleKeyRegistry styleKeyRegistry,
                    final ResourceManager resourceManager,
                    final ResourceKey source,
                    final long version,
                    final StyleRule parentRule ) {
    if ( styleKeyRegistry == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.styleKeyRegistry = styleKeyRegistry;
    this.resourceManager = resourceManager;
    this.source = source;
    if ( source != null ) {
      this.dependencies = new DependencyCollector( source, version );
    }

    this.parentRules.clear();
    if ( parentRule != null ) {
      parentRules.push( parentRule );
    }

    this.namespaces.clear();
  }

  //  public void init(final ResourceManager manager,
  //                   final ResourceKey source,
  //                   final long version,
  //                   final StyleKeyRegistry registry,
  //                   final StyleRule parentRule)
  //  {
  //    if (registry == null)
  //    {
  //      throw new NullPointerException();
  //    }
  //
  //    this.registry = registry;
  //
  //    this.parentRules.clear();
  //    if (parentRule != null)
  //    {
  //      parentRules.push(parentRule);
  //    }
  //
  //    this.manager = manager;
  //    this.source = source;
  //    if (source != null)
  //    {
  //      this.dependencies = new DependencyCollector(source, version);
  //    }
  //
  //    this.namespaces.clear();
  //  }

  public void registerNamespace( String prefix, String uri ) {
    if ( prefix == null ) {
      throw new NullPointerException();
    }
    if ( uri == null ) {
      throw new NullPointerException();
    }
    namespaces.put( prefix, uri );
  }

  public String getDefaultNamespaceURI() {
    return defaultNamespace;
  }

  public void setDefaultNamespaceURI( final String defaultNamespace ) {
    this.defaultNamespace = defaultNamespace;
  }

  public ResourceKey getSource() {
    return source;
  }

  public DependencyCollector getDependencies() {
    return dependencies;
  }

  public CSSDeclarationRule getStyleRule() {
    return styleRule;
  }

  public void setStyleRule( final CSSDeclarationRule styleRule ) {
    this.styleRule = styleRule;
  }

  public StyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final StyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public void initParseContext( InputSource source ) {
    // the default namespace might be fed from outside ..
    final CSSParserContext parserContext = CSSParserContext.getContext();
    parserContext.setNamespaces( namespaces );
    parserContext.setStyleKeyRegistry( styleKeyRegistry );
    parserContext.setSource( getSource() );
  }

  /**
   * Receive notification of the beginning of a style sheet.
   * <p/>
   * The CSS parser will invoke this method only once, before any other methods in this interface.
   *
   * @param source the input source
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void startDocument( InputSource source )
    throws CSSException {
    initParseContext( source );
    if ( this.styleSheet == null ) {
      this.styleSheet = new StyleSheet();
      this.styleSheet.setSource( getSource() );
    }
  }

  /**
   * Receive notification of the end of a document.
   * <p/>
   * The CSS parser will invoke this method only once, and it will be the last method invoked during the parse. The
   * parser shall not invoke this method until it has either abandoned parsing (because of an unrecoverable error) or
   * reached the end of input.
   *
   * @param source the input source
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void endDocument( InputSource source )
    throws CSSException {
    final Iterator entries = namespaces.entrySet().iterator();
    while ( entries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) entries.next();
      final String prefix = (String) entry.getKey();
      final String uri = (String) entry.getValue();
      styleSheet.addNamespace( prefix, uri );
    }
  }

  /**
   * Receive notification of a comment. If the comment appears in a declaration (e.g. color: /* comment * / blue;), the
   * parser notifies the comment before the declaration.
   *
   * @param text The comment.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void comment( String text )
    throws CSSException {
    // comments are ignored ..
  }

  /**
   * Receive notification of an unknown rule t-rule not supported by this parser.
   *
   * @param atRule The complete ignored at-rule.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void ignorableAtRule( String atRule )
    throws CSSException {
    StringTokenizer strtok = new StringTokenizer( atRule );
    if ( strtok.hasMoreTokens() == false ) {
      return;
    }
    String ruleName = strtok.nextToken();
    if ( ruleName.equalsIgnoreCase( "@namespace" ) ) {
      parseNamespaceRule( strtok );
    } else if ( styleRule instanceof CSSPageRule ) {
      CSSPageRule pageRule = (CSSPageRule) styleRule;
      if ( ruleName.length() <= 1 ) {
        return;
      }
      String areaName = ruleName.substring( 1 );
      final PageAreaType[] pageAreas = PageAreaType.getPageAreas();
      for ( int i = 0; i < pageAreas.length; i++ ) {
        PageAreaType pageArea = pageAreas[ i ];
        if ( areaName.equalsIgnoreCase( pageArea.getName() ) ) {
          final CSSPageAreaRule areaRule = parsePageRule( pageArea, atRule );
          if ( areaRule != null ) {
            pageRule.addRule( areaRule );
          }
          return;
        }
      }
      logger.info( "Did not recognize page @rule: " + atRule );
    } else {
      logger.info( "Ignorable @rule: " + atRule );
    }
  }

  private CSSPageAreaRule parsePageRule( PageAreaType areaType, String atRule ) {
    final ResourceKey source = this.source;
    final CSSPageAreaRule areaRule =
      new CSSPageAreaRule( styleSheet, styleRule, areaType );
    final int firstBrace = atRule.indexOf( '{' );
    final int lastBrace = atRule.indexOf( '}' );
    if ( firstBrace < 0 || lastBrace < firstBrace ) {
      // cannot parse that ..
      return null;
    }

    StyleSheetParserUtil.getInstance().parseStyleRule
      ( namespaces, atRule.substring( firstBrace + 1, lastBrace - 1 ),
        source, areaRule, getResourceManager(), styleKeyRegistry );
    return areaRule;
  }


  private void parseNamespaceRule( final StringTokenizer strtok ) {
    String next = strtok.nextToken();
    final String prefix;
    final String uri;
    if ( next.startsWith( "url(" ) ) {
      prefix = "";
      uri = next;
    } else {
      prefix = next;
      if ( strtok.hasMoreTokens() == false ) {
        return;
      }
      uri = strtok.nextToken();
    }
    int uriStart = uri.indexOf( '(' );
    if ( uriStart == -1 ) {
      return;
    }
    int uriEnd = uri.indexOf( ')' );
    if ( uriEnd == -1 ) {
      return;
    }
    if ( uriStart > uriEnd ) {
      return;
    }
    final String uriValue = uri.substring( uriStart + 1, uriEnd );
    namespaceDeclaration( prefix, uriValue );
  }

  /**
   * Receive notification of an unknown rule t-rule not supported by this parser.
   *
   * @param prefix <code>null</code> if this is the default namespace
   * @param uri    The URI for this namespace.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void namespaceDeclaration( String prefix, String uri )
    throws CSSException {
    if ( prefix == null || "".equals( prefix ) ) {
      this.namespaces.put( "", uri );
      this.defaultNamespace = uri;
      CSSParserContext.getContext().setDefaultNamespace( defaultNamespace );
    } else {
      this.namespaces.put( prefix, uri );
    }
  }

  /**
   * Receive notification of a import statement in the style sheet.
   *
   * @param uri                 The URI of the imported style sheet.
   * @param media               The intended destination media for style information.
   * @param defaultNamespaceURI The default namespace URI for the imported style sheet.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void importStyle( String uri,
                           SACMediaList media,
                           String defaultNamespaceURI )
    throws CSSException {
    //  instantiate a new parser and parse the stylesheet.
    final ResourceManager manager = getResourceManager();
    if ( manager == null ) {
      // there is no source set, so we have no resource manager, and thus
      // we do no parsing.
      //
      // This should only be the case if we parse style-values; in that case
      // include-statement are not supported anyway.
      return;
    }
    try {
      CSSParserContext.getContext().setDefaultNamespace( defaultNamespaceURI );
      final ResourceKey key;
      if ( source == null ) {
        key = manager.createKey( uri );
      } else {
        key = manager.deriveKey( source, uri );
      }

      final Resource res = manager.create( key, source, StyleSheet.class );
      if ( res == null ) {
        return;
      }
      final StyleSheet styleSheet = (StyleSheet) res.getResource();
      this.styleSheet.addStyleSheet( styleSheet );
    } catch ( ResourceException e ) {
      // ignore ..
    } finally {
      CSSParserContext.getContext().setStyleKeyRegistry( styleKeyRegistry );
      CSSParserContext.getContext().setSource( getSource() );
      CSSParserContext.getContext().setNamespaces( namespaces );
      CSSParserContext.getContext().setDefaultNamespace( defaultNamespace );
    }
  }

  /**
   * Receive notification of the beginning of a media statement.
   * <p/>
   * The Parser will invoke this method at the beginning of every media statement in the style sheet. there will be a
   * corresponding endMedia() event for every startElement() event.
   *
   * @param media The intended destination media for style information.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void startMedia( SACMediaList media )
    throws CSSException {
    // ignore for now ..
    styleRule = new CSSMediaRule( styleSheet, getParentRule() );
    parentRules.push( styleRule );

  }

  /**
   * Receive notification of the end of a media statement.
   *
   * @param media The intended destination media for style information.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void endMedia( SACMediaList media )
    throws CSSException {
    parentRules.pop();
    styleSheet.addRule( styleRule );
    styleRule = null;
  }

  /**
   * Receive notification of the beginning of a page statement.
   * <p/>
   * The Parser will invoke this method at the beginning of every page statement in the style sheet. there will be a
   * corresponding endPage() event for every startPage() event.
   *
   * @param name        the name of the page (if any, null otherwise)
   * @param pseudo_page the pseudo page (if any, null otherwise)
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void startPage( String name, String pseudo_page )
    throws CSSException {
    // Log.debug ("Page Rule: " + name + " / " + pseudo_page);
    // yes, we have to parse that.
    styleRule = new CSSPageRule( styleSheet, getParentRule(), name, pseudo_page );
    parentRules.push( styleRule );
  }

  /**
   * Receive notification of the end of a media statement.
   *
   * @param name        The intended destination medium for style information.
   * @param pseudo_page the pseudo page (if any, null otherwise)
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void endPage( String name, String pseudo_page )
    throws CSSException {
    parentRules.pop();
    styleSheet.addRule( styleRule );
    styleRule = null;
  }

  /**
   * Receive notification of the beginning of a font face statement.
   * <p/>
   * The Parser will invoke this method at the beginning of every font face statement in the style sheet. there will be
   * a corresponding endFontFace() event for every startFontFace() event.
   *
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void startFontFace()
    throws CSSException {
    // font-face events are ignored for now.
    styleRule = new CSSFontFaceRule( styleSheet, getParentRule() );
    parentRules.push( styleRule );
  }

  protected StyleRule getParentRule() {
    if ( parentRules.isEmpty() == false ) {
      return (StyleRule) parentRules.peek();
    }
    return null;
  }

  /**
   * Receive notification of the end of a font face statement.
   *
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void endFontFace()
    throws CSSException {
    parentRules.pop();
  }

  /**
   * Receive notification of the beginning of a rule statement.
   *
   * @param selectors All intended selectors for all declarations.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void startSelector( SelectorList selectors )
    throws CSSException {
    styleRule = new CSSStyleRule( styleSheet, getParentRule() );
  }

  /**
   * Receive notification of the end of a rule statement.
   *
   * @param selectors All intended selectors for all declarations.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void endSelector( SelectorList selectors )
    throws CSSException {
    if ( styleRule.isEmpty() ) {
      return;
    }

    int length = selectors.getLength();
    for ( int i = 0; i < length; i++ ) {
      final Selector selector = selectors.item( i );
      try {
        final CSSStyleRule rule = (CSSStyleRule) styleRule.clone();
        rule.setSelector( (CSSSelector) selector );
        styleSheet.addRule( rule );
      } catch ( CloneNotSupportedException e ) {
        // should not happen
      }
    }
  }

  /**
   * Receive notification of a declaration.
   *
   * @param name      the name of the property.
   * @param value     the value of the property. All whitespace are stripped.
   * @param important is this property important ?
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   */
  public void property( String name, LexicalUnit value, boolean important )
    throws CSSException {
    CSSValueFactory factory = CSSParserContext.getContext().getValueFactory();
    try {
      factory.parseValue( styleRule, name, value, important );
    } catch ( Exception e ) {
      // we catch everything.
      logger.warn( "Error parsing style key: " + name, e );
    }

  }

  /**
   * Receive notification of a warning. <p/> <p>CSS parsers will use this method to report conditions that are not
   * errors or fatal errors as defined by the XML 1.0 recommendation.  The default behaviour is to take no action.</p>
   * <p/> <p>The CSS parser must continue to provide normal parsing events after invoking this method: it should still
   * be possible for the application to process the document through to the end.</p>
   *
   * @param exception The warning information encapsulated in a CSS parse exception.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   * @see CSSParseException
   */
  public void warning( CSSParseException exception )
    throws CSSException {
    logger.warn( "Warning: " + exception.getMessage() );
  }

  /**
   * Receive notification of a recoverable error. <p/> <p>This corresponds to the definition of "error" in section 1.2
   * of the W3C XML 1.0 Recommendation.  For example, a validating parser would use this callback to report the
   * violation of a validity constraint.  The default behaviour is to take no action.</p> <p/> <p>The CSS parser must
   * continue to provide normal parsing events after invoking this method: it should still be possible for the
   * application to process the document through to the end.  If the application cannot do so, then the parser should
   * report a fatal error even if the XML 1.0 recommendation does not require it to do so.</p>
   *
   * @param exception The error information encapsulated in a CSS parse exception.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   * @see CSSParseException
   */
  public void error( CSSParseException exception )
    throws CSSException {
    logger.warn( "Error: ", exception );
  }

  /**
   * Receive notification of a non-recoverable error. <p/> <p>This corresponds to the definition of "fatal error" in
   * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a parser would use this callback to report the
   * violation of a well-formedness constraint.</p> <p/> <p>The application must assume that the document is unusable
   * after the parser has invoked this method, and should continue (if at all) only for the sake of collecting addition
   * error messages: in fact, CSS parsers are free to stop reporting any other events once this method has been
   * invoked.</p>
   *
   * @param exception The error information encapsulated in a CSS parse exception.
   * @throws CSSException Any CSS exception, possibly wrapping another exception.
   * @see CSSParseException
   */
  public void fatalError( CSSParseException exception )
    throws CSSException {
    logger.warn( "Fatal Error: ", exception );
  }
}
