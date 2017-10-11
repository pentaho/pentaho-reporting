/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.complextext;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.layout.text.ComplexTextFactory;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointUtilities;

import java.util.Arrays;

public class ComplexTextFactoryTest {
  private String[] processText( String t ) {
    int[] ints = CodePointUtilities.charsToCodepoint( t );
    return ComplexTextFactory.processText( ints, 0, ints.length ).toArray( new String[0] );
  }

  @Test
  public void testSplit() {
    assertArrayEquals( new String[] { "", "", "", "Test", "", "" }, processText( "\n\r\n\r\nTest\n\r\n" ) );
    assertArrayEquals( new String[] { "", "", "Test", "", "Test2", "" }, processText( "\r\rTest\r\rTest2\r" ) );
    assertArrayEquals( new String[] { "", "", "Test", "", "Test2", "" }, processText( "\n\nTest\n\nTest2\n" ) );
    assertArrayEquals( new String[] { "", "Test", "", "Test2" }, processText( "\r\nTest\r\n\r\nTest2" ) );
    assertArrayEquals( new String[] { "", "Test", "Test2" }, processText( "\r\nTest\r\nTest2" ) );

    assertArrayEquals( new String[] { "", "", "Test", "" }, processText( "\n\nTest\r\n" ) );
    assertArrayEquals( new String[] { "", "", "Test", "" }, processText( "\r\n\r\nTest\r\n" ) );
    assertArrayEquals( new String[] { "", "Test", "" }, processText( "\r\nTest\r\n" ) );
  }

  private void assertArrayEquals( String[] g, String[] b ) {
    Assert.assertEquals( "Not the same" + Arrays.asList( g ) + " - " + Arrays.asList( b ), Arrays.asList( g ), Arrays
        .asList( b ) );
  }

}
