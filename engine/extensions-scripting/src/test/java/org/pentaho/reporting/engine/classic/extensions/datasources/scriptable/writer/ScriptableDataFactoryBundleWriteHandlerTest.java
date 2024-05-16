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
 * Copyright (c) 2002-2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.writer;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScriptableDataFactoryBundleWriteHandlerTest {

  private ScriptableDataFactoryBundleWriteHandler handler = spy( new ScriptableDataFactoryBundleWriteHandler() );

  @Test
  public void testWriteDataFactory() throws IOException, BundleWriterException {
    WriteableDocumentBundle bundle = mock( WriteableDocumentBundle.class );
    ScriptableDataFactory dataFactory = mock( ScriptableDataFactory.class );
    BundleWriterState state = mock( BundleWriterState.class );
    XmlWriter xmlWriter = mock( XmlWriter.class );

    doReturn( "/" ).when( state ).getFileName();
    doReturn( xmlWriter ).when( handler )
        .createXmlWriter( Mockito.any(), Mockito.any() );
    doReturn( new String[] { "test_query_name" } ).when( dataFactory ).getQueryNames();
    doReturn( "test_query" ).when( dataFactory ).getQuery( "test_query_name" );

    doReturn( "lang_val" ).when( dataFactory ).getLanguage();
    doReturn( "script_val" ).when( dataFactory ).getScript();
    doReturn( "shutdown_script_val" ).when( dataFactory ).getShutdownScript();

    String fileName = handler.writeDataFactory( bundle, dataFactory, state );

    assertThat( fileName, is( equalTo( "datasources/scriptable-ds.xml" ) ) );

    ArgumentCaptor<AttributeList> rootAttrsCaptor = ArgumentCaptor.forClass( AttributeList.class );
    ArgumentCaptor<String> namespaceCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<Boolean> flagCaptor = ArgumentCaptor.forClass( Boolean.class );

    verify( xmlWriter, times( 2 ) ).writeTag( namespaceCaptor.capture(), nameCaptor.capture(),
        rootAttrsCaptor.capture(), flagCaptor.capture() );

    List<String> namespaceResults = namespaceCaptor.getAllValues();
    assertThat( namespaceResults.get( 0 ), is( equalTo( ScriptableDataFactoryModule.NAMESPACE ) ) );
    assertThat( namespaceResults.get( 1 ), is( equalTo( ScriptableDataFactoryModule.NAMESPACE ) ) );

    List<String> nameResults = nameCaptor.getAllValues();
    assertThat( nameResults.get( 0 ), is( equalTo( "scriptable-datasource" ) ) );
    assertThat( nameResults.get( 1 ), is( equalTo( "config" ) ) );

    List<AttributeList> rootAttrResults = rootAttrsCaptor.getAllValues();
    assertThat( rootAttrResults.get( 0 ).getAttribute( "http://www.w3.org/2000/xmlns/", "data" ),
        is( equalTo( ScriptableDataFactoryModule.NAMESPACE ) ) );
    assertThat( rootAttrResults.get( 1 ).getAttribute( ScriptableDataFactoryModule.NAMESPACE, "language" ),
        is( equalTo( "lang_val" ) ) );
    assertThat( rootAttrResults.get( 1 ).getAttribute( ScriptableDataFactoryModule.NAMESPACE, "script" ),
        is( equalTo( "script_val" ) ) );
    assertThat( rootAttrResults.get( 1 ).getAttribute( ScriptableDataFactoryModule.NAMESPACE, "shutdown-script" ),
        is( equalTo( "shutdown_script_val" ) ) );

    List<Boolean> flagResults = flagCaptor.getAllValues();
    assertThat( flagResults.get( 0 ), is( equalTo( XmlWriter.OPEN ) ) );
    assertThat( flagResults.get( 1 ), is( equalTo( XmlWriterSupport.CLOSE ) ) );

    verify( xmlWriter, times( 1 ) ).writeTextNormalized( "test_query", false );
    verify( xmlWriter, times( 2 ) ).writeCloseTag();
    verify( xmlWriter, times( 1 ) ).close();
  }
}
