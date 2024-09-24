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
package org.pentaho.reporting.ui.datasources.jdbc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Window;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.ui.datasources.jdbc.ui.SimpleJdbcDataSourceDialog;

public class SimpleJdbcDataSourcePluginTest {

  private static final String PROPERTY_NAME = "java.awt.headless";

  private static String defaultValue;

  @BeforeClass
  public static void beforeClass() {
    defaultValue = System.getProperty( PROPERTY_NAME, Boolean.toString( true ) );
    System.setProperty( PROPERTY_NAME, Boolean.toString( true ) );
  }

  @AfterClass
  public static void afterClass() {
    System.setProperty( PROPERTY_NAME, defaultValue );
  }

  @Test
  public void performEdit() {
    SimpleJdbcDataSourcePlugin simpleJdbcDataSourcePlugin = spy( new SimpleJdbcDataSourcePlugin() );
    SimpleJdbcDataSourceDialog editor = mock( SimpleJdbcDataSourceDialog.class );
    doReturn( editor ).when( simpleJdbcDataSourcePlugin ).createEditor( any( DesignTimeContext.class ) );
    DesignTimeContext designTimeContext = mock( DesignTimeContext.class );
    when( designTimeContext.getParentWindow() ).thenReturn( mock( Window.class ) );
    DataFactory input = mock( SimpleSQLReportDataFactory.class );
    DataFactoryChangeRecorder changeRecorder = mock( DataFactoryChangeRecorder.class );

    simpleJdbcDataSourcePlugin.performEdit( designTimeContext, input, "TEST_QUERY_NAME", changeRecorder );
    verify( editor, times( 1 ) ).performConfiguration( any( SimpleSQLReportDataFactory.class ) );
  }

}
