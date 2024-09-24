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

public class BandedMDXTableModelTest extends DataSourceTestBase {

  private static final String QUERY_BY_HIERARCHY =
    "SELECT {[Measures].[Quantity]} ON COLUMNS, TOPCOUNT(NONEMPTYCROSSJOIN([Markets].[Country].MEMBERS,[Markets.City]"
      + ".[City].MEMBERS), 5) ON ROWS FROM [SteelWheelsSales]";
  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY_BY_HIERARCHY, "steelwheels_hierarchy_result.txt" }
  };

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final BandedMDXDataFactory mdxDataFactory = new BandedMDXDataFactory();
    mdxDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels_hierarchy.mondrian"
        + ".xml" ) );
    mdxDataFactory.setDataSourceProvider( new JndiDataSourceProvider( "SampleData" ) );
    mdxDataFactory.setQuery( "default", query, null, null );
    initializeDataFactory( mdxDataFactory );
    return mdxDataFactory;
  }

  public void testQuery() throws Exception {
    runTest( QUERIES_AND_RESULTS );
  }

  public static void _main( String[] args ) throws Exception {
    final BandedMDXTableModelTest test = new BandedMDXTableModelTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

}
