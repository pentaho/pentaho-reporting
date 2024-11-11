/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.sorting;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class QueryDataRowWrapperTest {

  @BeforeClass
  public static void initEngine() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private List<SortConstraint> sortConstraintList;

  @Before
  public void setUp() throws Exception {
    sortConstraintList = Arrays.asList( new SortConstraint( "A", false ), new SortConstraint( "B", true ) );
  }

  @Test
  public void testExtraColumn() {
    QueryDataRowWrapper wrapper = new QueryDataRowWrapper( new StaticDataRow(), 10, 12, sortConstraintList );
    String[] expecteds = { DataFactory.QUERY_LIMIT, DataFactory.QUERY_TIMEOUT, DataFactory.QUERY_SORT };
    assertArrayEquals( expecteds, wrapper.getColumnNames() );
    assertEquals( wrapper.get( DataFactory.QUERY_LIMIT ), Integer.valueOf( 12 ) );
    assertEquals( wrapper.get( DataFactory.QUERY_TIMEOUT ), Integer.valueOf( 10 ) );
    assertEquals( wrapper.get( DataFactory.QUERY_SORT ), sortConstraintList );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void deprecatedConstructor_int_int() {
    final int limit = 1;
    final int timeout = 2;
    QueryDataRowWrapper wrapper = new QueryDataRowWrapper( new StaticDataRow(), limit, timeout );
    assertEquals( wrapper.get( DataFactory.QUERY_LIMIT ), Integer.valueOf( limit ) );
    assertEquals( wrapper.get( DataFactory.QUERY_TIMEOUT ), Integer.valueOf( timeout ) );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void deprecatedConstructor_Integer_Integer() {
    final Integer limit = 1;
    final Integer timeout = 2;
    QueryDataRowWrapper wrapper = new QueryDataRowWrapper( new StaticDataRow(), timeout, limit );
    assertEquals( wrapper.get( DataFactory.QUERY_LIMIT ), limit );
    assertEquals( wrapper.get( DataFactory.QUERY_TIMEOUT ), timeout );
  }
}
