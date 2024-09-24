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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4776IT extends TestCase {
  public Prd4776IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunReport() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4776.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType( logicalPageBox, LabelType.INSTANCE );
    for ( int i = 0; i < elementsByElementType.length; i++ ) {
      RenderNode renderNode = elementsByElementType[i];
      StyleSheet styleSheet = renderNode.getNodeLayoutProperties().getStyleSheet();
      Object styleProperty = styleSheet.getStyleProperty( TextStyleKeys.FONT );
      Assert.assertEquals( "Arial", styleProperty );
    }
  }
}
