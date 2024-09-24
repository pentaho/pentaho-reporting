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

package org.pentaho.reporting.engine.classic.core.sorting;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

public class SortingDataFactoryTest {

  private SortingDataFactory factory;

  @Before
  public void setUp() {
    DataFactory parent = mock( DataFactory.class );
    PerformanceMonitorContext performanceMonitorContext = mock( PerformanceMonitorContext.class );
    PerformanceLoggingStopWatch stopWatch = mock( PerformanceLoggingStopWatch.class );

    doReturn( parent ).when( parent ).derive();
    doReturn( stopWatch ).when( performanceMonitorContext ).createStopWatch( PerformanceTags.REPORT_QUERY_SORT );

    factory = new SortingDataFactory( parent, performanceMonitorContext );
  }

  @Test
  public void testPostProcess() {
    String query = "query";
    DataRow parameters = mock( DataRow.class );
    TableModel tableModel = null;

    TableModel result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( nullValue() ) );

    tableModel = mock( TableModel.class );
    doReturn( 1 ).when( tableModel ).getRowCount();
    result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( equalTo( tableModel ) ) );

    doReturn( 2 ).when( tableModel ).getRowCount();
    doReturn( 0 ).when( tableModel ).getColumnCount();
    result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( equalTo( tableModel ) ) );

    doReturn( 1 ).when( tableModel ).getColumnCount();
    doReturn( "sort" ).when( parameters ).get( DataFactory.QUERY_SORT );
    result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( equalTo( tableModel ) ) );
  }

  @Test
  public void testPostProcessWithSortConstraints() {
    String query = "query";
    DataRow parameters = mock( DataRow.class );
    TableModel tableModel = mock( TableModel.class );

    doReturn( 2 ).when( tableModel ).getRowCount();
    doReturn( 1 ).when( tableModel ).getColumnCount();

    List<Object> querySort = new ArrayList<Object>();
    doReturn( querySort ).when( parameters ).get( DataFactory.QUERY_SORT );
    TableModel result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( equalTo( tableModel ) ) );

    querySort.add( "val_0" );
    SortConstraint sortConstraint = new SortConstraint( "field", true );
    querySort.add( sortConstraint );
    SortConstraint emptySortConstraint = new SortConstraint( "", true );
    querySort.add( emptySortConstraint );
    SortConstraint columnSortConstraint = new SortConstraint( ClassicEngineBoot.INDEX_COLUMN_PREFIX + "0", true );
    querySort.add( columnSortConstraint );
    doReturn( querySort ).when( parameters ).get( DataFactory.QUERY_SORT );
    doReturn( "test_column_name" ).when( tableModel ).getColumnName( 0 );
    result = factory.postProcess( query, parameters, tableModel );
    assertThat( result, is( not( equalTo( tableModel ) ) ) );
    assertThat( result, is( instanceOf( SortingTableModel.class ) ) );
  }
}
