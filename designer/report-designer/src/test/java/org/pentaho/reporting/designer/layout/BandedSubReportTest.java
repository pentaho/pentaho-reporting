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

package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class BandedSubReportTest extends TestCase {
  public BandedSubReportTest() {
  }

  public BandedSubReportTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRendering() throws Exception {
    MasterReport masterReport = new MasterReport();
    SubReport element = new SubReport();
    masterReport.getReportHeader().addSubReport( element );

    ReportLayouter l = new ReportLayouter( new ReportRenderContext( masterReport ) );
    LogicalPageBox layout = l.layout();
    ModelPrinter.INSTANCE.print( layout );

    MatchFactory.findElementsByAttribute
      ( layout, AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE, element.getElementType() );
  }
}
