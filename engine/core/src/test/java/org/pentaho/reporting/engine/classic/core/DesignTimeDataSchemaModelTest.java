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


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;

import javax.swing.table.DefaultTableModel;

public class DesignTimeDataSchemaModelTest extends TestCase {
  public DesignTimeDataSchemaModelTest() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunWithInvalidQuery() throws ReportDataFactoryException {

    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add( new TableDataFactory( "query", new DefaultTableModel() ) );
    cdf.add( new TableDataFactory( "query", new DefaultTableModel() ) );

    final DataFactory tableDataFactory1 = cdf.getReference( 0 );
    final DataFactory tableDataFactory2 = cdf.getReference( 1 );

    final MasterReport report = new MasterReport();
    report.setDataFactory( cdf );
    report.setQuery( "default" );

    assertFalse( DesignTimeUtil.isSelectedDataSource( report, tableDataFactory2, "query" ) );
    assertFalse( DesignTimeUtil.isSelectedDataSource( report, tableDataFactory1, "query" ) );
  }

  public void testRunWithValidQuery() throws ReportDataFactoryException {

    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add( new TableDataFactory( "query", new DefaultTableModel() ) );
    cdf.add( new TableDataFactory( "query", new DefaultTableModel() ) );

    final DataFactory tableDataFactory1 = cdf.getReference( 0 );
    final DataFactory tableDataFactory2 = cdf.getReference( 1 );

    final MasterReport report = new MasterReport();
    report.setDataFactory( cdf );
    report.setQuery( "query" );

    assertFalse( DesignTimeUtil.isSelectedDataSource( report, tableDataFactory2, "query" ) );
    assertTrue( DesignTimeUtil.isSelectedDataSource( report, tableDataFactory1, "query" ) );
  }
}
