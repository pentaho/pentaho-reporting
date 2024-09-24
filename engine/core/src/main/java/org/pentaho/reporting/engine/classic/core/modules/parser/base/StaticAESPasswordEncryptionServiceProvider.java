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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.SecurePasswordEncryption;

/**
 * If you use dependency injection to provide your own key make sure that you also redefine the prefix.
 */
public class StaticAESPasswordEncryptionServiceProvider implements PasswordEncryptionServiceProvider {
  private SecurePasswordEncryption passwordEncryption;
  private String key;
  private String prefix;

  public StaticAESPasswordEncryptionServiceProvider() {
    prefix = "static-aes";
    passwordEncryption = new SecurePasswordEncryption();
    key =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.parser.base.StaticAESPassword",
            "fh34\u342228h%\u1234$3*@652!!" );
  }

  public String getKey() {
    return key;
  }

  public void setKey( final String key ) {
    this.key = key;
  }

  public void setPrefix( final String prefix ) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public String encrypt( final String rawPassword ) {
    try {
      return passwordEncryption.encryptPassword( rawPassword, key );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public String decrypt( final String encryptedPassword ) {
    if ( encryptedPassword == null ) {
      return null;
    }
    if ( "".equals( encryptedPassword ) ) {
      return "";
    }
    try {
      return passwordEncryption.decryptPassword( encryptedPassword, key );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }
}
