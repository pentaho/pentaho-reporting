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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class LegacyBandedDataSourceDriverTest extends DataSourceTestBase {
  private static final String[][] QUERIES_AND_RESULTS = MondrianTestUtil.createQueryArray( "-legacy" );

  public LegacyBandedDataSourceDriverTest() {
  }

  public LegacyBandedDataSourceDriverTest( final String s ) {
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
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver( "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:hsqldb:mem:SampleData" );

    final LegacyBandedMDXDataFactory mondrianDataFactory = new LegacyBandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setJdbcUser( "sa" );
    mondrianDataFactory.setJdbcPassword( "" );
    mondrianDataFactory.setQuery( "default", query, null, null );
    initializeDataFactory( mondrianDataFactory );
    return mondrianDataFactory;
  }

  public static void _main( String[] args ) throws Exception {
    final LegacyBandedDataSourceDriverTest test = new LegacyBandedDataSourceDriverTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

}
