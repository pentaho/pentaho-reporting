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

package org.pentaho.reporting.designer.core.settings;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.event.EventListenerList;
import java.io.File;
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
public class RecentFilesModel {
  private EventListenerList eventListeners;
  private Preferences properties;
  private static final File[] EMPTY_FILES = new File[ 0 ];

  public RecentFilesModel() {
    eventListeners = new EventListenerList();
    properties = Preferences.userRoot().node( "org/pentaho/reporting/designer/recent-files" ); // NON-NLS
  }

  public void addSettingsListener( final SettingsListener listener ) {
    eventListeners.add( SettingsListener.class, listener );
  }

  public void removeSettingsListener( final SettingsListener listener ) {
    eventListeners.remove( SettingsListener.class, listener );
  }

  public File[] getRecentFiles() {
    try {
      final ArrayList<File> retval = getFilesAsList();
      return retval.toArray( new File[ retval.size() ] );
    } catch ( final BackingStoreException e ) {
      // ignore ..
      UncaughtExceptionsModel.getInstance().addException( e );
      return EMPTY_FILES;
    }
  }

  private ArrayList<File> getFilesAsList()
    throws BackingStoreException {
    final ArrayList<File> retval = new ArrayList<File>();
    final String[] strings = properties.keys();
    Arrays.sort( strings );
    final int maxFiles = Math.min( 10, strings.length );
    for ( int i = 0; i < maxFiles; i++ ) {
      final String key = strings[ i ];
      final File file = new File( properties.get( key, null ) );

      if ( file.isFile() == false ) {
        continue;
      }
      if ( file.canRead() == false ) {
        continue;
      }

      retval.add( file );
    }

    if ( retval.size() != strings.length ) {
      // store ..
      store( retval );
    }
    return retval;
  }

  private void store( final List files ) throws BackingStoreException {
    properties.clear();
    for ( int i = 0; i < files.size(); i++ ) {
      final File file = (File) files.get( i );
      properties.put( String.valueOf( i ), file.getAbsolutePath() );
    }
    properties.flush();
  }

  public void addFile( final File file ) {
    try {
      final ArrayList<File> filesAsList = getFilesAsList();
      final int idx = filesAsList.indexOf( file );
      if ( idx == -1 ) {
        filesAsList.add( 0, file );
        while ( filesAsList.size() > 10 ) {
          filesAsList.remove( filesAsList.remove( filesAsList.size() - 1 ) );
        }
      } else {
        filesAsList.remove( idx );
        filesAsList.add( 0, file );
      }
      store( filesAsList );

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
