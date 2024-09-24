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

import org.pentaho.reporting.designer.core.settings.prefs.PreferencesMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.Enumeration;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class GlobalAuthenticationStore extends PreferencesMap<AuthenticationData> implements AuthenticationStore {
  private PasswordPolicyManager policyManager;
  private static final String[] EMPTY_STRINGS = new String[ 0 ];

  public GlobalAuthenticationStore() {
    super( Preferences.userNodeForPackage( GlobalAuthenticationStore.class ).node( "AuthStore" ) ); // NON-NLS
    policyManager = PasswordPolicyManager.getInstance();
    init();
  }

  protected AuthenticationData create( final String key, final long time ) {
    return new AuthenticationData( key, time );
  }

  public AuthenticationData getCredentials( final String url ) {
    final AuthenticationData configurationData = get( url );
    if ( configurationData == null ) {
      return null;
    }
    return new AuthenticationData( configurationData );
  }

  public String getUsername( final String url ) {
    final AuthenticationData data = get( url );
    if ( data == null ) {
      return null;
    }
    return data.getOption( USER_KEY );
  }

  public String getPassword( final String url ) {
    final AuthenticationData data = get( url );
    if ( data == null ) {
      return null;
    }
    return data.getOption( PASSWORD_KEY );
  }

  public String getOption( final String url, final String key ) {
    final AuthenticationData data = get( url );
    if ( data == null ) {
      return null;
    }
    return data.getOption( key );
  }

  public String[] getDefinedOptions( final String url ) {
    final AuthenticationData data = get( url );
    if ( data == null ) {
      return EMPTY_STRINGS;
    }
    return data.getDefinedOptions();
  }

  public String[] getKnownURLs() {
    return getKnownKeys();
  }

  public void add( final AuthenticationData authenticationData, final boolean persist ) {
    if ( persist && policyManager.isPasswordStoringAllowed( authenticationData.getUrl() ) ) {
      super.add( authenticationData );
    }
  }

  public void addCredentials( final String url,
                              final String user,
                              final String password,
                              final Properties options,
                              final boolean persist ) {
    if ( persist && policyManager.isPasswordStoringAllowed( url ) ) {
      add( createAuthenticationData( url, user, password, options ), persist );
    }
  }

  public static AuthenticationData createAuthenticationData( final String url,
                                                             final String user,
                                                             final String password,
                                                             final Properties options ) {
    final AuthenticationData data = new AuthenticationData( url );
    if ( options != null ) {
      final Enumeration enumeration = options.propertyNames();
      while ( enumeration.hasMoreElements() ) {
        final String key = (String) enumeration.nextElement();
        final String value = options.getProperty( key );
        data.setOption( key, value );
      }
    }
    data.setOption( USER_KEY, user );
    data.setOption( PASSWORD_KEY, password );
    return data;
  }

  protected void add( final AuthenticationData configurationData ) {
    final ConfigurationData oldData = get( configurationData.getKey() );
    if ( oldData != null ) {
      final String[] strings = oldData.getDefinedOptions();
      for ( int i = 0; i < strings.length; i++ ) {
        final String key = strings[ i ];
        if ( configurationData.getOption( key ) == null ) {
          configurationData.setOption( key, oldData.getOption( key ) );
        }
      }
    }

    super.add( configurationData );
  }

  public void removeCredentials( final String url ) {
    remove( url );
  }

  public int getIntOption( final String path, final String key, final int defaultValue ) {
    final String option = getOption( path, key );
    if ( StringUtils.isEmpty( option ) ) {
      return defaultValue;
    }
    try {
      return Integer.parseInt( option );
    } catch ( Exception e ) {
      return defaultValue;
    }
  }


}
