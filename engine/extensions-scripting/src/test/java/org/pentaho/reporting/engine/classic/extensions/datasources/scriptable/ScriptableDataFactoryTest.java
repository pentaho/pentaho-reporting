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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.table.TableModel;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

public class ScriptableDataFactoryTest {

  private static final String QUERY_NAME = "name";
  private static final String QUERY_VALUE = "value";
  private static final String LANGUAGE = "lang";

  @Test
  public void testSetQuery() {
    ScriptableDataFactory dataFactory = new ScriptableDataFactory();

    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    assertThat( dataFactory.getQuery( QUERY_NAME ), is( equalTo( QUERY_VALUE ) ) );

    dataFactory.setQuery( QUERY_NAME, null );
    assertThat( dataFactory.getQuery( QUERY_NAME ), is( nullValue() ) );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testQueryDataReportDataFactoryException() throws Exception {
    ScriptableDataFactory dataFactory = new ScriptableDataFactory();
    dataFactory.queryData( QUERY_NAME, null );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testQueryDataBSFException() throws Exception {
    ScriptableDataFactory dataFactory = spy( new ScriptableDataFactory() );
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    dataFactory.setLanguage( LANGUAGE );
    DataRow parameters = mock( DataRow.class );

    doThrow( BSFException.class ).when( dataFactory ).createInterpreter();

    dataFactory.queryData( QUERY_NAME, parameters );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testQueryDataNotTableModel() throws Exception {
    ScriptableDataFactory dataFactory = spy( new ScriptableDataFactory() );
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    dataFactory.setLanguage( LANGUAGE );
    DataRow parameters = mock( DataRow.class );
    BSFManager interpreter = mock( BSFManager.class );

    when( dataFactory.createInterpreter() ).thenReturn( interpreter );
    doReturn( "wrong_type" ).when( interpreter ).eval( LANGUAGE, "expression", 1, 1, QUERY_VALUE );

    dataFactory.queryData( QUERY_NAME, parameters );
  }

  @Test
  public void testQueryData() throws Exception {
    ScriptableDataFactory dataFactory = spy( new ScriptableDataFactory() );
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    dataFactory.setLanguage( LANGUAGE );
    DataRow parameters = mock( DataRow.class );
    BSFManager interpreter = mock( BSFManager.class );
    TableModel tableModel = mock( TableModel.class );

    when( dataFactory.createInterpreter() ).thenReturn( interpreter );
    doReturn( tableModel ).when( interpreter ).eval( LANGUAGE, "expression", 1, 1, QUERY_VALUE );

    TableModel result = dataFactory.queryData( QUERY_NAME, parameters );

    assertThat( result, is( equalTo( tableModel ) ) );
  }

  @Test
  public void testClone() throws Exception {
    ScriptableDataFactory dataFactory = new ScriptableDataFactory();
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );

    ScriptableDataFactory clonedFactory = dataFactory.clone();

    assertThat( clonedFactory, is( notNullValue() ) );
    assertThat( clonedFactory.getQuery( QUERY_NAME ), is( equalTo( QUERY_VALUE ) ) );
  }

  @Test
  public void testClose() throws Exception {
    ScriptableDataFactory dataFactory = spy( new ScriptableDataFactory() );
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    dataFactory.setLanguage( LANGUAGE );
    dataFactory.setShutdownScript( "test_shutdown_script" );

    BSFManager interpreter = mock( BSFManager.class );
    TableModel tableModel = mock( TableModel.class );

    when( dataFactory.createInterpreter() ).thenReturn( interpreter );
    doReturn( tableModel ).when( interpreter ).eval( LANGUAGE, "expression", 1, 1, QUERY_VALUE );
    dataFactory.queryData( QUERY_NAME, null );
    doReturn( null ).when( interpreter ).eval( LANGUAGE, "shutdown-script", 1, 1, "test_shutdown_script" );

    dataFactory.close();
    verify( interpreter ).eval( LANGUAGE, "shutdown-script", 1, 1, "test_shutdown_script" );
  }

  @Test
  public void testIsQueryExecutable() {
    ScriptableDataFactory dataFactory = new ScriptableDataFactory();

    boolean result = dataFactory.isQueryExecutable( QUERY_NAME, null );
    assertThat( result, is( equalTo( false ) ) );

    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    result = dataFactory.isQueryExecutable( QUERY_NAME, null );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testCancelRunningQuery() throws Exception {
    ScriptableDataFactory dataFactory = spy( new ScriptableDataFactory() );
    dataFactory.setQuery( QUERY_NAME, QUERY_VALUE );
    dataFactory.setLanguage( LANGUAGE );

    BSFManager interpreter = mock( BSFManager.class );
    TableModel tableModel = mock( TableModel.class );

    when( dataFactory.createInterpreter() ).thenReturn( interpreter );
    doReturn( tableModel ).when( interpreter ).eval( LANGUAGE, "expression", 1, 1, QUERY_VALUE );
    dataFactory.queryData( QUERY_NAME, null );

    dataFactory.cancelRunningQuery();
    verify( interpreter ).terminate();
  }
}
