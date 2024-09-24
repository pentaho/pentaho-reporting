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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

import java.io.ByteArrayOutputStream;

public class Prd5143IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testLayoutWorks() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-5143.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
    Assert.assertEquals( 1, elementsByNodeType.length );
  }

  @Test
  public void testFastHtmlExportWork() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-5143.prpt" );
    final ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    final ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastHtmlReportUtil.processStreamHtml( report, boutFast );
    HtmlReportUtil.createStreamHTML( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }
}
