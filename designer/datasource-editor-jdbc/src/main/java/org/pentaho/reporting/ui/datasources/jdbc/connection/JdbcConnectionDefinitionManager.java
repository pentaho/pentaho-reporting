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
 * Copyright (c) 2007 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.connection;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Manages the list of local database connections for this application.
 * <p/>
 * The list of local connections is contained in the user preferences for this user. The structure of the preferences is
 * as follows:
 * <ul>
 * <li>One node per connection - the name of the node is the JNDI Connection name
 * <li>Under each node, the following set of string keys and values which define each connection
 * <ul>
 * <li>driver - the JDBC driver class
 * <li>url - the URL of the database
 * <li>username - the username used to connect to the database
 * <li>password - the password used to connect to the database
 * </ul>
 * </ul>
 */
public class JdbcConnectionDefinitionManager {
  // Logger
  private static final Log log = LogFactory.getLog( JdbcConnectionDefinitionManager.class );

  // The default connection (used if no other connections can be loaded)
  private static final JdbcConnectionDefinition SAMPLE_DATA_JNDI_SOURCE = new JndiConnectionDefinition( "SampleData",
      "SampleData", "Hypersonic", null, null );
  private static final JdbcConnectionDefinition SAMPLE_DATA_DRIVER_SOURCE = new DriverConnectionDefinition(
      "SampleData (Hypersonic)", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost:9001/sampledata",
      "pentaho_user", "password" );
  private static final JdbcConnectionDefinition SAMPLE_DATA_MEMORY_SOURCE = new DriverConnectionDefinition(
      "SampleData (Memory)", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:SampleData", "pentaho_user", "password" );
  private static final JdbcConnectionDefinition LOCAL_SAMPLE_DATA_DRIVER_SOURCE = new DriverConnectionDefinition(
      "SampleData (Local)", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:./resources/sampledata/sampledata", "pentaho_user",
      "password" );
  private static final JdbcConnectionDefinition MYSQL_SAMPLE_DATA_DRIVER_SOURCE = new DriverConnectionDefinition(
      "SampleData (MySQL)", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/sampledata", "pentaho_user",
      "password" );

  // The location in the user preferences tree for the JNDI datasource settings
  private static final String DATASOURCE_PREFERENCES_NODE = "org/pentaho/reporting/ui/datasources/jdbc/Settings";

  // The current set of Sources
  private TreeMap<String, JdbcConnectionDefinition> connectionDefinitions =
      new TreeMap<String, JdbcConnectionDefinition>();

  /**
   * The reference to the preferences object for the datasource settings
   */
  private final Preferences userPreferences;

  private static final String TYPE_KEY = "type";
  // Constants use in the storage of the field information
  private static final String DRIVER_KEY = "driver";
  private static final String URL_KEY = "url";
  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";
  private static final String HOSTNAME_KEY = "hostname";
  private static final String PORT_KEY = "port";
  private static final String DATABASE_TYPE_KEY = "database_type";
  private static final String DATABASE_NAME_KEY = "database_name";
  private static final String JNDI_LOCATION = "jndi-location";

  /**
   * Initializes the instance by loading the database connection information from the configuration storage.
   */
  public JdbcConnectionDefinitionManager() {
    this( DATASOURCE_PREFERENCES_NODE );
  }

  public JdbcConnectionDefinitionManager( final String node ) {
    this( Preferences.userRoot().node( node ), node );
  }

  /**
   * package-local visibility for testing purposes
   */
  JdbcConnectionDefinitionManager( final Preferences externalPreferences, final String node ) {
    userPreferences = externalPreferences;
    // Load the list of JNDI Sources
    try {
      final String[] childNodeNames = userPreferences.childrenNames();
      for ( int i = 0; i < childNodeNames.length; i++ ) {
        final String name = childNodeNames[i];
        final Preferences p = userPreferences.node( name );
        final String type = p.get( "type", null );
        if ( type == null ) {
          p.removeNode();
        } else if ( type.equals( "local" ) ) {
          final Properties props = new Properties();
          if ( p.nodeExists( "properties" ) ) {
            final Preferences preferences = p.node( "properties" );
            final String[] strings = preferences.keys();
            for ( int j = 0; j < strings.length; j++ ) {
              final String string = strings[j];
              final String value = preferences.get( string, null );
              if ( value != null ) {
                props.setProperty( string, value );
              } else {
                props.remove( string );
              }
            }
          }

          final DriverConnectionDefinition driverConnection =
              new DriverConnectionDefinition( name, p.get( DRIVER_KEY, null ), p.get( URL_KEY, null ), p.get(
                  USERNAME_KEY, null ), p.get( PASSWORD_KEY, null ), p.get( HOSTNAME_KEY, null ),
                  p.get(DATABASE_NAME_KEY, null ), p.get( DATABASE_TYPE_KEY, null ), p.get( PORT_KEY, null ), props );

          connectionDefinitions.put( name, driverConnection );
        } else if ( type.equals( "jndi" ) ) {
          final JndiConnectionDefinition connectionDefinition =
              new JndiConnectionDefinition( name, p.get( JNDI_LOCATION, null ), p.get( DATABASE_TYPE_KEY, null ), p
                  .get( USERNAME_KEY, null ), p.get( PASSWORD_KEY, null ) );
          connectionDefinitions.put( name, connectionDefinition );
        } else {
          p.removeNode();
        }
      }
    } catch ( BackingStoreException e ) {
      // The system preferences system is not working - log this as a message and use defaults
      log.warn( "Could not access the user prefererences while loading the "
          + "JNDI connection information - using default JNDI connection entries", e );
    } catch ( final Exception e ) {
      log.warn( "Configuration information was invalid.", e );
    }

    // If the connectionDefinitions is empty, add any default entries
    if ( connectionDefinitions.isEmpty() && DATASOURCE_PREFERENCES_NODE.equals( node ) ) {
      if ( userPreferences.getBoolean( "sample-data-created", false ) == true ) {
        // only create the sample connections once, if we work on a totally fresh config.
        return;
      }
      updateSourceList( SAMPLE_DATA_JNDI_SOURCE );
      updateSourceList( SAMPLE_DATA_DRIVER_SOURCE );
      updateSourceList( SAMPLE_DATA_MEMORY_SOURCE );
      updateSourceList( LOCAL_SAMPLE_DATA_DRIVER_SOURCE );
      updateSourceList( MYSQL_SAMPLE_DATA_DRIVER_SOURCE );
      userPreferences.putBoolean( "sample-data-created", true );
      try {
        userPreferences.flush();
      } catch ( BackingStoreException e ) {
        // ignored ..
      }
    }
  }

  /**
   * Returns a copy of the current list of JNDI sources
   */
  public JdbcConnectionDefinition[] getSources() {
    return connectionDefinitions.values().toArray( new JdbcConnectionDefinition[connectionDefinitions.size()] );
  }

  /**
   * Removes the specified connection-definition from the list of connections.
   * 
   * @param name
   *          the name of the JNDI source to remove from the list
   */
  public void removeSource( final String name ) {
    // Make sure the name provided is not null
    if ( StringUtils.isEmpty( name ) ) {
      throw new IllegalArgumentException( "The provided name is invalid" );
    }

    // If the source is in our list, remove it
    connectionDefinitions.remove( name );

    // Remove the entry from the user preferences
    try {
      final Preferences node = userPreferences.node( name );
      if ( node != null ) {
        node.removeNode();
        userPreferences.flush();
      }
    } catch ( BackingStoreException e ) {
      log.error( "Could not remove JNDI connection entry [" + name + ']', e );
    }

  }

  /**
   * Adds or updates the source list with the specified source entry. Delagates to JNDI or JDBC method
   * 
   * @param source
   *          the entry to add/update in the list
   * @throws IllegalArgumentException
   *           indicates the source is <code>null</code>
   */
  public boolean updateSourceList( final JdbcConnectionDefinition source ) {
    if ( source == null ) {
      throw new IllegalArgumentException( "The provided source is null" );
    }

    if ( source instanceof DriverConnectionDefinition ) {
      return updateSourceList( (DriverConnectionDefinition) source );
    }

    if ( source instanceof JndiConnectionDefinition ) {
      return updateSourceList( (JndiConnectionDefinition) source );
    } else {
      throw new IllegalArgumentException( "The provided source is not a supported type" );
    }
  }

  /**
   * Adds or updates the source list with the specified source entry. If the entry exists (has the same name), the new
   * entry will replace the old entry. If the enrty does not already exist, the new entry will be added to the list. <br>
   * Since the definition of the ConnectionDefintion ensures that it will be valid, no testing will be performed on the
   * contents of the ConnectionDefintion.
   * 
   * @param source
   *          the entry to add/update in the list
   * @throws IllegalArgumentException
   *           indicates the source is <code>null</code>
   */
  private boolean updateSourceList( final DriverConnectionDefinition source ) throws IllegalArgumentException {
    if ( source == null ) {
      throw new IllegalArgumentException( "The provided source is null" );
    }

    // Update the node in the list
    final boolean updateExisting = ( connectionDefinitions.put( source.getName(), source ) != null );

    // Update the information in the preferences
    try {
      final Preferences node = userPreferences.node( source.getName() );
      put( node, TYPE_KEY, "local" );
      put( node, DRIVER_KEY, source.getDriverClass() );
      put( node, URL_KEY, source.getConnectionString() );
      put( node, USERNAME_KEY, source.getUsername() );
      put( node, PASSWORD_KEY, source.getPassword() );
      put( node, HOSTNAME_KEY, source.getHostName() );
      put( node, PORT_KEY, source.getPort() );
      put( node, DATABASE_TYPE_KEY, source.getDatabaseType() );
      put( node, DATABASE_NAME_KEY, source.getDatabaseName() );
      final Preferences preferences = node.node( "properties" );
      final Properties properties = source.getProperties();
      final Iterator entryIterator = properties.entrySet().iterator();
      while ( entryIterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) entryIterator.next();
        put( preferences, String.valueOf( entry.getKey() ), String.valueOf( entry.getValue() ) );
      }
      node.flush();
    } catch ( BackingStoreException e ) {
      log.error( "Could not add/update connection entry [" + source.getName() + ']', e );
    }
    return updateExisting;
  }

