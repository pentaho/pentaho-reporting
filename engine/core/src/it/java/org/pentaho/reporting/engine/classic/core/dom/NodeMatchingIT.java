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


package org.pentaho.reporting.engine.classic.core.dom;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.WatermarkType;

public class NodeMatchingIT extends TestCase {
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMatchStandardElements() throws Exception {
    final MasterReport report = new MasterReport();
    assertNotNull( report.getChildElementByType( ItemBandType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( NoDataBandType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( DetailsHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( DetailsFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( GroupDataBodyType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( RelationalGroupType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( ReportHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( ReportFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( PageHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( PageFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( WatermarkType.INSTANCE ) );
  }
}
