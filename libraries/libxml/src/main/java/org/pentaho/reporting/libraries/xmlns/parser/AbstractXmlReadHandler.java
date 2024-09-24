/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for implementing an {@link org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler}. This class
 * takes care of all the delegation management.
 */
public abstract class AbstractXmlReadHandler implements XmlReadHandler {
  private static final Log logger = LogFactory.getLog( AbstractXmlReadHandler.class );

  /**
   * The root handler.
   */
  private RootXmlReadHandler rootHandler;

  /**
   * The tag name.
   */
  private String tagName;

  /**
   * THe namespace URI.
   */
  private String uri;

  /**
   * A flag indicating the first call.
   */
  private boolean firstCall;

  /**
   * Creates a new handler.
   */
  protected AbstractXmlReadHandler() {
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler the root handler.
   * @param tagName     the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler,
                    final String uri,
                    final String tagName ) throws SAXException {
    if ( rootHandler == null ) {
      throw new NullPointerException( "Root handler must not be null" );
    }
    if ( tagName == null ) {
      throw new NullPointerException( "Tag name must not be null" );
    }
    this.uri = uri;
    this.rootHandler = rootHandler;
    this.tagName = tagName;
    this.firstCall = true;
  }

  /**
   * This method is called at the start of an element.
   *
   * @param tagName the tag name.
   * @param attrs   the attributes.
   * @throws SAXException if there is a parsing error.
   */
  public final void startElement( final String uri,
                                  final String tagName,
                                  final Attributes attrs )
    throws SAXException {
    if ( this.firstCall ) {
      if ( !this.tagName.equals( tagName ) || !this.uri.equals( uri ) ) {
        throw new ParseException(
          "Expected <" + this.tagName + ">, found <" + tagName + '>', getLocator() );
      }
      this.firstCall = false;
      startParsing( attrs );
    } else {
      final XmlReadHandler childHandler = getHandlerForChild( uri, tagName, attrs );
      if ( childHandler == null ) {
        logger.warn( "Unknown tag <" + uri + ':' + tagName + ">: Start to ignore this element and all of its childs. "
          + getLocatorString() );
        logger.debug( this.getClass() );
        final IgnoreAnyChildReadHandler ignoreAnyChildReadHandler =
          new IgnoreAnyChildReadHandler();
        ignoreAnyChildReadHandler.init( getRootHandler(), uri, tagName );
        this.rootHandler.recurse( ignoreAnyChildReadHandler, uri, tagName, attrs );
      } else {
        childHandler.init( getRootHandler(), uri, tagName );
        this.rootHandler.recurse( childHandler, uri, tagName, attrs );
      }
    }
  }

  /**
   * This method is called to process the character data between element tags.
   *
   * @param ch     the character buffer.
   * @param start  the start index.
   * @param length the length.
   * @throws SAXException if there is a parsing error.
   */
  public void characters( final char[] ch, final int start, final int length )
    throws SAXException {
    // nothing required
  }

  /**
   * This method is called at the end of an element.
   *
   * @param tagName the tag name.
   * @throws SAXException if there is a parsing error.
   */
  public final void endElement( final String uri,
                                final String tagName ) throws SAXException {
    if ( this.tagName.equals( tagName ) && this.uri.equals( uri ) ) {
      doneParsing();
      this.rootHandler.unwind( uri, tagName );
    } else {
      throw new ParseException( "Illegal Parser State." + toString(), getLocator() );
    }
  }

  /**
   * Computes a string containing the current parse location or an empty string if there is no locator.
   *
   * @return the location as debug-text.
   */
  private String getLocatorString() {
    final Locator locator = getLocator();
    if ( locator == null ) {
      return "";
    }
    final StringBuffer message = new StringBuffer( 100 );
    message.append( " [Location: Line=" );
    message.append( locator.getLineNumber() );
    message.append( " Column=" );
    message.append( locator.getColumnNumber() );
    message.append( "] " );
    return message.toString();

  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    // nothing required
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    // nothing required
  }

