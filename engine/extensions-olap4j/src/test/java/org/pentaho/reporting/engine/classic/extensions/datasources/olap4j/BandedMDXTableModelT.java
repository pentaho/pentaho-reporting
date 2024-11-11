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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;

public class BandedMDXTableModelT extends DataSourceTestBase {

  private static final String QUERY_BY_HIERARCHY =
    "SELECT {[Measures].[Quantity]} ON COLUMNS, TOPCOUNT(NONEMPTYCROSSJOIN([Markets].[Country].MEMBERS,[Markets.City]"
      + ".[City].MEMBERS), 5) ON ROWS FROM [SteelWheelsSales]";
  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY_BY_HIERARCHY, "steelwheels_hierarchy_result.txt" }
  };

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final DriverConnectionProvider provider = new DriverConnectionProvider();
    provider.setDriver( "mondrian.olap4j.MondrianOlap4jDriver" );
    provider.setProperty( "Catalog",
      "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/olap4j/steelwheels_hierarchy.mondrian.xml" );
    provider.setProperty( "JdbcUser", "sa" );
    provider.setProperty( "JdbcPassword", "" );
    provider.setProperty( "Jdbc", "jdbc:hsqldb:mem:SampleData" );
    provider.setProperty( "JdbcDrivers", "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:mondrian:" );

    final BandedMDXDataFactory dataFactory = new BandedMDXDataFactory( provider );
    dataFactory.setQuery( "default", query, null, null );
    dataFactory.initialize( new DesignTimeDataFactoryContext() );
    return dataFactory;
  }

  public void testQuery() throws Exception {
    runTest( QUERIES_AND_RESULTS );
  }

  public static void _main( String[] args ) throws Exception {
    final BandedMDXTableModelT test = new BandedMDXTableModelT();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

}
