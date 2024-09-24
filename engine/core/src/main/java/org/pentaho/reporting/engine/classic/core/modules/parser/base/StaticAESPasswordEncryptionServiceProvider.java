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
