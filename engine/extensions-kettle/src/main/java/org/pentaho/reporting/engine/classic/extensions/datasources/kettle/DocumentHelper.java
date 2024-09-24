/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
