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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class DomTreeResourceFactory implements ResourceFactory {
  private static class DomResource extends SimpleResource {
    public DomResource( final ResourceKey resourceKey,
                        final Document document,
                        final long version ) {
      super( resourceKey, document, Document.class, version );
    }

    public Object getResource() {
      final Document resource = (Document) super.getResource();
      return resource.cloneNode( true );
    }
  }

  private static final Log logger = LogFactory.getLog( DomTreeResourceFactory.class );

  /**
   * Creates a resource by interpreting the data given in the resource-data object. If additional datastreams need to be
   * parsed, the provided resource manager should be used.
   *
   * @param manager the resource manager used for all resource loading.
   * @param data    the resource-data from where the binary data is read.
   * @param context the resource context used to resolve relative resource paths.
   * @return the parsed result, never null.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceCreationException if the resource could not be
   *                                                                                  parsed due to syntaxctial or
   *                                                                                  logical errors in the data.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException  if the resource could not be
   *                                                                                  accessed from the physical
   *                                                                                  storage.
   */
  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context ) throws ResourceCreationException, ResourceLoadingException {
    final DocumentBuilderFactory dbf;
    try {
      dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
      dbf.setNamespaceAware( true );
      dbf.setValidating( false );
      final DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver( ParserEntityResolver.getDefaultResolver() );
      db.setErrorHandler( new LoggingErrorHandler() );
      final ResourceDataInputSource input = new ResourceDataInputSource( data, manager );
      return new DomResource( data.getKey(), db.parse( input ), data.getVersion( manager ) );
    } catch ( ParserConfigurationException e ) {
      throw new ResourceCreationException( "Unable to initialize the XML-Parser", e );
    } catch ( SAXException e ) {
      throw new ResourceCreationException( "Unable to parse the document: " + data.getKey(), e );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Unable to read the stream from document: " + data.getKey(), e );
    }
  }

  /**
   * Initializes the resource factory. This usually loads all system resources from the environment and maybe sets up
   * and initializes any factories needed during the parsing.
   */
  public void initializeDefaults() {

  }

  /**
   * Returns the expected result type.
   *
   * @return the result type.
   */
  public Class getFactoryType() {
    return Document.class;
  }
}
