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
