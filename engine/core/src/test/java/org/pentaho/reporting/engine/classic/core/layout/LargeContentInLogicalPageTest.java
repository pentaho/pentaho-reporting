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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.geom.Point2D;

public class LargeContentInLogicalPageTest extends TestCase {
  public LargeContentInLogicalPageTest() {
  }

  public LargeContentInLogicalPageTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private Element createLabel() {
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( "Large label" );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( new Integer( 10 ) );
    labelFactory.setBold( Boolean.TRUE );
    labelFactory.setAbsolutePosition( new Point2D.Double( 0, 0.0 ) );
    labelFactory.setMinimumSize( new FloatDimension( 4000, 10.0f ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.LEFT );
    return labelFactory.createElement();
  }

  /**
   * If a large element consumes more space as the parent, the parent must expand. Only valid for reports with
   * compatibility level >= 4.0
   *
   * @throws Exception
   */
  public void testTrunk() throws Exception {
    MasterReport report = new MasterReport();
    report.getNoDataBand().addElement( createLabel() );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    assertEquals( StrictGeomUtility.toInternalValue( 4000 ), logicalPageBox.getWidth() );
  }
}
