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


package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

public class PasswordObscurificationTest extends TestCase {
  public PasswordObscurificationTest() {
  }

  public void testKnownPassword() {
    assertEquals( "520c4657510d0301045f41", PasswordObscurification.encryptPassword( "abcdefghijk" ) );
    assertEquals( "520c4657510d0301045f41ae", PasswordObscurification.encryptPassword( "abcdefghijkl" ) );
    assertEquals( "520c4657510d0301045f4183", PasswordObscurification48.encryptPassword( "abcdefghijkl" ) );
  }

  public void testEncode() throws Exception {
    final String enc1 = PasswordObscurification.encryptPassword( "test" );
    assertNotNull( enc1 );
    assertEquals( "test", PasswordObscurification.decryptPassword( enc1 ) );

    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < 65535; i++ ) {
      if ( i >= 0xD800 && i <= 0xDFFF ) {
        // ignore surrogate space
        b.append( ' ' );
        continue;
      }
      if ( i >= 0xE000 && i <= 0xF8FF ) {
        // ignore private space 
        b.append( ' ' );
        continue;
      }
      b.append( (char) i );
    }

    final String originalText = b.toString();
    final String enc2 = PasswordObscurification.encryptPassword( originalText );
    assertNotNull( enc2 );

    final String decrypted = PasswordObscurification.decryptPassword( enc2 );
    final char[] originalChars = originalText.toCharArray();
    final char[] decodedChars = decrypted.toCharArray();
    assertEquals( originalChars.length, decodedChars.length );
    for ( int i = 0; i < decodedChars.length; i++ ) {
      final char decodedChar = decodedChars[ i ];
      final char orig = originalChars[ i ];
      assertEquals( "i=" + Integer.toHexString( i ), decodedChar, orig );
    }
  }
}
