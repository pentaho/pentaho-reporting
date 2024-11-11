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
