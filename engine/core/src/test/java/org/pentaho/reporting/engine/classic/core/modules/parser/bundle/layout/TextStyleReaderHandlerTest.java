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
 * Copyright (c) 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TextStyleReaderHandlerTest {
  private static String URI = "http://reporting.pentaho.org/namespaces/engine/classic/bundle/style/1.0";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testStartParsing() {
    TextStyleReadHandler handler = Mockito.spy( new TextStyleReadHandler() ) ;
    Mockito.doReturn( URI ).when( handler ).getUri();
    ElementStyleSheet sheet = new ElementStyleSheet();
    handler.setStyleSheet( sheet );
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute( URI,"word-break", "" , "boolean" , "false" );
    try {
      handler.startParsing( attributes );
      Assert.assertFalse( sheet.getBooleanStyleProperty( TextStyleKeys.WORDBREAK ) );
    } catch (SAXException e) {
      Assert.fail();
    }
  }
}
