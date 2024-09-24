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

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.libraries.base.util.PasswordObscurification48;

public class Obscurificate48PasswordEncryptionServiceProvider implements PasswordEncryptionServiceProvider {
  public static final String SERVICE_TAG = "encrypted";

  public Obscurificate48PasswordEncryptionServiceProvider() {
  }

  public String getPrefix() {
    return SERVICE_TAG;
  }

  public String encrypt( final String rawPassword ) {
    throw new UnsupportedOperationException();
  }

  public String decrypt( final String encryptedPassword ) {
    if ( encryptedPassword == null ) {
      return null;
    }
    if ( "".equals( encryptedPassword ) ) {
      return "";
    }
    return PasswordObscurification48.decryptPassword( encryptedPassword );
  }
}
