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