  /**
   * Checks whether the given url denotes the same namespace as the element's namespace.
   *
   * @param namespaceURI the namespace that should be tested.
   * @return true, if the namespace matches the element's namespace,false otherwise.
   */
  protected boolean isSameNamespace( final String namespaceURI ) {
    return ObjectUtilities.equal( namespaceURI, getUri() );
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts )
    throws SAXException {
    return null;
  }

  /**
   * Returns the tag name.
   *
   * @return the tag name.
   */
  public String getTagName() {
    return this.tagName;
  }

  /**
   * Returns the uri of the element. The URI identifies the namespace.
   *
   * @return the element's URI.
   */
  public String getUri() {
    return uri;
  }

  /**
   * Returns the root handler for the parsing.
   *
   * @return the root handler.
   */
  public RootXmlReadHandler getRootHandler() {
    return this.rootHandler;
  }

  /**
   * Returns the locator as provided by the XML parser. This method may return null if the XML parser has no locator
   * support.
   *
   * @return the locator or null.
   */
  public Locator getLocator() {
    return rootHandler.getDocumentLocator();
  }

  /**
   * Parses an external file using LibLoader and returns the parsed result as an object of type
   * <code>targetClass</code>. The file is given as relative pathname (relative to the current source file). The current
   * helper-methods are used as parse-parameters for the external parsing.
   *
   * @param file        the file to be parsed.
   * @param targetClass the target type of the parse operation.
   * @return the result, never null.
   * @throws ParseException           if parsing the result failed for some reason.
   * @throws ResourceLoadingException if there was an IO error loading the resource.
   * @see #deriveParseParameters()
   * @see #performExternalParsing(String, Class, Map)
   */
  protected Object performExternalParsing( final String file, final Class targetClass )
    throws ParseException, ResourceLoadingException {
    return performExternalParsing( file, targetClass, deriveParseParameters() );
  }

  /**
   * Parses an external file using LibLoader and returns the parsed result as an object of type
   * <code>targetClass</code>. The file is given as relative pathname (relative to the current source file). The current
   * helper-methods are used as parse-parameters for the external parsing.
   *
   * @param file        the file to be parsed.
   * @param targetClass the target type of the parse operation.
   * @param map         the map of parse parameters.
   * @return the result, never null.
   * @throws ParseException           if parsing the result failed for some reason.
   * @throws ResourceLoadingException if there was an IO error loading the resource.
   * @see #deriveParseParameters()
   */
  protected Object performExternalParsing( final String file, final Class targetClass, final Map map )
    throws ParseException, ResourceLoadingException {
    try {
      final ResourceManager resourceManager = rootHandler.getResourceManager();
      final ResourceKey source = rootHandler.getSource();

      final ResourceKey target = resourceManager.deriveKey( source, file, map );
      final DependencyCollector dc = rootHandler.getDependencyCollector();

      final Resource resource = resourceManager.create( target, rootHandler.getContext(), targetClass );
      dc.add( resource );
      return resource.getResource();
    } catch ( ResourceLoadingException rle ) {
      throw rle;
    } catch ( ResourceException e ) {
      throw new ParseException( "Failure while loading data: " + file, e, getLocator() );
    }

  }

  /**
   * Creates a working copy of the current parse state.
   *
   * @return the derived parse-parameters.
   * @noinspection ObjectAllocationInLoop as this is a cloning operation.
   */
  protected Map deriveParseParameters() {
    final RootXmlReadHandler rootHandler = getRootHandler();
    final HashMap map = new HashMap();
    final String[] names = rootHandler.getHelperObjectNames();
    final int length = names.length;
    for ( int i = 0; i < length; i++ ) {
      final String name = names[ i ];
      final FactoryParameterKey key = new FactoryParameterKey( name );
      map.put( key, rootHandler.getHelperObject( name ) );
    }
    return map;
  }
}
