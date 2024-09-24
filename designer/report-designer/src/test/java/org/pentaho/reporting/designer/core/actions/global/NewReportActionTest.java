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
