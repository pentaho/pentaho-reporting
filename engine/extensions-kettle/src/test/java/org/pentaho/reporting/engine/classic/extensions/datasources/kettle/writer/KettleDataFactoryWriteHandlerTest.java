/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer.KettleDataFactoryWriteHandler;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KettleDataFactoryWriteHandlerTest {

  private static final String FILE_NAME = "test-file.xml";

  private static final String STEP_NAME = "step";

  private static final String QUERY_NAME = "default";

  private static final String REPO_NAME = "repo";

  private static final String TRANS_NAME = "trans";

  private static final String USER_NAME = "user";

  private static final String PASS = "pass";

  private static final String DIRECTORY_NAME = "dir";

  @Test
  public void kettleTransFromRepositoryProducerWritesCorrect() throws Exception {
    AbstractKettleTransformationProducer producer =
        new KettleTransFromRepositoryProducer( REPO_NAME, DIRECTORY_NAME, TRANS_NAME, STEP_NAME, USER_NAME, PASS,
            new FormulaArgument[0], new FormulaParameter[0] );

    Map<String, String> params = new HashMap<String, String>();
    params.put( "name", QUERY_NAME );
    params.put( "repository", REPO_NAME );
    params.put( "directory", DIRECTORY_NAME );
    params.put( "transformation", TRANS_NAME );
    params.put( "step", STEP_NAME );
    params.put( "username", USER_NAME );
    params.put( "password", PASS );

    testAbstractKettleTransformationProducerWritesCorrect( producer, params, "data:query-repository" );
  }

  @Test
  public void kettleDataFactoryWriteHandlerWritesCorrect() throws Exception {
    final AbstractKettleTransformationProducer producer = new KettleTransFromFileProducer( FILE_NAME, STEP_NAME );

    Map<String, String> params = new HashMap<String, String>();
    params.put( "name", QUERY_NAME );
    params.put( "filename", FILE_NAME );
    params.put( "step", STEP_NAME );

    testAbstractKettleTransformationProducerWritesCorrect( producer, params, "data:query-file" );
  }

  private static void testAbstractKettleTransformationProducerWritesCorrect(
      AbstractKettleTransformationProducer producer, Map<String, String> params, String tagName ) throws Exception {
    KettleDataFactoryWriteHandler writeHandler = new KettleDataFactoryWriteHandler();
    ReportWriterContext reportWriterContext = mock( ReportWriterContext.class );

    KettleDataFactory factory = new KettleDataFactory();
    factory.setQuery( QUERY_NAME, producer );

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Writer writer = new PrintWriter( outputStream );
    try {
      XmlWriter xmlWriter = new XmlWriter( writer );
      writeHandler.write( reportWriterContext, xmlWriter, factory );
    } finally {
      writer.close();
    }
    checkParsing( new ByteArrayInputStream( outputStream.toByteArray() ), params, tagName );
  }

  private static void checkParsing( InputStream documentStream, Map<String, String> params, String tagName )
    throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse( documentStream );
    document.getDocumentElement().normalize();

    NodeList nList = document.getElementsByTagName( tagName );
    assertEquals( 1, nList.getLength() );
    Node nNode = nList.item( 0 );

    if ( nNode.getNodeType() == Node.ELEMENT_NODE ) {
      Element element = (Element) nNode;

      for ( Map.Entry<String, String> pair : params.entrySet() ) {
        assertEquals( pair.getValue(), element.getAttribute( pair.getKey() ) );
      }
    }
  }

}
