/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.base;

public class PlainTextPasswordEncryptionServiceProvider implements PasswordEncryptionServiceProvider {
  public PlainTextPasswordEncryptionServiceProvider() {
  }

  public String getPrefix() {
    return "plain";
  }

  public String encrypt( final String rawPassword ) {
    return rawPassword;
  }

  public String decrypt( final String encryptedPassword ) {
    if ( encryptedPassword == null ) {
      return null;
    }
    if ( "".equals( encryptedPassword ) ) {
      return "";
    }
    return encryptedPassword;
  }
}
