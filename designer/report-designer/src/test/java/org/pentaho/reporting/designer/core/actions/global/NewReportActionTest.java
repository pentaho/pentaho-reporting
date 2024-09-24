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

package org.pentaho.reporting.designer.core.actions.global;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;

public class NewReportActionTest extends TestCase {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testPrepareMasterReport() {
    MasterReport masterReport = NewReportAction.prepareMasterReport();

    assertTrue( masterReport.getAutoSort() );
    assertNull( masterReport.getQuery() );
    assertTrue( masterReport.isQueryLimitInherited() );
    assertTrue( (Boolean) masterReport.getRelationalGroup( 0 ).getHeader().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
    assertTrue( (Boolean) masterReport.getRelationalGroup( 0 ).getFooter().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
    assertTrue( (Boolean) masterReport.getDetailsFooter().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
    assertTrue( (Boolean) masterReport.getDetailsHeader().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
    assertTrue( (Boolean) masterReport.getNoDataBand().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
    assertTrue( (Boolean) masterReport.getWatermark().getAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE ) );
  }
}
