/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
