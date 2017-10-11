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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

public class StyleWriterUtilityTest {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testStartParsing() {
    StringWriter stringWriter = new StringWriter();
    XmlWriter writer = new XmlWriter( stringWriter );
    writer.addImpliedNamespace( "http://reporting.pentaho.org/namespaces/engine/classic/bundle/style/1.0", "rep" );
    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setBooleanStyleProperty( TextStyleKeys.WORDBREAK, false );
    try {
      StyleWriterUtility.writeTextStyles( writer, sheet );
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail();
    }
    String res = stringWriter.toString().replace( "\r", "" ).replace( "\n", "" );
    Assert.assertEquals( "<rep:text-styles word-break=\"false\"/>", res );
  }
}
