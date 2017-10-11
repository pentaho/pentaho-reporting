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
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.12.2009 Time: 18:09:53
 *
 * @author Thomas Morgner.
 */
public class ParameterTest extends TestCase {
  public ParameterTest() {
  }

  public ParameterTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBoo() throws ReportDataFactoryException {
/*    final String query =
        "select NON EMPTY {[Measures].[Sales],[Measures].[Quantity] } ON COLUMNS,\n" +
        "  { [TopSelection], [Customers].[All Customers].[Other Customers]} ON ROWS\n" +
        "from [SteelWheelsSales]\n" +
        "where \n" +
        "(\n" +
        "Parameter(\"sLine\", [Product], \n" +
        "   [Product].[All Products].[Classic Cars]), \n" +
        "[Markets].[All Markets].[Japan],\n" +
        "[Time].[All Years].[2003]\n" +
        ")";
*/
    String query = "SELECT STRTOMEMBER(\"[Product].[All Products].[Classic Cars]\") ON 0 FROM [SteelWheelsSales]";
    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver( "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:hsqldb:mem:SampleData" );
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setJdbcUser( "sa" );
    mondrianDataFactory.setJdbcPassword( "" );
    try {
      mondrianDataFactory.setQuery( "default", query, null, null );
      mondrianDataFactory.initialize( new DesignTimeDataFactoryContext() );

      final ParameterDataRow parameters = new ParameterDataRow( new String[] { "sLine" },
        new String[] { "[Product].[All Products].[Classic Cars]" } );
      final CloseableTableModel tableModel = (CloseableTableModel) mondrianDataFactory.queryData( "default",
        parameters );
      tableModel.close();
    } finally {

      mondrianDataFactory.close();
    }

  }
}
