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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
