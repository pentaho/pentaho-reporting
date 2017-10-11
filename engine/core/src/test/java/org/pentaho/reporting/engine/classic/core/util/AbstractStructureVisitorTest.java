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

package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;

import javax.swing.table.DefaultTableModel;

public class AbstractStructureVisitorTest extends TestCase {
  private static class TestStructureVisitor extends AbstractStructureVisitor {
    private final int[] callCount;

    public TestStructureVisitor( final int[] callCount ) {
      this.callCount = callCount;
    }

    protected void traverseSection( final Section section ) {
      traverseSectionWithSubReports( section );
    }

    protected void inspectDataSource( final AbstractReportDefinition report, final DataFactory dataFactory ) {
      callCount[0] += 1;
      assertTrue( dataFactory instanceof TableDataFactory );
      TableDataFactory tdf = (TableDataFactory) dataFactory;
      String[] queryNames = tdf.getQueryNames();
      assertEquals( 1, queryNames.length );
      if ( "query1".equals( queryNames[0] ) == false && "query2".equals( queryNames[0] ) == false ) {
        fail();
      }
    }

    public void inspect( final AbstractReportDefinition reportDefinition ) {
      super.inspect( reportDefinition );
    }
  }

  public AbstractStructureVisitorTest() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testDataFactoryInspection() {
    CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add( new TableDataFactory( "query1", new DefaultTableModel() ) );
    cdf.add( new TableDataFactory( "query2", new DefaultTableModel() ) );
    MasterReport report = new MasterReport();
    report.setDataFactory( cdf );

    final int[] callCount = new int[1];
    final TestStructureVisitor v = new TestStructureVisitor( callCount );
    v.inspect( report );
    assertEquals( 2, callCount[0] );
  }
}
