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

public class SecurePasswordEncryptionTest extends TestCase {
  public void testAll() throws Exception {

    final String testText = "asdasdk2038";

    final SecurePasswordEncryption se = new SecurePasswordEncryption();
    final StringBuffer b = new StringBuffer();
    se.appendAsHexString( testText.getBytes( "UTF-8" ), b );
    final byte[] testBytes = se.stringToBytes( b.toString() );
    assertEquals( testText, new String( testBytes, "UTF-8" ) );
    assertTrue( Integer.MAX_VALUE == se.bytesToInt( se.intToByte( Integer.MAX_VALUE ), 0 ) );
    assertTrue( Integer.MIN_VALUE == se.bytesToInt( se.intToByte( Integer.MIN_VALUE ), 0 ) );

    final String s = se.encryptPassword( "my password to encrypt", "secret-key" );
    final String s1 = se.decryptPassword( s, "secret-key" );
    assertEquals( s1, "my password to encrypt" );
  }
}
