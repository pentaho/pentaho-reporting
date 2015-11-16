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
 * Copyright (c) 2000 - 2015 Pentaho Corporation, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;

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

}
