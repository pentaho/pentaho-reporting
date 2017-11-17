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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.BarcodeWrapper;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesType;

import java.awt.*;
import java.lang.reflect.Field;

public class Prd3514Test extends TestCase {
  public Prd3514Test() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testDefaultBarcode() throws Exception {
    assertTrue( EnvironmentFactory.getEnvironment() instanceof HeadlessEnvironment );
    assertEquals( "false", ClassicEngineBoot.getInstance().getGlobalConfig()
      .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.VerboseCellMarkers" ) );

    Element e = new Element();
    e.setElementType( new SimpleBarcodesType() );
    e.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE, "code128" );
    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "S12_1099" );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 15.053f );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 129f );
    e.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, 5f );
    e.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, 5f );
    e.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 8 );
    e.getStyle().setStyleProperty( TextStyleKeys.FONT, "Tahoma" );

    final Object value = e.getElementType().getValue( new DebugExpressionRuntime(), e );
    assertTrue( value instanceof BarcodeWrapper );
    final BarcodeWrapper w = (BarcodeWrapper) value;
    w.setStyleSheet( e.getStyle() );
    final Barcode barcode = w.getBarcode();
    final Field barWidth = Barcode.class.getDeclaredField( "barWidth" );
    barWidth.setAccessible( true );
    final Field barHeight = Barcode.class.getDeclaredField( "barHeight" );
    barHeight.setAccessible( true );
    assertEquals( 1, barWidth.get( barcode ) );
    assertEquals( 18, barHeight.get( barcode ) );
    assertEquals( new Dimension( 132, 18 ), w.getPreferredSize() );
  }

  public void testWeirdTocLayout() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3514.prpt" );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY, "false" );
    SubReport toc = (SubReport) report.getReportHeader().getElement( 0 );
    Band paragraph = (Band) toc.getItemBand().getElement( 1 );
    paragraph.setName( "outer-box" );
    paragraph.getElement( 1 ).setName( "dotField" );
    paragraph.getElement( 0 ).setName( "textField" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox );

    RenderBox outerBox = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "outer-box" );
    RenderNode dotFieldBox = MatchFactory.findElementByName( logicalPageBox, "dotField" );
    RenderNode textFieldBox = MatchFactory.findElementByName( logicalPageBox, "textField" );
    assertNotNull( outerBox );
    assertNotNull( dotFieldBox );
    assertNotNull( textFieldBox );

    assertEquals( StrictGeomUtility.toInternalValue( 92 ), outerBox.getY() );
    // report-header of master-report defines that the v-align for all its childs should be 'middle'
    assertEquals( StrictGeomUtility.toInternalValue( 94.988 ), dotFieldBox.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 94.988 ), textFieldBox.getY() );

    // box only contains one line, and min-size is set to 8, max size = 20, so the line-height of 14.024 is used.
    assertEquals( StrictGeomUtility.toInternalValue( 14.024 ), outerBox.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 14.024 ), outerBox.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 14 ), dotFieldBox.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 14 ), textFieldBox.getHeight() );
  }
}
