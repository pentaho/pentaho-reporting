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

import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * A base root SAX handler.
 *
 * @author Peter Becker
 * @author Thomas Morgner
 */
public class RootXmlReadHandler extends DefaultHandler {
  /**
   * Storage for the parser configuration.
   */
  private DefaultConfiguration parserConfiguration;

  /**
   * The DocumentLocator can be used to resolve the current parse position.
   */
  private Locator documentLocator;

  /**
   * The current handlers.
   */
  private FastStack<XmlReadHandler> currentHandlers;

  /**
   * The list of parent handlers.
   */
  private FastStack<FastStack<XmlReadHandler>> outerScopes;

  /**
   * The root handler.
   */
  private XmlReadHandler rootHandler;

  /**
   * The object registry.
   */
  private HashMap<String, Object> objectRegistry;

  /**
   * A flag indicating whether this handler has initialized the root-element.
   */
  private boolean rootHandlerInitialized;

  /**
   * The current comment handler used to receive xml comments.
   */
  private CommentHandler commentHandler;

  private DependencyCollector dependencyCollector;
  private ResourceKey source;
  private ResourceKey context;
  private ResourceManager manager;
  private FastStack<String> namespaces;
  private boolean firstCall;
  private boolean xmlnsUrisNotAvailable;

  /**
   * Creates a new root-handler using the given versioning information and resource-manager.
   *
   * @param manager the resource manager that loaded this xml-file.
   * @param source  the source-key that identifies from where the file was loaded.
   * @param version the versioning information for the root-file.
   */
  public RootXmlReadHandler( final ResourceManager manager,
                             final ResourceKey source,
                             final long version ) {
    this( manager, source, source, version );
  }

  /**
   * Creates a new root-handler using the given versioning information and resource-manager.
   *
   * @param manager the resource manager that loaded this xml-file.
   * @param source  the source-key that identifies from where the file was loaded.
   * @param context the key that should be used to resolve relative paths.
   * @param version the versioning information for the root-file.
   */
  public RootXmlReadHandler( final ResourceManager manager,
                             final ResourceKey source,
                             final ResourceKey context,
                             final long version ) {
    if ( manager == null ) {
      throw new NullPointerException();
    }
    if ( source == null ) {
      throw new NullPointerException();
    }
    this.firstCall = true;
    this.manager = manager;
    this.source = source;
    this.context = context;
    this.dependencyCollector = new DependencyCollector( source, version );
    this.objectRegistry = new HashMap<String, Object>();
    this.parserConfiguration = new DefaultConfiguration();
    this.commentHandler = new CommentHandler();
    this.namespaces = new FastStack<String>();
  }

  /**
   * Returns the context key. This key may specify a base context for loading resources. (It behaves like the 'base-url'
   * setting of HTML and allows to reference external resources as relative paths without being bound to the original
   * location of the xml file.)
   *
   * @return the context.
   */
  public ResourceKey getContext() {
    return context;
  }

  /**
   * Returns the resource-manager that is used to load external resources.
   *
   * @return the resource-manager.
   */
  public ResourceManager getResourceManager() {
    return manager;
  }

  /**
   * Checks, whether this is the first call to the handler.
   *
   * @return true, if this is the first call, false otherwise.
   */
  public boolean isFirstCall() {
    return firstCall;
  }

  /**
   * Returns the source key. This key points to the file or stream that is currently parsed.
   *
   * @return the source key.
   */
  public ResourceKey getSource() {
    return source;
  }

  /**
   * Returns the current dependency collector for this parse-operation. The Collector allows to check compound-keys for
   * changes.
   *
   * @return the dependency collector.
   */
  public DependencyCollector getDependencyCollector() {
    return dependencyCollector;
  }

  /**
   * Returns the comment handler that is used to collect comments.
   *
   * @return the comment handler.
   */
  public CommentHandler getCommentHandler() {
    return this.commentHandler;
  }

  /**
   * Returns the parser-configuration. This can be use to configure the parsing process.
   *
   * @return the parser's configuration.
   */
  public DefaultConfiguration getParserConfiguration() {
    return parserConfiguration;
  }

  /**
   * Receive an object for locating the origin of SAX document events.
   * <p/>
   * The documentLocator allows the application to determine the end position of any document-related event, even if the
   * parser is not reporting an error. Typically, the application will use this information for reporting its own errors
   * (such as character content that does not match an application's business rules). The information returned by the
   * documentLocator is probably not sufficient for use with a search engine.
   *
   * @param locator the documentLocator.
   */
  public void setDocumentLocator( final Locator locator ) {
    this.documentLocator = locator;
  }

  /**
   * Returns the current documentLocator.
   *
   * @return the documentLocator.
   */
  public Locator getDocumentLocator() {
    return this.documentLocator;
  }

