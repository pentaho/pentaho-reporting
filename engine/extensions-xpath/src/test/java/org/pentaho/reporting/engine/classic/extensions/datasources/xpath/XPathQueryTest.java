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


package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class XPathQueryTest extends DataSourceTestBase {
  public static final String QUERY_1 = "/*/*";
  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY_1, "query1-results.txt" },
  };

  public XPathQueryTest() {
  }

  public XPathQueryTest( final String s ) {
    super( s );
  }

  public void testFromBundle() throws Exception {

    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res =
      manager.createDirectly( XPathQueryTest.class.getResource( "xpath-bundle-test.prpt" ), MasterReport.class );
    final MasterReport report = (MasterReport) res.getResource();

    final CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    final XPathDataFactory xpathDataFactory = (XPathDataFactory) dataFactory.getReference( 0 );
    xpathDataFactory.initialize( new DesignTimeDataFactoryContext( report ) );
    xpathDataFactory.queryData( "default", new StaticDataRow() );
    xpathDataFactory.close();
  }

  public void testSaveAndLoad() throws Exception {
    runSaveAndLoad( QUERIES_AND_RESULTS );
  }

  public void testDerive() throws Exception {
    runDerive( QUERIES_AND_RESULTS );
  }

  public void testSerialize() throws Exception {
    runSerialize( QUERIES_AND_RESULTS );
  }

  public void testQuery() throws Exception {
    runTest( QUERIES_AND_RESULTS );
  }


  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final XPathDataFactory dataFactory = new XPathDataFactory();
    dataFactory
      .setXqueryDataFile( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/xpath/customer.xml" );
    initializeDataFactory( dataFactory );
    dataFactory.setQuery( "default", query, true );
    return dataFactory;
  }


  public static void main( final String[] args ) throws Exception {
    final XPathQueryTest test = new XPathQueryTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }
}
