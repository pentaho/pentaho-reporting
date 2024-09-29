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


package org.pentaho.reporting.designer.actions.elements;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.elements.LayerDownAction;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;

import java.util.ArrayList;

public class LayerDownActionTest extends TestCase {
  private class TestLayerDownAction extends LayerDownAction {
    private TestLayerDownAction() {
    }

    @Override
    public boolean collectChange( final Object[] selectedElements,
                                  final AbstractReportDefinition report,
                                  final ArrayList undos ) {
      return super.collectChange( selectedElements, report, undos );
    }
  }

  public LayerDownActionTest() {
  }

  public LayerDownActionTest( final String s ) {
    super( s );
  }

  @Override
  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testLayerDown() {
    final MasterReport report = new MasterReport();
    final PageHeader pageHeader = report.getPageHeader();
    final Element first = new Element();
    final Element second = new Element();
    final Element third = new Element();

    pageHeader.addElement( first );
    pageHeader.addElement( second );
    pageHeader.addElement( third );

    final Element[] selectedElements = new Element[] { second, third };
    final ArrayList list = new ArrayList();
    assertTrue( new TestLayerDownAction().collectChange( selectedElements, report, list ) );
    assertEquals( 2, list.size() );

    assertEquals( pageHeader.getElement( 0 ).getObjectID(), selectedElements[ 0 ].getObjectID() );
    assertEquals( pageHeader.getElement( 1 ).getObjectID(), selectedElements[ 1 ].getObjectID() );
  }
}
