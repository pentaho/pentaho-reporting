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

package org.pentaho.reporting.designer.core.settings.prefs;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.PasswordObscurification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class PreferencesMap<T extends PreferencesMap.ConfigurationData> {
  public static interface ConfigurationData {
    public String getKey();

    public String getOption( String key );

    public void setOption( String key, String value );

    public String[] getDefinedOptions();

    public long getLastChanged();

    public void setLastChanged( long time );
  }

  private static class DataHolder<T extends PreferencesMap.ConfigurationData> {
    private T configurationData;
    private String nodeName;

    private DataHolder( final T configurationData ) {
      this.configurationData = configurationData;
    }

    public T getConfigurationData() {
      return configurationData;
    }

    public String getKey() {
      return configurationData.getKey();
    }

    public String getNodeName() {
      return nodeName;
    }

    public void setNodeName( final String nodeName ) {
      this.nodeName = nodeName;
    }

    public long getLastChanged() {
      return configurationData.getLastChanged();
    }

    public void setLastChanged( final long time ) {
      configurationData.setLastChanged( time );
    }
  }

  private static final String VERSION_KEY = "#version";
  private static final String URL_KEY = "#url";

  private LinkedHashMap<String, DataHolder<T>> backend;
  private LinkedHashMap<String, DataHolder<T>> removedNodes;
  private Preferences storageBackend;

  public PreferencesMap( final Preferences preferences ) {
    if ( preferences == null ) {
      throw new NullPointerException();
    }
    storageBackend = preferences;
    //Preferences.userNodeForPackage(StoredPublishLocations.class).node("StoredPublishLocations");
    backend = new LinkedHashMap<String, DataHolder<T>>();
    removedNodes = new LinkedHashMap<String, DataHolder<T>>();
  }

  protected abstract T create( String key, long time );

  protected T get( final String key ) {
    final DataHolder<T> holder = backend.get( key );
    if ( holder != null ) {
      return holder.getConfigurationData();
    }
    return null;
  }

  protected void init() {
    try {
      sync();
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  protected String[] getKnownKeys() {
    return backend.keySet().toArray( new String[ backend.size() ] );
  }

  protected void add( final T configurationData ) {
    final DataHolder<T> data = new DataHolder<T>( configurationData );
    final DataHolder<T> oldData = backend.put( data.getKey(), data );
    if ( oldData != null ) {
      data.setNodeName( oldData.getNodeName() );
    }
    try {
      sync();
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  protected void remove( final String key ) {
    final DataHolder<T> data = backend.remove( key );
    if ( data != null ) {
      removedNodes.put( key, data );
      try {
        sync();
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  public void sync() throws BackingStoreException {
    final Iterator<DataHolder<T>> removedNodes = this.removedNodes.values().iterator();
    while ( removedNodes.hasNext() ) {
      final DataHolder<T> s = removedNodes.next();
      removedNodes.remove();
      final String nodeName = s.getNodeName();
      if ( nodeName != null ) {
        storageBackend.node( nodeName );
      }
    }

    final LinkedHashMap<String, DataHolder<T>> dataFromStore = new LinkedHashMap<String, DataHolder<T>>();
    final String[] strings = storageBackend.childrenNames();
    Arrays.sort( strings );
    for ( int i = 0; i < strings.length; i++ ) {
      final String name = strings[ i ];
      if ( name.startsWith( "#" ) == false ) {
        continue;
      }
      final Preferences authNode = storageBackend.node( name );
      final String url = authNode.get( URL_KEY, null );
      if ( url == null ) {
        authNode.removeNode();
        continue;
      }

      final long authTime = authNode.getLong( VERSION_KEY, 0 );
      final DataHolder<T> data = new DataHolder<T>( create( url, authTime ) );
      final String[] options = authNode.keys();
      for ( int j = 0; j < options.length; j++ ) {
        final String option = options[ j ];
        if ( option.startsWith( "#" ) ) {
          continue;
        }
        data.getConfigurationData().setOption( option,
          PasswordObscurification.decryptPasswordWithOptionalEncoding( authNode.get( option, null ) ) );
      }

      data.setNodeName( name );
      data.setLastChanged( authTime );

      dataFromStore.put( url, data );
    }

    final ArrayList<DataHolder<T>> dataToWrite = new ArrayList<DataHolder<T>>();
    final ArrayList<DataHolder<T>> datas = new ArrayList<DataHolder<T>>( backend.values() );
    for ( final DataHolder<T> local : datas ) {
      final String url = local.getKey();
      final DataHolder<T> fromStore = dataFromStore.get( url );
      if ( fromStore == null ) {
        // new item ..
        dataToWrite.add( local );
      } else if ( local.getLastChanged() > fromStore.getLastChanged() ) {
        // in-memory item is newer
        dataToWrite.add( local );
        dataFromStore.remove( url );
      } else if ( local.getLastChanged() < fromStore.getLastChanged() ) {
        // external item is newer
        backend.put( url, fromStore );
        dataFromStore.remove( url );
      } else {
        // item is unchanged ..
        dataFromStore.remove( url );
      }
    }

    // all that is left in the collection are new items from the config-store
    this.backend.putAll( dataFromStore );

    // and the newly created in memory items ..
    final HashSet<String> names = new HashSet<String>( Arrays.asList( strings ) );
    for ( int i = 0; i < dataToWrite.size(); i++ ) {
      final DataHolder<T> data = dataToWrite.get( i );
      final String existingName = data.getNodeName();
      String name = null;
      if ( existingName != null ) {
        final Preferences preferences = storageBackend.node( existingName );
        final String key = preferences.get( URL_KEY, null );
        if ( data.getKey().equals( key ) ) {
          name = existingName;
        }
      }
      if ( name == null ) {
        name = generateName( names );
      }
      if ( name != null ) {
        final Preferences subnode = storageBackend.node( name );
        data.setNodeName( name );
        subnode.putLong( VERSION_KEY, data.getLastChanged() );
        subnode.put( URL_KEY, data.getKey() );
        final String[] optionKeys = data.getConfigurationData().getDefinedOptions();
        for ( int j = 0; j < optionKeys.length; j++ ) {
          final String optionKey = optionKeys[ j ];
          if ( optionKey.startsWith( "#" ) ) {
            continue;
          }
          final String password = data.getConfigurationData().getOption( optionKey );
          if ( password != null ) {
            subnode.put( optionKey, PasswordObscurification.encryptPasswordWithOptionalEncoding( password ) );
          }
        }
      }
    }
  }

  private String generateName( final HashSet names ) throws BackingStoreException {
    for ( int i = 0; i < names.size(); i++ ) {
      final String nodeName = "#" + i;
      if ( names.contains( nodeName ) == false ) {
        return nodeName;
      }
    }

    for ( int i = 0; i < 9999; i++ ) {
      final String nodeName = "#" + i;
      if ( storageBackend.nodeExists( nodeName ) == false ) {
        return nodeName;
      }
    }
    return null;
  }


  public String getMostRecentEntry() {
    long age = 0;
    String result = null;
    for ( final Map.Entry<String, DataHolder<T>> entry : backend.entrySet() ) {
      final DataHolder holder = entry.getValue();
      if ( holder.getLastChanged() > age ) {
        age = holder.getLastChanged();
        result = entry.getKey();
      }
    }
    return result;
  }

}
