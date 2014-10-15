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
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import javax.naming.spi.NamingManager;
import javax.swing.table.TableModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableModelInfo;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;

public class Prd5276Test
{
  private static final String query = "SELECT\n" +
      " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON COLUMNS,\n" +
      "NON EMPTY(\n" +
      "Union ( \n" +
      "[Product].Children * {[Markets].[All Markets], [Markets].Children},\n" +
      "[Product].[All Products] * [Markets].[All Markets] \n" +
      ") \n" +
      ") ON ROWS\n" +
      "FROM [SteelWheelsSales]\n" +
      "WHERE [Measures].[Quantity]\n";

  private static final String queryFlipped = "SELECT\n" +
      " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON ROWS,\n" +
      "NON EMPTY(\n" +
      "Union ( \n" +
      "[Product].Children * {[Markets].[All Markets], [Markets].Children},\n" +
      "[Product].[All Products] * [Markets].[All Markets] \n" +
      ") \n" +
      ") ON COLUMNS\n" +
      "FROM [SteelWheelsSales]\n" +
      "WHERE [Measures].[Quantity]\n";

  private static final String queryBroken = "SELECT\n" +
      " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON COLUMNS,\n" +
      "NON EMPTY(\n" +
      "Union ( \n" +
      "[Product].[All Products] * [Markets].[All Markets], \n" +
      "[Product].Children * {[Markets].[All Markets], [Markets].Children}\n" +
      ") \n" +
      ") ON ROWS\n" +
      "FROM [SteelWheelsSales]\n" +
      "WHERE [Measures].[Quantity]\n";



  @Before
  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    if (NamingManager.hasInitialContextFactoryBuilder() == false)
    {
      NamingManager.setInitialContextFactoryBuilder(new DebugJndiContextFactoryBuilder());
    }
  }


  /**
   *  Validates that queries with empty results (no rows or no columns)
   *  are correctly handled by CachingDataFactory.
   *
   *   http://jira.pentaho.com/browse/PRD-4628
   */
  @Test
  public void testQuery() throws ReportDataFactoryException
  {
    DataFactory dataFactory = createDataFactory(queryBroken);
    final TableModel tableModel = dataFactory.queryData("default", new ParameterDataRow());
//    new DataPreviewDialog().showData(tableModel);

    Assert.assertEquals("[Markets].[(All)]", tableModel.getColumnName(1));
    Assert.assertEquals("[Product].[Line]", tableModel.getColumnName(2));
    Assert.assertNotNull(tableModel.getValueAt(1, 1));
    Assert.assertNotNull(tableModel.getValueAt(2, 2));

//    TableModelInfo.printTableModel(tableModel);
  }

  /**
   *  Validates that queries with empty results (no rows or no columns)
   *  are correctly handled by CachingDataFactory.
   *
   *   http://jira.pentaho.com/browse/PRD-4628
   */
  @Test
  public void testQueryOK() throws ReportDataFactoryException
  {
    DataFactory dataFactory = createDataFactory(query);
    final TableModel tableModel = dataFactory.queryData("default", new ParameterDataRow());
//    new DataPreviewDialog().showData(tableModel);

    Assert.assertEquals("[Markets].[(All)]", tableModel.getColumnName(2));
    Assert.assertEquals("[Product].[Line]", tableModel.getColumnName(1));
    Assert.assertNotNull(tableModel.getValueAt(1, 1));
    Assert.assertNotNull(tableModel.getValueAt(2, 2));

//    TableModelInfo.printTableModel(tableModel);
  }

  protected DataFactory createDataFactory(final String query) throws ReportDataFactoryException
  {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver("org.hsqldb.jdbcDriver");
    provider.setUrl("jdbc:hsqldb:mem:SampleData");

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider(new DefaultCubeFileProvider
        ("test/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml"));
    mondrianDataFactory.setDataSourceProvider(provider);
    mondrianDataFactory.setJdbcUser("sa");
    mondrianDataFactory.setJdbcPassword("");
    mondrianDataFactory.setQuery("default", query, null, null);
    return mondrianDataFactory;
  }
}
