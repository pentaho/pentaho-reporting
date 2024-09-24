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

package org.pentaho.reporting.designer.core.auth;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.settings.prefs.PreferencesMap;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.io.Serializable;
import java.util.Properties;

public class AuthenticationData implements Serializable, PreferencesMap.ConfigurationData {
  private String url;
  private Properties properties;
  private long lastChanged;

  public AuthenticationData( final String url ) {
    this( url, System.currentTimeMillis() );
  }

  public AuthenticationData( final AuthenticationData data ) {
    this.url = data.url;
    this.properties = (Properties) data.properties.clone();
    this.lastChanged = data.lastChanged;
  }

  public AuthenticationData( final String baseUrl, final String username, final String password, final int timeout ) {
    this( baseUrl );
    setOption( AuthenticationStore.USER_KEY, username );
    setOption( AuthenticationStore.PASSWORD_KEY, password );
    setOption( AuthenticationStore.TIMEOUT_KEY, String.valueOf( timeout ) );
  }

  public AuthenticationData( final String url,
                             final long lastChanged ) {
    if ( url == null ) {
      throw new NullPointerException();
    }
    this.url = url;
    this.properties = new Properties();
    this.lastChanged = lastChanged;
  }

  public String getUrl() {
    return url;
  }

  public String getOption( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    return this.properties.getProperty( key );
  }

  public String getKey() {
    return getUrl();
  }

  public String[] getDefinedOptions() {
    //noinspection SuspiciousToArrayCall
    return this.properties.keySet().toArray( new String[ this.properties.size() ] );
  }

  public void setOption( final String key, final String value ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      this.properties.remove( key );
    } else {
      this.properties.setProperty( key, value );
    }
    this.lastChanged = System.currentTimeMillis();
  }

  public long getLastChanged() {
    return lastChanged;
  }

  public void setLastChanged( final long time ) {
    this.lastChanged = time;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof AuthenticationData ) ) {
      return false;
    }

    final AuthenticationData that = (AuthenticationData) o;

    if ( !url.equals( that.url ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return url.hashCode();
  }

  public String getUsername() {
    return getOption( AuthenticationStore.USER_KEY );
  }

  public String getPassword() {
    return getOption( AuthenticationStore.PASSWORD_KEY );
  }

  public int getTimeout() {
    final String s = getOption( AuthenticationStore.TIMEOUT_KEY );
    return ParserUtil.parseInt( s, WorkspaceSettings.getInstance().getConnectionTimeout() );
  }
}
