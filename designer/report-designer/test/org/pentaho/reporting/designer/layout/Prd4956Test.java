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

package org.pentaho.reporting.designer.layout;

import java.awt.geom.Rectangle2D;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.testsupport.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.testsupport.graphics.TestGraphics2D;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd4956Test
{
  @Before
  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  private MasterReport createReport() {
    SubReport sr = new SubReport();
    sr.getPageHeader().addElement(create("SR-Page-Header"));
    sr.getPageFooter().addElement(create("SR-Page-Footer"));
    sr.getReportHeader().addElement(create("SR-Report-Header"));

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport(sr);
    report.getPageHeader().addElement(create("MR-Page-Header"));
    report.getPageFooter().addElement(create("MR-Page-Footer"));
    return report;
  }

  private Element create(String name)
  {
    Element dataItemSR = TableTestUtil.createDataItem(name, 100, 20);
    dataItemSR.setName(name);
    return dataItemSR;
  }

  @Test
  public void testBanded() {

    final MasterReport report = createReport();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
        new ReportRenderContext(report, report, null, globalAuthenticationStore);
    final SubReport subReport = report.getReportHeader().getSubReport(0);
    final ReportRenderContext subContext =
        new ReportRenderContext(report, subReport, masterContext, globalAuthenticationStore);
    final TestRootBandRenderer r = new TestRootBandRenderer(subReport.getPageHeader(), subContext);
    final Rectangle2D bounds = r.getBounds();

    ModelPrinter.INSTANCE.print(r.getLogicalPageDrawable().getLogicalPageBox());
    Assert.assertEquals(new Rectangle2D.Double(0, 20, 468, 72), bounds);

    final TestGraphics2D graphics2D = new ValidateTextGraphics(468, 108);
    Assert.assertTrue(graphics2D.hitClip(10, 10, 1, 1));
    r.draw(graphics2D);

  }
}
