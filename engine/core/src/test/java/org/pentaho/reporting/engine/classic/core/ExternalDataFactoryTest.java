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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;

public class ExternalDataFactoryTest {

  private static final String QUERY = "test_query";

  private ExternalDataFactory factory;

  @Before
  public void setUp() {
    factory = new ExternalDataFactory();
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testQueryDataException() throws ReportDataFactoryException {
    DataRow parameters = mock( DataRow.class );
    doReturn( "incorrect_type" ).when( parameters ).get( QUERY );
    factory.queryData( QUERY, parameters );
  }

  @Test
  public void testQueryData() throws ReportDataFactoryException {
    DataRow parameters = mock( DataRow.class );
    TableModel model = mock( TableModel.class );
    doReturn( model ).when( parameters ).get( QUERY );
    TableModel result = factory.queryData( QUERY, parameters );
    assertThat( result, is( equalTo( model ) ) );
  }

  @Test
  public void testDerive() {
    DataFactory result = factory.derive();
    assertThat( result, is( instanceOf( ExternalDataFactory.class ) ) );
    assertThat( (ExternalDataFactory) result, is( equalTo( factory ) ) );
    assertThat( (ExternalDataFactory) result, is( sameInstance( factory ) ) );
  }

  @Test
  public void testIsQueryExecutable() {
    DataRow parameters = mock( DataRow.class );
    TableModel model = mock( TableModel.class );
    doReturn( model ).when( parameters ).get( QUERY );
    boolean result = factory.isQueryExecutable( QUERY, parameters );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetQueryNames() {
    String[] result = factory.getQueryNames();
    assertThat( result, is( equalTo( new String[] {} ) ) );
  }

  @Test
  public void testCalculateQueryLimit() {
    DataRow parameters = mock( DataRow.class );
    doReturn( "10" ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    int result = factory.calculateQueryLimit( parameters );
    assertThat( result, is( equalTo( -1 ) ) );

    doReturn( 10 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    result = factory.calculateQueryLimit( parameters );
    assertThat( result, is( equalTo( 10 ) ) );
  }

  @Test
  public void testCalculateQueryTimeOut() {
    DataRow parameters = mock( DataRow.class );
    doReturn( "10" ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    int result = factory.calculateQueryTimeOut( parameters );
    assertThat( result, is( equalTo( -1 ) ) );

    doReturn( 10 ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    result = factory.calculateQueryTimeOut( parameters );
    assertThat( result, is( equalTo( 10 ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testInitializeException() throws ReportDataFactoryException {
    factory.initialize( null );
  }

  @Test
  public void testInitialize() throws ReportDataFactoryException {
    DataFactoryContext dataFactoryContext = mock( DataFactoryContext.class );
    Configuration conf = mock( Configuration.class );
    ResourceBundleFactory bundleFactory = mock( ResourceBundleFactory.class );
    ResourceManagerBackend resourceManagerBackend = mock( ResourceManagerBackend.class );
    ResourceManager manager = new ResourceManager( resourceManagerBackend );
    ResourceKey key = new ResourceKey( "schema", "identifier", null );

    doReturn( conf ).when( dataFactoryContext ).getConfiguration();
    doReturn( bundleFactory ).when( dataFactoryContext ).getResourceBundleFactory();
    doReturn( manager ).when( dataFactoryContext ).getResourceManager();
    doReturn( key ).when( dataFactoryContext ).getContextKey();

    factory.initialize( dataFactoryContext );

    assertThat( factory.getConfiguration(), is( equalTo( conf ) ) );
    assertThat( factory.getResourceBundleFactory(), is( equalTo( bundleFactory ) ) );
    assertThat( factory.getResourceManager(), is( equalTo( manager ) ) );
    assertThat( factory.getContextKey(), is( equalTo( key ) ) );
    assertThat( factory.getLocale(), is( equalTo( Locale.getDefault() ) ) );
  }

  @Test
  public void testQueryDesignTimeStructure() throws ReportDataFactoryException {
    DataRow parameters = mock( DataRow.class );
    TableModel model = mock( TableModel.class );
    doReturn( new String[] {} ).when( parameters ).getColumnNames();
    doReturn( model ).when( parameters ).get( QUERY );
    TableModel result = factory.queryDesignTimeStructure( QUERY, parameters );
    assertThat( result, is( equalTo( model ) ) );
  }

  @Test
  public void testGetDisplayConnectionName() {
    assertThat( factory.getDisplayConnectionName(), is( nullValue() ) );
  }

  @Test
  public void testgetQueryHash() throws ReportDataFactoryException {
    assertThat( factory.getQueryHash( QUERY, null ), is( nullValue() ) );
  }

  @Test
  public void testGetReferencedFields() throws ReportDataFactoryException {
    assertThat( factory.getReferencedFields( QUERY, null ), is( nullValue() ) );
  }

  @Test
  public void testClone() {
    DataFactory clonedFactory = factory.clone();
    assertThat( clonedFactory, is( instanceOf( ExternalDataFactory.class ) ) );
    assertThat( (ExternalDataFactory) clonedFactory, is( not( sameInstance( factory ) ) ) );
  }

}
