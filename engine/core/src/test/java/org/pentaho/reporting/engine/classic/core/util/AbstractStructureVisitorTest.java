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
