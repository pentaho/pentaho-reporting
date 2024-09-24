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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class Prd3688IT extends TestCase {
  public Prd3688IT() {
  }

  public Prd3688IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public static Element createDataItem( final String text ) {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    return label;
  }

  public void testPrd3688() throws Exception {
    final MasterReport report = new MasterReport();
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final Element e1 = createDataItem( "Header" );
    e1.setName( "E1" );
    e1.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -37f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 11f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 500f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );

    final Element e2 = createDataItem( "Header" );
    e2.setName( "E2" );
    e2.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -50f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 11f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );

    final Element e3 = createDataItem( "Header" );
    e3.setName( "E3" );
    e3.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 1464f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 3.5f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 12.5f );

    report.getReportHeader().setName( "RH" );
    report.getReportHeader().addElement( e1 );
    report.getReportHeader().addElement( e2 );
    report.getReportHeader().addElement( e3 );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final RenderNode e1Box = MatchFactory.findElementByName( logicalPageBox, "E1" );
    final RenderNode e2Box = MatchFactory.findElementByName( logicalPageBox, "E2" );
    final RenderNode e3Box = MatchFactory.findElementByName( logicalPageBox, "E3" );
    final RenderNode rhBox = MatchFactory.findElementByName( logicalPageBox, "RH" );

    assertEquals( StrictGeomUtility.toInternalValue( 500 ), e1Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e1Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 173.16f ), e1Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 11 ), e1Box.getHeight() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e2Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e2Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), e2Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 11 ), e2Box.getHeight() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e3Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 12.5 ), e3Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 1464f ), e3Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 8 ), e3Box.getHeight() );

    // in non-legacy mode, a parent box expands to enclose all childs, unless prohibited by max-size or preferred-size.
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), rhBox.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), rhBox.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 1464f ), rhBox.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20.5 ), rhBox.getHeight() );
  }

  public void testPrd3688Strict() throws Exception {
    final MasterReport report = new MasterReport();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );

    final Element e1 = createDataItem( "Header" );
    e1.setName( "E1" );
    e1.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -37f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 11f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 500f );
    e1.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );

    final Element e2 = createDataItem( "Header" );
    e2.setName( "E2" );
    e2.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -50f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 11f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    e2.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );

    final Element e3 = createDataItem( "Header" );
    e3.setName( "E3" );
    e3.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 1464f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 3.5f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    e3.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 12.5f );

    report.getReportHeader().setName( "RH" );
    report.getReportHeader().addElement( e1 );
    report.getReportHeader().addElement( e2 );
    report.getReportHeader().addElement( e3 );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final RenderNode e1Box = MatchFactory.findElementByName( logicalPageBox, "E1" );
    final RenderNode e2Box = MatchFactory.findElementByName( logicalPageBox, "E2" );
    final RenderNode e3Box = MatchFactory.findElementByName( logicalPageBox, "E3" );
    final RenderNode rhBox = MatchFactory.findElementByName( logicalPageBox, "RH" );

    assertEquals( StrictGeomUtility.toInternalValue( 500 ), e1Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e1Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 173.16f ), e1Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 11 ), e1Box.getHeight() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e2Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e2Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), e2Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 11 ), e2Box.getHeight() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), e3Box.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 12.5 ), e3Box.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 1464f ), e3Box.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 3.5f ), e3Box.getHeight() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), rhBox.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), rhBox.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 468f ), rhBox.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 16 ), rhBox.getHeight() );

  }
}