  /**
   * Adds an object to the registry.
   *
   * @param key   the key.
   * @param value the object.
   */
  public void setHelperObject( final String key, final Object value ) {
    if ( value == null ) {
      this.objectRegistry.remove( key );
    } else {
      this.objectRegistry.put( key, value );
    }
  }

  /**
   * Returns an object from the registry.
   *
   * @param key the key.
   * @return The object.
   */
  public Object getHelperObject( final String key ) {
    return this.objectRegistry.get( key );
  }

  /**
   * Returns the array of all currently registered helper-objects. Helper objects are used as simple communication
   * process between the various handler implementations.
   *
   * @return the helper object names.
   */
  public String[] getHelperObjectNames() {
    return this.objectRegistry.keySet().toArray( new String[ objectRegistry.size() ] );
  }

  /**
   * Sets the root SAX handler.
   *
   * @param handler the SAX handler.
   */
  protected void setRootHandler( final XmlReadHandler handler ) {
    if ( handler == null ) {
      throw new NullPointerException();
    }
    this.rootHandler = handler;
    this.rootHandlerInitialized = false;
  }

  /**
   * Returns the root SAX handler.
   *
   * @return the root SAX handler.
   */
  protected XmlReadHandler getRootHandler() {
    return this.rootHandler;
  }

  /**
   * Start a new handler stack and delegate to another handler.
   *
   * @param handler the handler.
   * @param uri     the namespace uri of the current tag.
   * @param tagName the tag name.
   * @param attrs   the attributes.
   * @throws SAXException if there is a problem with the parser.
   */
  public void recurse( final XmlReadHandler handler,
                       final String uri,
                       final String tagName,
                       final Attributes attrs )
    throws SAXException {
    if ( handler == null ) {
      throw new NullPointerException();
    }

    this.outerScopes.push( this.currentHandlers );
    this.currentHandlers = new FastStack<XmlReadHandler>();
    this.currentHandlers.push( handler );
    handler.startElement( uri, tagName, attrs );

  }

  /**
   * Delegate to another handler.
   *
   * @param handler the new handler.
   * @param tagName the tag name.
   * @param uri     the namespace uri of the current tag.
   * @param attrs   the attributes.
   * @throws SAXException if there is a problem with the parser.
   */
  public void delegate( final XmlReadHandler handler,
                        final String uri,
                        final String tagName,
                        final Attributes attrs )
    throws SAXException {
    if ( handler == null ) {
      throw new NullPointerException();
    }
    this.currentHandlers.push( handler );
    handler.init( this, uri, tagName );
    handler.startElement( uri, tagName, attrs );
  }

  /**
   * Hand control back to the previous handler.
   *
   * @param tagName the tagname.
   * @param uri     the namespace uri of the current tag.
   * @throws SAXException if there is a problem with the parser.
   */
  public void unwind( final String uri, final String tagName )
    throws SAXException {
    // remove current handler from stack ..
    this.currentHandlers.pop();
    if ( this.currentHandlers.isEmpty() && !this.outerScopes.isEmpty() ) {
      // if empty, but "recurse" had been called, then restore the old handler stack ..
      // but do not end the recursed element ..
      this.currentHandlers = this.outerScopes.pop();
    } else if ( !this.currentHandlers.isEmpty() ) {
      // if there are some handlers open, close them too (these handlers must be delegates)..
      getCurrentHandler().endElement( uri, tagName );
    }
  }

  /**
   * Returns the current handler.
   *
   * @return The current handler.
   */
  protected XmlReadHandler getCurrentHandler() {
    return this.currentHandlers.peek();
  }

  /**
   * Starts processing a document.
   *
   * @throws SAXException not in this implementation.
   */
  public void startDocument() throws SAXException {
    this.outerScopes = new FastStack<FastStack<XmlReadHandler>>();
    this.currentHandlers = new FastStack<XmlReadHandler>();
    if ( rootHandler != null ) {
      // When dealing with the multiplexing beast, we cant define a
      // root handler unless we've seen the first element and all its
      // namespace declarations ...
      this.currentHandlers.push( this.rootHandler );
    }
  }

