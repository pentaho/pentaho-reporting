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

package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.designtime.swing.WeakEventListenerList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PublishSettings {
  private static final Log LOG = LogFactory.getLog( PublishSettings.class );

  private static PublishSettings instance;

  public static synchronized PublishSettings getInstance() {
    if ( instance == null ) {
      instance = new PublishSettings();
    }
    return instance;
  }

  private static final String REMEMBER_SETTINGS = "RememberSettings";

  private Preferences properties;
  private WeakEventListenerList settingsListeners;

  private PublishSettings() {
    properties = Preferences.userRoot().node( "org/pentaho/reporting/designer/pentaho-publish-settings" );
    settingsListeners = new WeakEventListenerList();
  }

  public void flush() {
    try {
      properties.flush();
    } catch ( BackingStoreException e ) {
      // ignore, we cant do anything about it.
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  public boolean isRememberSettings() {
    return getBoolean( REMEMBER_SETTINGS, false );
  }

  public void setRememberSettings( final boolean rememberSettings ) {
    put( REMEMBER_SETTINGS, String.valueOf( rememberSettings ) );
  }

  private void put( final String key, final String value ) {
    if ( key == null ) {
      throw new IllegalArgumentException( "key must not be null" );
    }
    if ( value == null ) {
      throw new IllegalArgumentException( "value must not be null" );
    }
    properties.put( key, value );
    fireSettingsChanged();
  }

  private void put( final String key, final Collection<String> values ) {
    // noinspection ConstantConditions
    if ( key == null ) {
      throw new IllegalArgumentException( "key must not be null" );
    }
    // noinspection ConstantConditions
    if ( values == null ) {
      throw new IllegalArgumentException( "values must not be null" );
    }
    final String[] strings = values.toArray( new String[values.size()] );
    final Preferences preferences = properties.node( key );
    for ( int i = 0; i < strings.length; i++ ) {
      final String string = strings[i];
      preferences.put( String.valueOf( i ), string );
    }

    fireSettingsChanged();
  }

  private String[] getList( final String key ) {
    final Preferences preferences = properties.node( key );
    final ArrayList<String> data = new ArrayList<String>();
    for ( ;; ) {
      final String s = preferences.get( String.valueOf( data.size() ), null );
      if ( s == null ) {
        break;
      }
      data.add( s );
    }
    return data.toArray( new String[data.size()] );
  }

  private Integer getInt( final String key ) {
    final String value = properties.get( key, null );
    if ( value == null ) {
      return null;
    }
    try {
      return Integer.valueOf( value );
    } catch ( NumberFormatException e ) {
      if ( LOG.isDebugEnabled() ) {
        LOG.debug( "PublishSettings.getInt ", e );
      }
      return null;
    }
  }

  private boolean getBoolean( final String key, final boolean defaultValue ) {
    final String value = properties.get( key, null );
    if ( value == null ) {
      return defaultValue;
    }
    return Boolean.valueOf( value );
  }

  private Boolean getBoolean( final String key ) {
    final String value = properties.get( key, null );
    if ( value == null ) {
      return null;
    }
    return Boolean.valueOf( value );
  }

  private String getString( final String key ) {
    return properties.get( key, null );
  }

  public void fireSettingsChanged() {
    final SettingsListener[] listeners = settingsListeners.getListeners( SettingsListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final SettingsListener listener = listeners[i];
      listener.settingsChanged();
    }
  }

}
