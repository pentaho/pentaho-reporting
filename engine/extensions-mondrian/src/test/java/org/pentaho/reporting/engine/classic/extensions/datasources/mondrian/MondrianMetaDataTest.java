/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

public class MondrianMetaDataTest extends TestCase {
  private static final String QUERY = "with member [Measures].[Foo] as  ' [Measures].[Sales] / 2 ',\n" +
    "   format_string = '$#,###',\n" +
    "   back_color = 'yellow',  \n" +
    "   my_property = iif([Measures].CurrentMember > 10, \"foo\", \"bar\")\n" +
    "select {[Measures].[Foo], [Measures].[Sales]} on 0,\n" +
    " [Product].Children on 1\n" +
    "from [SteelWheelsSales]";

  private static final String PARAMETRIZED_QUERY = "with member [Measures].[Foo] as  ' [Measures].[Sales] / 2 ',\n" +
    "   format_string = '$#,###',\n" +
    "   back_color = '${color}',  \n" +
    "   my_property = iif([Measures].CurrentMember > PARAMETER(\"pnum\", NUMERIC, 10), \"foo\", \"bar\")\n" +
    "select {[Measures].[Foo], [Measures].[Sales]} on 0,\n" +
    " [Product].Children on 1\n" +
    "from [SteelWheelsSales]";

  public MondrianMetaDataTest() {
  }

  public MondrianMetaDataTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }


  public void testMetaData() throws ReportDataFactoryException {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver( "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:hsqldb:mem:SampleData" );

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setJdbcUser( "sa" );
    mondrianDataFactory.setJdbcPassword( "" );
    mondrianDataFactory.initialize( new DesignTimeDataFactoryContext() );
    mondrianDataFactory.setQuery( "default", PARAMETRIZED_QUERY, null, null );


    final DataFactoryMetaData metaData = mondrianDataFactory.getMetaData();

    final Object queryHash = metaData.getQueryHash( mondrianDataFactory, "default", new StaticDataRow() );
    assertNotNull( queryHash );

    final BandedMDXDataFactory mdxDataFactory = new BandedMDXDataFactory();
    mdxDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mdxDataFactory.setDataSourceProvider( provider );
    mdxDataFactory.setJdbcUser( "sa" );
    mdxDataFactory.setJdbcPassword( "" );
    mdxDataFactory.initialize( new DesignTimeDataFactoryContext() );
    mdxDataFactory.setQuery( "default", QUERY, null, null );
    mdxDataFactory.setQuery( "default2", PARAMETRIZED_QUERY, null, null );

    assertNotEquals( "Physical Query is not the same", queryHash,
      metaData.getQueryHash( mdxDataFactory, "default", new StaticDataRow() ) );
    assertEquals( "Physical Query is the same", queryHash,
      metaData.getQueryHash( mdxDataFactory, "default2", new StaticDataRow() ) );

    final BandedMDXDataFactory mdxDataFactory2 = new BandedMDXDataFactory();
    mdxDataFactory2.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels2.mondrian.xml" ) );
    mdxDataFactory2.setDataSourceProvider( provider );
    mdxDataFactory2.setJdbcUser( "sa" );
    mdxDataFactory2.setJdbcPassword( "" );
    mdxDataFactory2.initialize( new DesignTimeDataFactoryContext() );
    mdxDataFactory2.setQuery( "default", QUERY, null, null );
    mdxDataFactory2.setQuery( "default2", PARAMETRIZED_QUERY, null, null );

    assertNotEquals( "Physical Connection is not the same", queryHash,
      metaData.getQueryHash( mdxDataFactory, "default", new StaticDataRow() ) );
    assertNotEquals( "Physical Connection is the same", queryHash,
      metaData.getQueryHash( mdxDataFactory2, "default2", new StaticDataRow() ) );
  }

  public void testParameter() throws ReportDataFactoryException {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver( "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:hsqldb:mem:SampleData" );

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setJdbcUser( "sa" );
    mondrianDataFactory.setJdbcPassword( "" );
    mondrianDataFactory.initialize( new DesignTimeDataFactoryContext() );
    mondrianDataFactory.setQuery( "default", PARAMETRIZED_QUERY, null, null );
    mondrianDataFactory.setQuery( "default2", QUERY, null, null );

    final DataFactoryMetaData metaData = mondrianDataFactory.getMetaData();
    final String[] fields = metaData.getReferencedFields( mondrianDataFactory, "default", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 3, fields.length );
    assertEquals( "color", fields[ 0 ] );
    assertEquals( "pnum", fields[ 1 ] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[ 2 ] );

    final String[] fields2 = metaData.getReferencedFields( mondrianDataFactory, "default2", new StaticDataRow() );
    assertNotNull( fields2 );
    assertEquals( 1, fields2.length );
    assertEquals( DataFactory.QUERY_LIMIT, fields2[ 0 ] );
  }

  private static void assertNotEquals( final String message, final Object o1, final Object o2 ) {
    if ( o1 == o2 ) {
      fail( message );
    }
    if ( o1 != null && o2 == null ) {
      return;
    }
    if ( o1 == null ) {
      return;
    }
    if ( o1.equals( o2 ) ) {
      fail( message );
    }
  }

}