  /**
   * Starts processing an element.
   *
   * @param originalUri the URI.
   * @param localName   the local name.
   * @param qName       the qName.
   * @param attributes  the attributes.
   * @throws SAXException if there is a parsing problem.
   */
  public final void startElement( final String originalUri,
                                  final String localName,
                                  final String qName,
                                  final Attributes attributes )
    throws SAXException {
    // Check the default-namespace ..
    if ( firstCall ) {
      firstCall = false;
      interceptFirstStartElement( originalUri, localName, qName, attributes );
      return;
    }

    final String defaultNamespace;
    final String nsuri = attributes.getValue( "xmlns" );
    if ( nsuri != null ) {
      defaultNamespace = nsuri;
    } else if ( namespaces.isEmpty() ) {
      defaultNamespace = "";
    } else {
      defaultNamespace = namespaces.peek();
    }

    pushDefaultNamespace( defaultNamespace );

    final String uri;
    if ( ( originalUri == null || "".equals( originalUri ) ) &&
      defaultNamespace != null ) {
      uri = defaultNamespace;
    } else {
      uri = originalUri;
    }

    if ( rootHandlerInitialized == false ) {
      rootHandler.init( this, uri, localName );
      rootHandlerInitialized = true;
    }

    final XmlReadHandler currentHandler = getCurrentHandler();
    currentHandler.startElement( uri, localName,
      wrapAttributes( new FixNamespaceUriAttributes( uri, attributes ) ) );
  }

  protected Attributes wrapAttributes( final Attributes attributes ) {
    return attributes;
  }

  /**
   * A helper call that allows to override the first call to the startElememt method. This allows the implementation of
   * an multiplexing parser, which requires the information from the root-level elements.
   *
   * @param uri        the namespace uri of the current tag.
   * @param localName  the unqualified tag-name.
   * @param qName      the qualified tag-name.
   * @param attributes the attributes of the current element.
   * @throws SAXException if something goes wrong.
   */
  protected void interceptFirstStartElement( final String uri,
                                             final String localName,
                                             final String qName,
                                             final Attributes attributes )
    throws SAXException {
    startElement( uri, localName, qName, attributes );
  }

  /**
   * Updates the current default namespace.
   *
   * @param nsuri the uri of the current namespace.
   */
  protected final void pushDefaultNamespace( final String nsuri ) {
    namespaces.push( nsuri );
  }

  /**
   * Sets and configures the root handle for the given root-level element.
   *
   * @param handler    the read handler for the root element.
   * @param uri        the uri of the root elements namespace.
   * @param localName  the local tagname of the root element.
   * @param attributes the attributes of the root element.
   * @throws SAXException if something goes wrong.
   */
  protected void installRootHandler( final XmlReadHandler handler,
                                     final String uri,
                                     final String localName,
                                     final Attributes attributes )
    throws SAXException {
    if ( handler == null ) {
      throw new NullPointerException();
    }
    this.rootHandler = handler;
    this.rootHandler.init( this, uri, localName );
    this.currentHandlers.push( handler );
    this.rootHandlerInitialized = true;
    this.rootHandler.startElement( uri, localName, attributes );
  }

  /**
   * Process character data.
   *
   * @param ch     the character buffer.
   * @param start  the start index.
   * @param length the length of the character data.
   * @throws SAXException if there is a parsing error.
   */
  public void characters( final char[] ch, final int start, final int length )
    throws SAXException {
    try {
      getCurrentHandler().characters( ch, start, length );
    } catch ( SAXException se ) {
      throw se;
    } catch ( Exception e ) {
      throw new ParseException
        ( "Failed at handling character data", e, getDocumentLocator() );
    }
  }

  /**
   * Finish processing an element.
   *
   * @param originalUri the URI.
   * @param localName   the local name.
   * @param qName       the qName.
   * @throws SAXException if there is a parsing error.
   */
  public final void endElement( final String originalUri,
                                final String localName,
                                final String qName )
    throws SAXException {
    final String defaultNamespace = namespaces.pop();
    final String uri;
    if ( ( originalUri == null || "".equals( originalUri ) ) &&
      defaultNamespace != null ) {
      uri = defaultNamespace;
    } else {
      uri = originalUri;
    }

    final XmlReadHandler currentHandler = getCurrentHandler();
    currentHandler.endElement( uri, localName );
  }

  /**
   * Tries to return the parse-result of the selected root-handler.
   *
   * @return the parse-result.
   * @throws SAXException if an error occurs.
   */
  public Object getResult() throws SAXException {
    if ( this.rootHandler != null ) {
      return this.rootHandler.getObject();
    }
    return null;
  }

  public EntityResolver getEntityResolver() {
    return this;
  }


  /**
   * Returns, whether the parser resolves namespace-URIs.
   *
   * @return true, if the parser will *NOT* resolve namespaces, false otherwise.
   */
  public boolean isXmlnsUrisNotAvailable() {
    return xmlnsUrisNotAvailable;
  }

  /**
   * Sets a hint that the parser will not be able to return URIs for XML-Namespaces. You should not see this nowadays,
   * as all the common JAXP-parser implementations seem to work fine with namespaces.
   *
   * @param xmlnsUrisNotAvailable a flag indicating that the XML parser has troubles resolving namespaces.
   */
  public void setXmlnsUrisNotAvailable( final boolean xmlnsUrisNotAvailable ) {
    this.xmlnsUrisNotAvailable = xmlnsUrisNotAvailable;
  }

}
