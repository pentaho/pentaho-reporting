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


package org.pentaho.reporting.engine.classic.core.metadata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;

public class DefaultDataFactoryCoreTest {

  private static final String QUERY = "test_query";
  private static final String CONNECTION_NAME = "test_con_name";
  private static final String[] FIELDS = new String[] { "test_field" };

  private DefaultDataFactoryCore dataFactoryCore = new DefaultDataFactoryCore();
  private DataFactoryMetaData metaData;
  private StaticDataFactory element;
  private DataRow parameter;

  @Before
  public void setUp() throws ReportDataFactoryException {
    metaData = mock( DataFactoryMetaData.class );
    element = mock( StaticDataFactory.class );
    parameter = mock( DataRow.class );

    doReturn( FIELDS ).when( element ).getReferencedFields( QUERY, parameter );
    doReturn( CONNECTION_NAME ).when( element ).getDisplayConnectionName();
  }

  @Test
  public void testGetReferencedFields() {
    String[] result = dataFactoryCore.getReferencedFields( metaData, mock( DataFactory.class ), QUERY, parameter );
    assertThat( result, is( nullValue() ) );

    result = dataFactoryCore.getReferencedFields( metaData, element, QUERY, parameter );
    assertThat( result, is( equalTo( FIELDS ) ) );
  }

  @Test
  public void testGetReferencedResources() {
    ResourceReference[] result = dataFactoryCore.getReferencedResources( metaData, element, null, QUERY, parameter );
    assertThat( result, is( emptyArray() ) );
  }

  @Test
  public void testGetDisplayConnectionName() {
    String result = dataFactoryCore.getDisplayConnectionName( metaData, mock( DataFactory.class ) );
    assertThat( result, is( nullValue() ) );

    result = dataFactoryCore.getDisplayConnectionName( metaData, element );
    assertThat( result, is( equalTo( CONNECTION_NAME ) ) );
  }

  @Test
  public void testGetQueryHash() {
    Object result = dataFactoryCore.getQueryHash( metaData, mock( DataFactory.class ), QUERY, parameter );
    assertThat( result, is( nullValue() ) );

    result = dataFactoryCore.getQueryHash( metaData, element, QUERY, parameter );
    assertThat( result, is( instanceOf( String[].class ) ) );
    assertThat( (String[]) result, is( equalTo( FIELDS ) ) );
  }
}
