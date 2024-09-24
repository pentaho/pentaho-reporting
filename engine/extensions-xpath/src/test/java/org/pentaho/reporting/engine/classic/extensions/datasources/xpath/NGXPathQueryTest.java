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

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class NGXPathQueryTest extends DataSourceTestBase {
  public static final String QUERY_1 = "/*/*";

  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY_1, "query-ng-results.txt" },
  };


  public NGXPathQueryTest() {
  }

  public NGXPathQueryTest( final String s ) {
    super( s );
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final XPathDataFactory dataFactory = new XPathDataFactory();
    dataFactory
      .setXqueryDataFile( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/xpath/customer.xml" );
    initializeDataFactory( dataFactory );
    dataFactory.setQuery( "default", query, false );
    return dataFactory;
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

  public static void main( final String[] args ) throws Exception {
    final NGXPathQueryTest test = new NGXPathQueryTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }
}
