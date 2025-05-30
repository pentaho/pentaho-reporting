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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataFactoryScriptSupportIT {

  private String globalScript = "var globalScopeVariable = 'global'; \n" + "function init (dataRow)  \n" + "{ \n"
      + "  if (globalScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error';  \n"
      + "} \n" + "\n" + "function shutdown ()  \n" + "{ \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "} \n" + "\n";

  private String queryScript = "var localScopeVariable = 'local'; \n" + "function initQuery ()  \n" + "{ \n"
      + "  print ('Init ');\n" + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (globalScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error';  \n"
      + "} \n" + "\n" + "function computeQueryFields (query, queryName)  \n" + "{ \n"
      + "  print ('computeQueryFields ' + query);\n" + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (globalScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "  return ['one', 'two'];\n" + "} \n" + "\n" + "function computeQuery (query, queryName, dataRow)  \n" + "{ \n"
      + "  print ('computeQuery ' + query);" + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (globalScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "  return 'result'; \n" + "} \n" + "\n" + "function shutdown ()  \n" + "{ \n" + "  print ('shutdown ');"
      + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (globalScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "} \n" + "\n";

  private String queryScript2 = "var localScopeVariable = 'local'; \n" + "function initQuery ()  \n" + "{ \n"
      + "  print ('Init ');\n" + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (resourceManager == null) throw 'error';  \n" + "  if (dataFactory == null) throw 'error';  \n"
      + "  if (configuration == null) throw 'error'; \n" + "  if (contextKey == null) throw 'error';  \n"
      + "  if (resourceBundleFactory == null) throw 'error';  \n" + "} \n" + "\n"
      + "function computeQueryFields (query, queryName)  \n" + "{ \n" + "  print ('computeQueryFields ' + query);\n"
      + "  if (localScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "  return ['one', 'two'];\n" + "} \n" + "\n" + "function computeQuery (query, queryName, dataRow)  \n" + "{ \n"
      + "  print ('computeQuery ' + query);" + "  if (localScopeVariable == null) throw 'error';  \n"
      + "  if (resourceManager == null) throw 'error';  \n" + "  if (dataFactory == null) throw 'error';  \n"
      + "  if (configuration == null) throw 'error'; \n" + "  if (contextKey == null) throw 'error';  \n"
      + "  if (resourceBundleFactory == null) throw 'error'; \n" + "  return 'result'; \n" + "} \n" + "\n"
      + "function shutdown ()  \n" + "{ \n" + "  print ('shutdown ');"
      + "  if (localScopeVariable == null) throw 'error';  \n" + "  if (resourceManager == null) throw 'error';  \n"
      + "  if (dataFactory == null) throw 'error';  \n" + "  if (configuration == null) throw 'error'; \n"
      + "  if (contextKey == null) throw 'error';  \n" + "  if (resourceBundleFactory == null) throw 'error'; \n"
      + "} \n" + "\n";

  private String queryScript3 = " var globalScopeVariable = 'override';\n"
    + "    function initQuery (){\n"
    + "      if(globalScopeVariable!='override'){\n"
    + "        throw 'error';\n"
    + "      }\n"
    + "    }";

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
    System.setProperty( "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "true" );
  }

  @After
  public void tearDown() {
    System.clearProperty( "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation" );
  }

  @Test
  public void testSimpleSetup() throws ReportDataFactoryException {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();

    assertNotNull( support );

    support.setGlobalScriptLanguage( "JavaScript" );
    support.setGlobalScript( globalScript );
    support.initialize( new TableDataFactory(),
        new DesignTimeDataFactoryContext( ClassicEngineBoot.getInstance().getGlobalConfig(), mgr, new ResourceKey(
            "dummy", "dummy", new HashMap() ), new DefaultResourceBundleFactory() ) );

    assertNotNull( support.globalScriptContext );
    assertNotNull( support.globalScriptContext.getAttribute( "dataFactory" ) );
    assertNotNull( support.globalScriptContext.getAttribute( "configuration" ) );
    assertNotNull( support.globalScriptContext.getAttribute( "resourceManager" ) );
    assertNotNull( support.globalScriptContext.getAttribute( "contextKey" ) );
    assertNotNull( support.globalScriptContext.getAttribute( "resourceBundleFactory" ) );
    assertNotNull( support.globalScriptContext.getAttribute( "scriptHelper" ) );

    support.shutdown();
  }

  @Test
  public void testQuerySetup() throws ReportDataFactoryException {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.setGlobalScriptLanguage( "JavaScript" );
    support.setGlobalScript( globalScript );
    support.initialize( new TableDataFactory(),
        new DesignTimeDataFactoryContext( ClassicEngineBoot.getInstance().getGlobalConfig(), mgr, new ResourceKey(
            "dummy", "dummy", new HashMap() ), new DefaultResourceBundleFactory() ) );
    support.setQuery( "test", "test-query", null, null );
    support.setQuery( "test-script", "test-query-2", "JavaScript", queryScript );

    assertEquals( "test-query", support.computeQuery( "test", new ParameterDataRow() ) );
    assertEqualsArray( null, support.computeAdditionalQueryFields( "test", new ParameterDataRow() ) );
    assertEquals( "result", support.computeQuery( "test-script", new ParameterDataRow() ) );
    assertEqualsArray( null, support.computeAdditionalQueryFields( "test-script", new ParameterDataRow() ) );
    support.shutdown();
  }

  @Test
  public void testQueryOnly() throws ReportDataFactoryException {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.setGlobalScriptLanguage( "JavaScript" );
    support.setGlobalScript( globalScript );
    support.initialize( new TableDataFactory(),
        new DesignTimeDataFactoryContext( ClassicEngineBoot.getInstance().getGlobalConfig(), mgr, new ResourceKey(
            "dummy", "dummy", new HashMap() ), new DefaultResourceBundleFactory() ) );
    support.setQuery( "test", "test-query", null, null );
    support.setQuery( "test-script", "test-query-2", "JavaScript", queryScript2 );

    assertEquals( "test-query", support.computeQuery( "test", new ParameterDataRow() ) );
    assertEqualsArray( null, support.computeAdditionalQueryFields( "test", new ParameterDataRow() ) );
    assertEquals( "result", support.computeQuery( "test-script", new ParameterDataRow() ) );
    assertEqualsArray( null, support.computeAdditionalQueryFields( "test-script", new ParameterDataRow() ) );
    support.shutdown();
  }

  @Test
  public void testPostProcessResult() throws ReportDataFactoryException {
    final DriverConnectionProvider driverConnectionProvider = new DriverConnectionProvider();

    driverConnectionProvider.setDriver( "org.hsqldb.jdbcDriver" );
    driverConnectionProvider.setUrl( "jdbc:hsqldb:mem:SampleData" );
    driverConnectionProvider.setProperty( "user", "sa" );
    driverConnectionProvider.setProperty( "password", "" );

    final SQLReportDataFactory sqlReportDataFactory = new SQLReportDataFactory( driverConnectionProvider );

    sqlReportDataFactory.setQuery( "default", "SELECT Count(*) FROM CUSTOMERS", "Groovy",
        "import org.pentaho.reporting.engine.classic.core.util.TypedTableModel\n"
            + "def postProcessResult(query, queryName, dataRow, tableModel){\n"
            + "TypedTableModel model = new TypedTableModel([\"column1\"] as String[], [String.class] as Class[]);"
            + "model.addRow(\"row1\");" + "return model;" + "}" );

    try {
      sqlReportDataFactory.initialize( new DesignTimeDataFactoryContext() );
      TableModel data = sqlReportDataFactory.queryData( "default", new StaticDataRow() );
      assertEquals( 1, data.getColumnCount() );
      assertEquals( 1, data.getRowCount() );
      Assert.assertEquals( "row1", data.getValueAt( 0, 0 ) );
    } finally {
      sqlReportDataFactory.close();
    }
  }

  @Test
  public void testOverrideGlobal() throws ReportDataFactoryException {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.setGlobalScriptLanguage( "JavaScript" );
    support.setGlobalScript( globalScript );
    support.initialize( new TableDataFactory(),
      new DesignTimeDataFactoryContext( ClassicEngineBoot.getInstance().getGlobalConfig(), mgr, new ResourceKey(
        "dummy", "dummy", new HashMap() ), new DefaultResourceBundleFactory() ) );
    support.setQuery( "test", "test-query", null, null );
    support.setQuery( "test-script", "test-query-3", "JavaScript", queryScript3 );


    support.computeQuery( "test-script", new ParameterDataRow() );

    support.shutdown();
  }

  private void assertEqualsArray( final String[] o, final String[] strings ) {
    if ( o == null ) {
      System.out.println( Arrays.asList( strings ) );
      return;
    }
    assertNotNull( strings );
    Assert.assertEquals( Arrays.asList( o ), Arrays.asList( strings ) );
  }
}
