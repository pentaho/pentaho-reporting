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

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * A root-handler that intercepts the first call to startElement to select a XmlReadHandler based on the XmlDocumentInfo
 * provided by the parser.
 *
 * @author Thomas Morgner
 */
public class MultiplexRootElementHandler extends RootXmlReadHandler {
  /**
   * A entity resolver that collects information about the DTD used in the document while the underlying parser tries to
   * resolve the DTD into a local InputSource.
   */
  private static class RootEntityResolver implements EntityResolver {
    private ParserEntityResolver entityResolver;
    private String publicId;
    private String systemId;

    /**
     * DefaultConstructor.
     */
    private RootEntityResolver() {
      entityResolver = ParserEntityResolver.getDefaultResolver();
    }

    /**
     * Collects the public and System-ID from the call for later use in the XmlDocumentInfo and then forwards the
     * resolver to the default resolver.
     *
     * @param publicId The public identifier of the external entity being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity being referenced.
     * @return An InputSource object describing the new input source, or null to request that the parser open a regular
     * URI connection to the system identifier.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
     * @throws java.io.IOException      A Java-specific IO exception, possibly the result of creating a new InputStream
     *                                  or Reader for the InputSource.
     * @see org.xml.sax.InputSource
     */
    public InputSource resolveEntity( final String publicId, final String systemId )
      throws SAXException, IOException {
      this.publicId = publicId;
      this.systemId = systemId;
      return entityResolver.resolveEntity( publicId, systemId );
    }

    /**
     * Returns the public ID of the document or null, if the document does not use DTDs.
     *
     * @return the public ID of the documents DTD.
     */
    public String getPublicId() {
      return publicId;
    }

    /**
     * Returns the system ID of the document or null, if the document does not use DTDs.
     *
     * @return the system ID of the documents DTD.
     */
    public String getSystemId() {
      return systemId;
    }

    /**
     * Returns the entity resolver used by this class.
     *
     * @return the entity resolver.
     */
    public ParserEntityResolver getEntityResolver() {
      return entityResolver;
    }
  }

  private XmlFactoryModule[] rootHandlers;
  private RootEntityResolver entityResolver;
  private XmlFactoryModule selectedRootHandler;

  /**
   * Creates a new MultiplexRootElementHandler for the given root handler selection.
   *
   * @param manager      the resource manager that loaded this xml-file.
   * @param source       the source-key that idenfies from where the file was loaded.
   * @param context      the key that should be used to resolve relative paths.
   * @param version      the versioning information for the root-file.
   * @param rootHandlers the roothandlers, never null.
   */
  public MultiplexRootElementHandler
  ( final ResourceManager manager,
    final ResourceKey source,
    final ResourceKey context,
    final long version,
    final XmlFactoryModule[] rootHandlers ) {
    super( manager, source, context, version );
    this.entityResolver = new RootEntityResolver();
    this.rootHandlers = rootHandlers.clone();
  }

  /**
   * Returns the entity resolver used in this handler.
   *
   * @return the entity resolver.
   */
  public EntityResolver getEntityResolver() {
    return entityResolver;
  }

  /**
   * Returns the parent entity resolver used in the element handler. This returns the modifiable entity-resolver
   * backend.
   *
   * @return the entity resolver.
   */
  public ParserEntityResolver getParserEntityResolver() {
    return entityResolver.getEntityResolver();
  }

  /**
   * Returns all known roothandlers.
   *
   * @return the known root handlers.
   */
  protected XmlFactoryModule[] getRootHandlers() {
    return rootHandlers.clone();
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
  protected void interceptFirstStartElement( final String originalUri,
                                             final String localName,
                                             final String qName,
                                             Attributes attributes )
    throws SAXException {
    // build the document info and select the root handler that will
    // deal with the document content.
    final DefaultXmlDocumentInfo documentInfo = new DefaultXmlDocumentInfo();
    documentInfo.setPublicDTDId( entityResolver.getPublicId() );
    documentInfo.setSystemDTDId( entityResolver.getSystemId() );
    documentInfo.setRootElement( localName );
    documentInfo.setRootElementNameSpace( originalUri );
    documentInfo.setRootElementAttributes( attributes );

    final String nsuri = attributes.getValue( "xmlns" );
    if ( nsuri != null ) {
      documentInfo.setDefaultNameSpace( nsuri );
    } else {
      documentInfo.setDefaultNameSpace( "" );
    }

    // ok, now find the best root handler and start parsing ...
    XmlFactoryModule bestRootHandler = null;
    int bestRootHandlerWeight = -1;
    for ( int i = 0; i < rootHandlers.length; i++ ) {
      final XmlFactoryModule rootHandler = rootHandlers[ i ];
      final int weight = rootHandler.getDocumentSupport( documentInfo );
      if ( weight > bestRootHandlerWeight ) {
        bestRootHandler = rootHandler;
        bestRootHandlerWeight = weight;
      }
    }
    if ( bestRootHandlerWeight < 0 || bestRootHandler == null ) {
      throw new NoRootHandlerException( "No suitable root handler known for this document: " + documentInfo );
    }
    final XmlReadHandler readHandler =
      bestRootHandler.createReadHandler( documentInfo );
    if ( readHandler == null ) {
      throw new NoRootHandlerException( "Unable to create the root handler. " + bestRootHandler );
    }
    this.selectedRootHandler = bestRootHandler;

    String defaultNamespace = documentInfo.getDefaultNameSpace();
    if ( defaultNamespace == null || "".equals( defaultNamespace ) ) {
      // Now correct the namespace ..
      defaultNamespace = bestRootHandler.getDefaultNamespace( documentInfo );
      if ( defaultNamespace != null && "".equals( defaultNamespace ) == false ) {
        documentInfo.setRootElementNameSpace( defaultNamespace );
      }
    }

    pushDefaultNamespace( defaultNamespace );

    final String uri;
    if ( ( originalUri == null || "".equals( originalUri ) ) &&
      defaultNamespace != null ) {
      uri = defaultNamespace;
    } else {
      uri = originalUri;
    }

    attributes = new FixNamespaceUriAttributes( uri, attributes );
    installRootHandler( readHandler, uri, localName, wrapAttributes( attributes ) );
  }

  public XmlFactoryModule getSelectedRootHandler() {
    return selectedRootHandler;
  }
}
