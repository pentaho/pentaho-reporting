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
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.csv.FastCsvReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.FileOutputStream;
import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class Backlog7181Test {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testXls() throws Exception {
    URL url = getClass().getResource( "Backlog-7181.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner.createTestOutputFile();
    final ReportProgressListener mock = mock( ReportProgressListener.class );
    try ( FileOutputStream stream = new FileOutputStream( "test-output/Backlog-7181.xls" ) ) {
      FastExcelReportUtil.processXls( report, stream, mock );
    }
    verify( mock, times( 1 ) ).reportProcessingStarted( any( ReportProgressEvent.class ) );
    verify( mock, times( 1 ) ).reportProcessingFinished( any( ReportProgressEvent.class ) );
    verify( mock, atLeastOnce() ).reportProcessingUpdate( any( ReportProgressEvent.class ) );
  }


  @Test
  public void testXlsx() throws Exception {
    URL url = getClass().getResource( "Backlog-7181.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner.createTestOutputFile();
    final ReportProgressListener mock = mock( ReportProgressListener.class );
    try ( FileOutputStream stream = new FileOutputStream( "test-output/Backlog-7181.xlsx" ) ) {
      FastExcelReportUtil.processXlsx( report, stream, mock );
    }
    verify( mock, times( 1 ) ).reportProcessingStarted( any( ReportProgressEvent.class ) );
    verify( mock, times( 1 ) ).reportProcessingFinished( any( ReportProgressEvent.class ) );
    verify( mock, atLeastOnce() ).reportProcessingUpdate( any( ReportProgressEvent.class ) );
  }


  @Test
  public void testCsv() throws Exception {
    URL url = getClass().getResource( "Backlog-7181.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner.createTestOutputFile();
    final ReportProgressListener mock = mock( ReportProgressListener.class );
    try ( FileOutputStream stream = new FileOutputStream( "test-output/Backlog-7181.csv" ) ) {
      FastCsvReportUtil.process( report, stream, mock );
    }
    verify( mock, times( 1 ) ).reportProcessingStarted( any( ReportProgressEvent.class ) );
    verify( mock, times( 1 ) ).reportProcessingFinished( any( ReportProgressEvent.class ) );
    verify( mock, atLeastOnce() ).reportProcessingUpdate( any( ReportProgressEvent.class ) );
  }
}
