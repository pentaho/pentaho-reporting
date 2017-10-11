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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

public class SecurePasswordEncryption {
  private static final String[] byteToText;
  private static final byte[] SALT = { 0x12, 0x56, (byte) 0x89, (byte) 0xFE, (byte) 0xAB, (byte) 0xC5, 0x7F, 0x01 };

  static {
    byteToText = new String[ 256 ];
    for ( int i = 0; i < 16; i++ ) {
      byteToText[ i ] = '0' + Integer.toHexString( i );
    }
    for ( int i = 16; i < 256; i++ ) {
      byteToText[ i ] = Integer.toHexString( i );
    }
  }


  public String encryptPassword( final String password, final String key )
    throws GeneralSecurityException, UnsupportedEncodingException {
    final SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA1" );
    final KeySpec spec = new PBEKeySpec( key.toCharArray(), SALT, 1024, 128 );
    final SecretKey tmp = factory.generateSecret( spec );
    final SecretKey secret = new SecretKeySpec( tmp.getEncoded(), "AES" );
    return encryptPassword( password, secret, "AES/CBC/PKCS5PADDING" );
  }

  public String encryptPassword( final String password, final SecretKey key, final String algorithm )
    throws GeneralSecurityException, UnsupportedEncodingException {
    final Cipher c = Cipher.getInstance( algorithm );
    c.init( Cipher.ENCRYPT_MODE, key );
    final byte[] encryptedData = c.doFinal( password.getBytes( "UTF-8" ) );
    final StringBuffer b = new StringBuffer();
    final byte[] iv = c.getIV();
    appendAsHexString( intToByte( iv.length ), b );
    appendAsHexString( iv, b );
    appendAsHexString( intToByte( encryptedData.length ), b );
    appendAsHexString( encryptedData, b );
    return b.toString();
  }

  protected static void appendAsHexString( final byte[] encryptedData, final StringBuffer b ) {
    for ( int i = 0; i < encryptedData.length; i++ ) {
      final int b1 = ( 0xFF & ( encryptedData[ i ] ) );
      b.append( byteToText[ b1 ] );
    }
  }

  protected static byte[] intToByte( final int value ) {
    final byte[] b = new byte[ 4 ];
    b[ 0 ] = (byte) ( ( value >> 24 ) & 0xFF );
    b[ 1 ] = (byte) ( ( value >> 16 ) & 0xFF );
    b[ 2 ] = (byte) ( ( value >> 8 ) & 0xFF );
    b[ 3 ] = (byte) ( ( value ) & 0xFF );
    return b;
  }

  public String decryptPassword( final String password,
                                 final String key ) throws UnsupportedEncodingException, GeneralSecurityException {
    final SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA1" );
    final KeySpec spec = new PBEKeySpec( key.toCharArray(), SALT, 1024, 128 );
    final SecretKey tmp = factory.generateSecret( spec );
    final SecretKey secret = new SecretKeySpec( tmp.getEncoded(), "AES" );
    return decryptPassword( password, secret, "AES/CBC/PKCS5PADDING" );
  }

  public String decryptPassword( final String password, final SecretKey key, final String algorithm )
    throws UnsupportedEncodingException, GeneralSecurityException {
    final byte[] b = stringToBytes( password );
    if ( b == null ) {
      return null;
    }

    final int ivLength = bytesToInt( b, 0 );
    final byte[] iv = new byte[ ivLength ];
    System.arraycopy( b, 4, iv, 0, ivLength );
    final int dataLength = bytesToInt( b, 4 + ivLength );
    final byte[] data = new byte[ dataLength ];
    System.arraycopy( b, 8 + ivLength, data, 0, dataLength );

    final Cipher c = Cipher.getInstance( algorithm );
    c.init( Cipher.DECRYPT_MODE, key, new IvParameterSpec( iv ) );
    final byte[] decryptedData = c.doFinal( data );


    return new String( decryptedData, "UTF-8" );

  }

  protected static byte[] stringToBytes( final String password )
    throws UnsupportedEncodingException {
    final char[] chars = password.toCharArray();
    if ( ( chars.length % 2 ) != 0 ) {
      return null;
    }

    final byte[] b = new byte[ chars.length / 2 ];
    for ( int i = 0; i < b.length; i++ ) {
      final int c1 = PasswordObscurification.charToHex( chars[ i * 2 ] );
      final int c2 = PasswordObscurification.charToHex( chars[ i * 2 + 1 ] );
      final int encodedByte = ( c1 ) * 16 + c2;
      final int encByte = ( 0xFF & encodedByte );
      b[ i ] = (byte) ( encByte );
    }
    return b;
  }

  protected static int bytesToInt( final byte[] data, final int offset ) {
    int retval = 0;
    retval |= ( ( data[ offset ] & 0xFF ) << 24 );
    retval |= ( ( data[ offset + 1 ] & 0xFF ) << 16 );
    retval |= ( ( data[ offset + 2 ] & 0xFF ) << 8 );
    retval |= ( ( data[ offset + 3 ] & 0xFF ) );
    return retval;
  }

}
