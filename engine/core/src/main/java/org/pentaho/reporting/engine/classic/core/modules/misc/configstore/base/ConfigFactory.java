/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base;

/**
 * The config factory is used to access the currently active config storage implementation. The implementation itself
 * allows to read or store a set of properties stored under a certain path.
 *
 * @author Thomas Morgner
 */
public final class ConfigFactory {
  /**
   * The selector configuration key that defines the active config storage implementation.
   */
  public static final String CONFIG_TARGET_KEY = "org.pentaho.reporting.engine.classic.core.ConfigStore"; //$NON-NLS-1$

  /**
   * The singleton instance of the config factory.
   */
  private static ConfigFactory factory;
  /**
   * The user storage is used to store user dependend settings.
   */
  private ConfigStorage userStorage;
  /**
   * The system storage is used to store system wide settings.
   */
  private ConfigStorage systemStorage;

  /**
   * Returns the singleton instance of the config factory.
   *
   * @return the config factory
   */
  public static synchronized ConfigFactory getInstance() {
    if ( factory == null ) {
      factory = new ConfigFactory();
      factory.defineSystemStorage( new NullConfigStorage() );
      factory.defineUserStorage( new NullConfigStorage() );
    }
    return factory;
  }

  /**
   * DefaultConstructor.
   */
  private ConfigFactory() {
  }

  /**
   * Defines the user storage implementation that should be used. This method should only be called by the module
   * initialization methods.
   *
   * @param storage
   *          the user settings storage implementation.
   */
  public void defineUserStorage( final ConfigStorage storage ) {
    if ( storage == null ) {
      throw new NullPointerException();
    }
    this.userStorage = storage;
  }

  /**
   * Defines the system storage implementation that should be used. This method should only be called by the module
   * initialization methods.
   *
   * @param storage
   *          the system settings storage implementation.
   */
  public void defineSystemStorage( final ConfigStorage storage ) {
    if ( storage == null ) {
      throw new NullPointerException();
    }
    this.systemStorage = storage;
  }

  /**
   * Returns the user settings storage implementation used in the config subsystem.
   *
   * @return the user settingsstorage provider.
   */
  public ConfigStorage getUserStorage() {
    return userStorage;
  }

  /**
   * Returns the system settings storage implementation used in the config subsystem.
   *
   * @return the system settings storage provider.
   */
  public ConfigStorage getSystemStorage() {
    return systemStorage;
  }

  /**
   * Checks whether the given string denotes a valid config storage path. Such an path must not contain whitespaces or
   * non-alphanumeric characters.
   *
   * @param path
   *          the path that should be tested.
   * @return true, if the path is valid, false otherwise.
   */
  public static boolean isValidPath( final String path ) {
    final char[] data = path.toCharArray();
    for ( int i = 0; i < data.length; i++ ) {
      if ( Character.isJavaIdentifierPart( data[i] ) == false ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Encodes the given configuration path. All non-ascii characters get replaced by an escape sequence.
   *
   * @param path
   *          the path.
   * @return the translated path.
   */
  public static String encodePath( final String path ) {
    final char[] data = path.toCharArray();
    final StringBuffer encoded = new StringBuffer( path.length() );
    for ( int i = 0; i < data.length; i++ ) {
      if ( data[i] == '$' ) {
        // double quote
        encoded.append( '$' );
        encoded.append( '$' );
      } else if ( Character.isJavaIdentifierPart( data[i] ) == false ) {
        // padded hex string
        encoded.append( '$' );
        final String hex = Integer.toHexString( data[i] );
        for ( int x = hex.length(); x < 4; x++ ) {
          encoded.append( '0' );
        }
        encoded.append( hex );
      } else {
        encoded.append( data[i] );
      }
    }
    return encoded.toString();
  }
}
