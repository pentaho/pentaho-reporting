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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

import javax.naming.spi.NamingManager;
import javax.swing.table.TableModel;

public class Prd5276Test {
  private static final String query = MondrianTestUtil.QUERY_UNION_OK;

  private static final String queryFlipped = MondrianTestUtil.QUERY_UNION_FLIPPED;

  private static final String queryBroken = MondrianTestUtil.QUERY_UNION_BROKEN;

  private static final String queryMultipleH =
    "SELECT [Product].Children ON COLUMNS, Hierarchize({[Time].[Years].Members, [Time].[Quarters].Members, [Time]"
      + ".[Months].Members}) ON ROWS FROM [SteelWheelsSales]";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }


  /**
   * Validates that queries with empty results (no rows or no columns) are correctly handled by CachingDataFactory.
   * <p/>
   * http://jira.pentaho.com/browse/PRD-4628
   */
  @Test
  public void testQuery() throws ReportDataFactoryException {
    DataFactory dataFactory = createDataFactory( queryBroken );
    final TableModel tableModel = dataFactory.queryData( "default", new ParameterDataRow() );
    //    new DataPreviewDialog().showData(tableModel);

    Assert.assertEquals( "[Markets].[(All)]", tableModel.getColumnName( 2 ) );
    Assert.assertEquals( "[Product].[Line]", tableModel.getColumnName( 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 0, 3 ) );
    Assert.assertNotNull( tableModel.getValueAt( 1, 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 2, 2 ) );

    //    TableModelInfo.printTableModel(tableModel);
  }

  /**
   * Validates that queries with empty results (no rows or no columns) are correctly handled by CachingDataFactory.
   * <p/>
   * http://jira.pentaho.com/browse/PRD-4628
   */
  @Test
  public void testQueryOK() throws ReportDataFactoryException {
    DataFactory dataFactory = createDataFactory( query );
    final TableModel tableModel = dataFactory.queryData( "default", new ParameterDataRow() );
    //    new DataPreviewDialog().showData(tableModel);

    Assert.assertEquals( "[Markets].[(All)]", tableModel.getColumnName( 2 ) );
    Assert.assertEquals( "[Product].[Line]", tableModel.getColumnName( 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 1, 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 2, 2 ) );

    //    TableModelInfo.printTableModel(tableModel);
  }

  @Test
  public void testQueryMultipleH() throws ReportDataFactoryException {
    DataFactory dataFactory = createDataFactory( queryMultipleH );
    final TableModel tableModel = dataFactory.queryData( "default", new ParameterDataRow() );

    Assert.assertEquals( "[Time].[Quarters]", tableModel.getColumnName( 2 ) );
    Assert.assertEquals( "[Time].[Years]", tableModel.getColumnName( 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 1, 1 ) );
    Assert.assertNotNull( tableModel.getValueAt( 2, 2 ) );

    //    TableModelInfo.printTableModel(tableModel);
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver( "org.hsqldb.jdbcDriver" );
    provider.setUrl( "jdbc:hsqldb:mem:SampleData" );

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider( new DefaultCubeFileProvider
      ( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" ) );
    mondrianDataFactory.setDataSourceProvider( provider );
    mondrianDataFactory.setJdbcUser( "sa" );
    mondrianDataFactory.setJdbcPassword( "" );
    mondrianDataFactory.setQuery( "default", query, null, null );
    mondrianDataFactory.initialize( new DesignTimeDataFactoryContext() );
    return mondrianDataFactory;
  }
}
