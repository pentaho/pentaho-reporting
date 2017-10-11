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

import java.io.UnsupportedEncodingException;


/**
 * This class handles basic password obscurification. Note that it's not really encryption, it's more obfuscation.
 * Passwords are <b>difficult</b> to read, not impossible.
 * <p/>
 * This implementation guarantees consistent results for all valid character ranges.
 */
public final class PasswordObscurification {
  /**
   * The word that is put before a password to indicate an encrypted form. If this word is not present, the password is
   * considered to be NOT encrypted
   */
  public static final String PASSWORD_ENCRYPTED_PREFIX = "Encrypted ";
  private static final String SEED = "3n%34kdim5*\u00a789(10-9)^8B@4513";
  private static final String[] byteToText;

  static {
    byteToText = new String[ 256 ];
    for ( int i = 0; i < 16; i++ ) {
      byteToText[ i ] = '0' + Integer.toHexString( i );
    }
    for ( int i = 16; i < 256; i++ ) {
      byteToText[ i ] = Integer.toHexString( i );
    }
  }

  private PasswordObscurification() {
  }

  public static String byteToHexString( final int b ) {
    return byteToText[ b ];
  }

  public static int charToHex( final int c ) throws UnsupportedEncodingException {
    switch( c ) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
      case 'a':
      case 'A':
        return 10;
      case 'b':
      case 'B':
        return 11;
      case 'c':
      case 'C':
        return 12;
      case 'd':
      case 'D':
        return 13;
      case 'e':
      case 'E':
        return 14;
      case 'f':
      case 'F':
        return 15;
    }
    throw new UnsupportedEncodingException();
  }

  public static String encryptPassword( final String password ) {
    if ( password == null ) {
      return null;
    }
    if ( password.length() == 0 ) {
      return password;
    }

    try {
      final byte[] val = password.getBytes( "UTF-8" );
      final byte[] seed = SEED.getBytes( "UTF-8" );

      final StringBuilder b = new StringBuilder();
      for ( int i = 0; i < val.length; i++ ) {
        final byte seedByte = seed[ i % seed.length ];
        final int b1 = ( 0xFF & ( val[ i ] ^ seedByte ) );
        b.append( byteToText[ b1 ] );
      }

      return b.toString();
    } catch ( UnsupportedEncodingException e ) {
      return password;
    }
  }

  public static String decryptPassword( final String encrypted ) {
    if ( encrypted == null ) {
      return null;
    }
    if ( encrypted.length() == 0 ) {
      return null;
    }

    try {
      final byte[] seed = SEED.getBytes( "UTF-8" );
      final char[] chars = encrypted.toCharArray();
      if ( ( chars.length % 2 ) != 0 ) {
        return null;
      }

      final byte[] b = new byte[ chars.length / 2 ];
      for ( int i = 0; i < b.length; i++ ) {
        final int c1 = charToHex( chars[ i * 2 ] );
        final int c2 = charToHex( chars[ i * 2 + 1 ] );
        final int encodedByte = ( c1 ) * 16 + c2;
        final int encByte = ( 0xFF & encodedByte );
        b[ i ] = (byte) ( encByte ^ seed[ i % seed.length ] );
      }
      return new String( b, "UTF-8" );
    } catch ( Exception e ) {
      return null;
    }
  }

  public static String encryptPasswordWithOptionalEncoding( final String password ) {
    if ( password == null ) {
      return null;
    }
    final String s = encryptPassword( password );
    if ( password.equals( s ) ) {
      return s;
    }
    return PASSWORD_ENCRYPTED_PREFIX + s;
  }

  /**
   * Decrypts a password if it contains the prefix "Encrypted "
   *
   * @param password The encrypted password
   * @return The decrypted password or the original value if the password doesn't start with "Encrypted "
   */
  public static String decryptPasswordWithOptionalEncoding( final String password ) {
    if ( !StringUtils.isEmpty( password ) && password.startsWith( PASSWORD_ENCRYPTED_PREFIX ) ) {
      return PasswordObscurification.decryptPassword( password.substring( PASSWORD_ENCRYPTED_PREFIX.length() ) );
    }
    return password;
  }

}













































































