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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.pentaho.reporting.libraries.xmlns.parser.LoggingErrorHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DocumentHelper {
  private DocumentHelper() {
  }

  public static Document loadDocumentFromBytes( final byte[] bytes ) throws KettlePluginException {
    return loadDocument( new ByteArrayInputStream( bytes ) );
  }

  public static Document loadDocumentFromPlugin( String id )
    throws KettlePluginException {

    EmbeddedKettleDataFactoryMetaData md =
      (EmbeddedKettleDataFactoryMetaData) DataFactoryRegistry.getInstance().getMetaData( id );
    final InputStream in = new ByteArrayInputStream( md.getBytes() );

    return loadDocument( in );
  }

  public static Document loadDocument( final InputStream in ) throws KettlePluginException {
    try {
      final DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
      dbf.setNamespaceAware( true );
      dbf.setValidating( false );

      final DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver( ParserEntityResolver.getDefaultResolver() );
      db.setErrorHandler( new LoggingErrorHandler() );
      final InputSource input = new InputSource( in );
      return db.parse( input );
    } catch ( ParserConfigurationException e ) {
      throw new KettlePluginException( "Unable to initialize the XML-Parser", e );
    } catch ( SAXException e ) {
      throw new KettlePluginException( "Unable to parse the document.", e );
    } catch ( IOException e ) {
      throw new KettlePluginException( "Unable to read the document from stream.", e );
    } finally {
      try {
        in.close();
      } catch ( IOException e ) {
        // ignored ..
      }
    }
  }
}
