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
