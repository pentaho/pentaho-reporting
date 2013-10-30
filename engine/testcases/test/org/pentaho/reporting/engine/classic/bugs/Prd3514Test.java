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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.bugs;

import java.awt.Dimension;
import java.lang.reflect.Field;

import junit.framework.TestCase;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.BarcodeWrapper;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesType;

public class Prd3514Test extends TestCase
{
  public Prd3514Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testDefaultBarcode() throws Exception
  {
    assertTrue(EnvironmentFactory.getEnvironment() instanceof HeadlessEnvironment);
    assertEquals("false", ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.base.VerboseCellMarkers"));
    
    Element e = new Element();
    e.setElementType(new SimpleBarcodesType());
    e.setAttribute(SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE, "code128");
    e.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "S12_1099");
    e.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 15.053f);
    e.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 129f);
    e.getStyle().setStyleProperty(ElementStyleKeys.PADDING_LEFT, 5f);
    e.getStyle().setStyleProperty(ElementStyleKeys.PADDING_RIGHT, 5f);
    e.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, 8);
    e.getStyle().setStyleProperty(TextStyleKeys.FONT, "Tahoma");

    final Object value = e.getElementType().getValue(new DebugExpressionRuntime(), e);
    assertTrue(value instanceof BarcodeWrapper);
    final BarcodeWrapper w = (BarcodeWrapper) value;
    w.setStyleSheet(e.getStyle());
    final Barcode barcode = w.getBarcode();
    final Field barWidth = Barcode.class.getDeclaredField("barWidth");
    barWidth.setAccessible(true);
    final Field barHeight = Barcode.class.getDeclaredField("barHeight");
    barHeight.setAccessible(true);
    assertEquals(1, barWidth.get(barcode));
    assertEquals(18, barHeight.get(barcode));
    assertEquals(new Dimension(132, 18), w.getPreferredSize());
  }
}