  private static void put( final Preferences node, final String key, final String value ) {
    if ( value == null ) {
      node.remove( key );
    } else {
      node.put( key, value );
    }

  }

  /**
   * Adds or updates the source list with the specified source entry. If the entry exists (has the same name), the new
   * entry will replace the old entry. If the enrty does not already exist, the new entry will be added to the list. <br>
   * Since the definition of the ConnectionDefintion ensures that it will be valid, no testing will be performed on the
   * contents of the ConnectionDefintion.
   * 
   * @param source
   *          the entry to add/update in the list
   * @throws IllegalArgumentException
   *           indicates the source is <code>null</code>
   */
  private boolean updateSourceList( final JndiConnectionDefinition source ) throws IllegalArgumentException {
    if ( source == null ) {
      throw new IllegalArgumentException( "The provided source is null" );
    }

    // Update the node in the list
    final boolean updateExisting = ( connectionDefinitions.put( source.getName(), source ) != null );

    // Update the information in the preferences
    try {
      final Preferences node = userPreferences.node( source.getName() );
      put( node, TYPE_KEY, "jndi" );
      put( node, JNDI_LOCATION, source.getJndiName() );
      put( node, USERNAME_KEY, null );
      put( node, PASSWORD_KEY, null );
      put( node, DATABASE_TYPE_KEY, source.getDatabaseType() );
      node.flush();
    } catch ( BackingStoreException e ) {
      log.error( "Could not add/update connection entry [" + source.getName() + ']', e );
    }
    return updateExisting;
  }
}
