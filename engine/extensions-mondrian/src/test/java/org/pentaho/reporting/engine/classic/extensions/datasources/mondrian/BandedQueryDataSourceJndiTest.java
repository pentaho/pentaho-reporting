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

public class BandedQueryDataSourceJndiTest extends DataSourceTestBase {
  private static final String[][] QUERIES_AND_RESULTS = MondrianTestUtil.createQueryArray( "-banded" );

  public BandedQueryDataSourceJndiTest() {
  }

  public BandedQueryDataSourceJndiTest( final String s ) {
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
    final JndiDataSourceProvider provider = new JndiDataSourceProvider( "SampleData" );

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setQuery( "default", query, null, null );
    initializeDataFactory( mondrianDataFactory );
    return mondrianDataFactory;
  }

  public static void _main( String[] args ) throws Exception {
    final BandedQueryDataSourceJndiTest test = new BandedQueryDataSourceJndiTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

}
