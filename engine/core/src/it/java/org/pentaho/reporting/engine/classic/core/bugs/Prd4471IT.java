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


package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupFooterType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4471IT extends TestCase {
  public Prd4471IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportRun() throws Exception {
    final MasterReport elements = DebugReportRunner.parseGoldenSampleReport( "Prd-4471.prpt" );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( elements, 0 );

    final RenderNode[] elementsByElementType =
        MatchFactory.findElementsByElementType( logicalPageBox.getRepeatFooterArea(), GroupFooterType.INSTANCE );
    assertEquals( 0, elementsByElementType.length );
  }
}
