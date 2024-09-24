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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;

public class LegacyBandedOlap4JJndiT extends DataSourceTestBase {
  private static final String[][] QUERIES_AND_RESULTS = Olap4JTestUtil.createQueryArray( "-legacy" );

  public LegacyBandedOlap4JJndiT() {
  }

  public LegacyBandedOlap4JJndiT( final String s ) {
    super( s );
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
    final JndiConnectionProvider provider = new JndiConnectionProvider();
    provider.setConnectionPath( "SampleOlap4J" );

    final LegacyBandedMDXDataFactory dataFactory = new LegacyBandedMDXDataFactory( provider );
    dataFactory.setQuery( "default", query, null, null );
    initializeDataFactory( dataFactory );
    return dataFactory;
  }


  public static void _main( final String[] args ) throws Exception {
    final LegacyBandedOlap4JJndiT test = new LegacyBandedOlap4JJndiT();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

}
