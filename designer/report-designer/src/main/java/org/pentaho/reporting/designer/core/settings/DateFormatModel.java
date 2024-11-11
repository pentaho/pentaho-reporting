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


package org.pentaho.reporting.designer.core.settings;

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DateFormatModel {
  private EventListenerList eventListeners;
  private Preferences properties;
  private static final String[] EMPTY_FORMATS = new String[ 0 ];

  public DateFormatModel() {
    eventListeners = new EventListenerList();
    properties = Preferences.userRoot().node( "org/pentaho/reporting/designer/dateformats" ); // NON-NLS
  }

  public void addSettingsListener( final SettingsListener listener ) {
    eventListeners.add( SettingsListener.class, listener );
  }

  public void removeSettingsListener( final SettingsListener listener ) {
    eventListeners.remove( SettingsListener.class, listener );
  }

  public String[] getNumberFormats() {
    try {
      final ArrayList<String> retval = getFormatsAsList();
      if ( retval.isEmpty() == false ) {
        return retval.toArray( new String[ retval.size() ] );
      }
      return ModelUtility.getDateFormats();
    } catch ( final BackingStoreException e ) {
      // ignore ..
      UncaughtExceptionsModel.getInstance().addException( e );
      return EMPTY_FORMATS;
    }
  }

  private ArrayList<String> getFormatsAsList()
    throws BackingStoreException {
    final ArrayList<String> retval = new ArrayList<String>();
    final String[] strings = properties.keys();
    Arrays.sort( strings );
    final int maxFiles = Math.min( 10, strings.length );
    for ( int i = 0; i < maxFiles; i++ ) {
      final String key = strings[ i ];
      final String file = properties.get( key, null );
      retval.add( file );
    }

    if ( retval.size() != strings.length ) {
      // store ..
      store( retval );
    }
    return retval;
  }

  private void store( final List<String> files ) throws BackingStoreException {
    properties.clear();
    for ( int i = 0; i < files.size(); i++ ) {
      final String file = files.get( i );
      properties.put( String.valueOf( i ), file );
    }
    properties.flush();
  }

  public void setNumberFormats( final String[] file ) {
    try {
      store( Arrays.asList( file ) );
    } catch ( BackingStoreException e ) {
      // ignore ..
      UncaughtExceptionsModel.getInstance().addException( e );
    }

    fireSettingsChanged();

  }

  private void fireSettingsChanged() {
    final SettingsListener[] listeners = eventListeners.getListeners( SettingsListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final SettingsListener listener = listeners[ i ];
      listener.settingsChanged();
    }
  }

  public void clear() {
    try {
      properties.clear();
      fireSettingsChanged();

    } catch ( BackingStoreException e ) {
      // ignore ..
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }
}
