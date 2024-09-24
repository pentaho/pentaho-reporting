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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.states.ProcessStateHandle;

import javax.swing.table.TableModel;

public class Prd3187IT extends TestCase {
  private static class TestDataFactory extends AbstractDataFactory {
    private boolean open;

    private TestDataFactory() {
      open = true;
    }

    public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
      throw new ReportDataFactoryException( "Unconditional error thrown" );
    }

    public DataFactory derive() {
      return this;
    }

    public DataFactory clone() {
      return this;
    }

    public void close() {
      if ( open ) {
        open = false;
      }
    }

    public boolean isOpen() {
      return open;
    }

    public boolean isQueryExecutable( final String query, final DataRow parameters ) {
      return true;
    }

    public String[] getQueryNames() {
      return new String[0];
    }
  }

  private class TestPrintReportProcessor extends PrintReportProcessor {
    private TestPrintReportProcessor( final MasterReport report ) throws ReportProcessingException {
      super( report );
    }

    public ProcessStateHandle getProcessStateHandle() {
      return super.getProcessStateHandle();
    }
  }

  public Prd3187IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testErrorDoesNotClose() throws Exception {
    final MasterReport report = new MasterReport();
    final TestDataFactory dataFactory = new TestDataFactory();
    report.setDataFactory( dataFactory );

    final TestPrintReportProcessor reportProcessor = new TestPrintReportProcessor( report );
    try {
      reportProcessor.paginate();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    assertNotNull( reportProcessor.getErrorReason() );
    final ProcessStateHandle processStateHandle = reportProcessor.getProcessStateHandle();
    processStateHandle.close();
    assertFalse( dataFactory.isOpen() );
  }

  public void testErrorDoesNotCloseSubreport() throws Exception {
    final TestDataFactory dataFactory = new TestDataFactory();
    final SubReport subreport = new SubReport();
    subreport.setQuery( "Query" );
    subreport.setDataFactory( dataFactory );

    final MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( subreport );

    final TestPrintReportProcessor reportProcessor = new TestPrintReportProcessor( report );
    try {
      reportProcessor.paginate();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    assertNotNull( reportProcessor.getErrorReason() );
    final ProcessStateHandle processStateHandle = reportProcessor.getProcessStateHandle();
    processStateHandle.close();
    assertFalse( dataFactory.isOpen() );
  }
}
