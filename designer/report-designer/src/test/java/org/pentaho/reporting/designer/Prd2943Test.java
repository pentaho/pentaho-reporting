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
 * Copyright (c) 2018 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.designer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class Prd2943Test {
  protected MasterReport report;
  private SubReport subReport;
  private GlobalAuthenticationStore authStore;
  private SubReport siblingSubReport;

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    authStore = new GlobalAuthenticationStore();

    report = new MasterReport();
    subReport = new SubReport();
    siblingSubReport = new SubReport();
    report.getReportHeader().addSubReport( subReport );
    report.getReportFooter().addSubReport( siblingSubReport );
  }

  @Test
  public void dirtyFlagDependsOnStateChangesMasterReportVersion() {
    // CloseReportAction#performCloseReport is a static method so we cannot mock the calls within it.
    // The sources show that the method depends on ReportDesignerDocumentContext#isReportFileModified to indicate
    // whether a report is clean.
    //
    // When saving a report, the change tracker is reset to indicate that the report is not considered
    // a modified report.

    ReportDocumentContext masterContext = new ReportRenderContext( report );
    Assert.assertFalse( masterContext.isChanged() );

    ReportDocumentContext subreportContext = new ReportRenderContext( report, subReport, masterContext, authStore );
    Assert.assertFalse( subreportContext.isChanged() );

    ReportDocumentContext siblingContext = new ReportRenderContext( report, siblingSubReport, masterContext, authStore );
    Assert.assertFalse( siblingContext.isChanged() );

    // When we modify a report element, we trigger a change in the change tracker on the master report
    // and any subreport that is parent to the element being changed.
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.POS_X, 10f );

    // Changing a master report element does not make changes to a subreport. But as subreports all live in
    // the same PRPT bundle, the report in memory is no longer representing the same state as the report on
    // disk. Therefore this flag should report "true" to indicate that.
    Assert.assertTrue( masterContext.isChanged() );
    Assert.assertTrue( subreportContext.isChanged() );
    Assert.assertTrue( siblingContext.isChanged() );

    // when we save an report, the report-context's #resetChangeTracker method is called. This should
    // reset the flag for the report, and by extension to all subreports that share the same master-report
    // (as all are saved to the same file bundle).
    masterContext.resetChangeTracker();
    Assert.assertFalse( masterContext.isChanged() );
    Assert.assertFalse( subreportContext.isChanged() );
    Assert.assertFalse( siblingContext.isChanged() );
  }

  @Test
  public void dirtyFlagDependsOnStateChangesSubReportVersion() {
    // CloseReportAction#performCloseReport is a static method so we cannot mock the calls within it.
    // The sources show that the method depends on ReportDesignerDocumentContext#isReportFileModified to indicate
    // whether a report is clean.
    //
    // When saving a report, the change tracker is reset to indicate that the report is not considered
    // a modified report.

    ReportDocumentContext masterContext = new ReportRenderContext( report );
    Assert.assertFalse( masterContext.isChanged() );

    ReportDocumentContext subreportContext = new ReportRenderContext( report, subReport, masterContext, authStore );
    Assert.assertFalse( subreportContext.isChanged() );

    ReportDocumentContext siblingContext = new ReportRenderContext( report, siblingSubReport, masterContext, authStore );
    Assert.assertFalse( siblingContext.isChanged() );

    // When we modify a report element, we trigger a change in the change tracker on the master report
    // and any subreport that is parent to the element being changed.
    subReport.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.POS_X, 10f );

    // all subreports live in the same PRPT bundle, so any change to any element in the report
    // definition should trigger the dirty flag. The report in memory is no longer representing
    // the same state as the report on disk. Therefore this flag should report "true" to indicate that.
    Assert.assertTrue( masterContext.isChanged() );
    Assert.assertTrue( subreportContext.isChanged() );
    Assert.assertTrue( siblingContext.isChanged() );

    // when we save an report, the report-context's #resetChangeTracker method is called. This should
    // reset the flag for the report, and by extension to all subreports that share the same master-report
    // (as all are saved to the same file).
    subreportContext.resetChangeTracker();
    Assert.assertFalse( masterContext.isChanged() );
    Assert.assertFalse( subreportContext.isChanged() );
    Assert.assertFalse( siblingContext.isChanged() );

  }
}
