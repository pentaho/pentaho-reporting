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
 * Copyright (c) 2017-2019 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.database.HypersonicDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.ui.datasources.jdbc.DatabaseMapping;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.ui.database.Messages;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.swing.SwingXulLoader;

import java.awt.Window;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * Managing class for instance of Xul Commons Database Dialog. This class handles the translation between DatabaseMeta
 * objects and types of @{link JdbcConnectionDefinition}.
 * <p/>
 * The dialog is always modal.
 *
 * @author NBaker
 */
public class XulDatabaseDialog {
  private static final Log log = LogFactory.getLog( XulDatabaseDialog.class );
  private static final String DIALOG_DEFINITION_FILE = "org/pentaho/ui/database/databasedialog.xul"; //$NON-NLS-1$
  private static final String OVERLAY_DEFINITION_FILE =
    "org/pentaho/reporting/ui/datasources/jdbc/ui/databasedialogOverlay.xul";  //$NON-NLS-1$
  public static final String OTHER_PREFIX = "::pentaho-reporting-other-attribute::";
  private XulDialog dialog;
  private XulDatabaseHandler handler;
  private DatabaseMeta meta;
  private static final String HSQLDB_PREFIX = "jdbc:hsqldb:hsql://";
  private static final String HSQLDB_MEM_PREFIX = "jdbc:hsqldb:mem:";
  private static final String HSQLDB_LOCAL_PREFIX = "jdbc:hsqldb:.";
  private DesignTimeContext designTimeContext;

  public XulDatabaseDialog( final Window parent,
                            final DesignTimeContext designTimeContext ) throws XulException {
    this.designTimeContext = designTimeContext;
    final SwingXulLoader loader = new SwingXulLoader();

    if ( parent != null ) {
      loader.setOuterContext( parent );
    }
    final XulDomContainer container = loader.loadXul( DIALOG_DEFINITION_FILE, Messages.getBundle() );
    container.getDocumentRoot().addOverlay( OVERLAY_DEFINITION_FILE );
    container.initialize();

    handler = new XulDatabaseHandler();
    container.addEventHandler( handler );   //$NON-NLS-1$

    final Document documentRoot = container.getDocumentRoot();
    final XulComponent root = documentRoot.getRootElement();

    if ( root instanceof XulDialog ) {
      dialog = (XulDialog) root;
      dialog.setResizable( Boolean.TRUE );
    } else {
      throw new XulException( "Error getting Xul Database Dialog root, element of type: " + root );
    }
  }

  private void setData( final JdbcConnectionDefinition def ) {
    if ( def instanceof DriverConnectionDefinition ) {
      final DriverConnectionDefinition jdbcDef = (DriverConnectionDefinition) def;
      this.meta = new DatabaseMeta();
      this.meta.setUsername( jdbcDef.getUsername() );
      this.meta.setPassword( jdbcDef.getPassword() );
      this.meta.setName( jdbcDef.getName() );

      if ( jdbcDef.getDatabaseType() != null ) {
        log.debug( "Database type is known: " + jdbcDef.getDatabaseType() );
        try {
          this.meta.setDatabaseType( jdbcDef.getDatabaseType() );
        } catch ( RuntimeException re ) {
          // sic!
        }
        this.meta.setDBName( jdbcDef.getDatabaseName() );
        this.meta.setHostname( jdbcDef.getHostName() );
        this.meta.setDBPort( jdbcDef.getPort() );
        this.meta.getAttributes()
          .setProperty( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jdbcDef.getConnectionString() );
        this.meta.getAttributes()
          .setProperty( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jdbcDef.getDriverClass() );
      } else if ( String.valueOf( jdbcDef.getConnectionString() ).startsWith( HSQLDB_MEM_PREFIX ) ) {
        this.meta.setDatabaseType( DatabaseMapping.getGenericInterface().getPluginId() );
        this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jdbcDef.getConnectionString() );
        this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jdbcDef.getDriverClass() );
      } else if ( String.valueOf( jdbcDef.getConnectionString() ).startsWith( HSQLDB_LOCAL_PREFIX ) ) {
        this.meta.setDatabaseType( DatabaseMapping.getGenericInterface().getPluginId() );
        this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jdbcDef.getConnectionString() );
        this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jdbcDef.getDriverClass() );
      } else {
        final DatabaseInterface databaseInterface = DatabaseMapping.getMappingForDriver( jdbcDef.getDriverClass() );
        this.meta.setDatabaseType( databaseInterface.getPluginId() );
        log.debug( "Database type is unknown, using " + databaseInterface );
        try {
          final String pattern;
          if ( databaseInterface instanceof HypersonicDatabaseMeta ) {
            final String connectionString = jdbcDef.getConnectionString();
            if ( connectionString.startsWith( HSQLDB_PREFIX ) ) {
              if ( connectionString.indexOf( ':', HSQLDB_PREFIX.length() ) == -1 ) {
                pattern = HSQLDB_PREFIX + "{0}/{2}";
              } else {
                pattern = HSQLDB_PREFIX + "{0}:{1}/{2}";
              }
            } else {
              pattern = databaseInterface.getURL( "{0}", "{1}", "{2}" );
            }
          } else {
            pattern = databaseInterface.getURL( "{0}", "{1}", "{2}" );
          }
          // knowing that most databases are written in C, we can be sure that the zero-character
          // is not a common value.
          if ( pattern != null && pattern.length() > 0 ) {
            final MessageFormat format = new MessageFormat( pattern );
            final Object[] objects = format.parse( jdbcDef.getConnectionString() );
            if ( objects[ 0 ] != null ) {
              this.meta.setHostname( String.valueOf( objects[ 0 ] ) );
            }
            if ( objects[ 1 ] != null ) {
              this.meta.setDBPort( String.valueOf( objects[ 1 ] ) );
            }
            if ( objects[ 2 ] != null ) {
              this.meta.setDBName( String.valueOf( objects[ 2 ] ) );
            }
          }
        } catch ( Exception e ) {
          designTimeContext.error( new XulException( "Unable to parse database-URL, please report "
            + "your database driver to Pentaho to include it in our list of databases.", e ) );
          this.meta.setDatabaseType( DatabaseMapping.getGenericInterface().getPluginId() );
          this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jdbcDef.getConnectionString() );
          this.meta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jdbcDef.getDriverClass() );
        }
      }


      final Properties properties = jdbcDef.getProperties();
      final Iterator entryIterator = properties.entrySet().iterator();
      while ( entryIterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) entryIterator.next();
        final String key = (String) entry.getKey();
        if ( key.startsWith( "::pentaho-reporting::" ) ) {
          continue;
        }
        if ( "user".equals( key ) || "password".equals( key ) ) {
          continue;
        }
        if ( key.startsWith( OTHER_PREFIX ) ) {
          this.meta.getAttributes().put( key.substring( OTHER_PREFIX.length() ), entry.getValue() );
        } else {
          this.meta.addExtraOption( meta.getPluginId(), key, (String) entry.getValue() );
        }
      }
    } else if ( def instanceof JndiConnectionDefinition ) {
      final JndiConnectionDefinition jndiDef = (JndiConnectionDefinition) def;
      this.meta = new DatabaseMeta();
      this.meta.setDBName( jndiDef.getJndiName() ); //JNDI name stored in DBname
      this.meta.setName( jndiDef.getName() );
      try {
        if ( jndiDef.getDatabaseType() != null ) {
          this.meta.setDatabaseType( jndiDef.getDatabaseType() );
        }
      } catch ( RuntimeException re ) {
        // even invalid values should not kill us.
        // sic! Kettle throws generic Exceptions.
      }
      this.meta.setAccessType( DatabaseMeta.TYPE_ACCESS_JNDI );
    } else {
      this.meta = null;
    }
  }

  public JdbcConnectionDefinition open( final JdbcConnectionDefinition definition ) {
    setData( definition );
    try {
      log.debug( "showing database dialog" );
      if ( meta != null ) {
        handler.setData( meta );
      }
      dialog.show(); //Blocks current thread
      log.debug( "dialog closed, getting DabaseMeta" );
      if ( handler.isConfirmed() == false ) {
        return null;
      }

      final DatabaseMeta database = (DatabaseMeta) handler.getData(); //$NON-NLS-1$
      if ( database == null ) {
        log.debug( "DatabaseMeta is null" );
        return null;
      }

      return convertDbMeta( database );
    } catch ( Exception e ) {
      log.error( e.getMessage(), e );
      return null;
    }
  }

  private JdbcConnectionDefinition convertDbMeta( final DatabaseMeta meta ) throws KettleDatabaseException {
    if ( meta.getAccessType() == DatabaseMeta.TYPE_ACCESS_JNDI ) {
      return new JndiConnectionDefinition( meta.getName(),
        meta.getDatabaseName(),
        meta.getDatabaseInterface().getPluginName(), null, null );
    } else {
      final Map<String, String> map = meta.getExtraOptions();
      final Properties properties = new Properties();
      final Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();
      while ( entryIterator.hasNext() ) {
        final Map.Entry<String, String> entry = entryIterator.next();
        final String key = entry.getKey();
        final String realKey = key.substring( meta.getPluginId().length() + 1 );
        final String value = entry.getValue();
        if ( DatabaseMeta.EMPTY_OPTIONS_STRING.equals( value ) ) {
          properties.put( realKey, "" );
        } else {
          properties.put( realKey, value );
        }
      }

      meta.getAttributes().keySet().stream()
        .map( key -> ( (String) key ) )
        .filter( key -> !key.startsWith( BaseDatabaseMeta.ATTRIBUTE_PREFIX_EXTRA_OPTION ) )
        .forEach( key -> properties.put( OTHER_PREFIX + key, meta.getAttributes().getProperty( key ) ) );


      return new DriverConnectionDefinition(
        meta.getName(),
        meta.getDriverClass(),
        meta.getURL(),
        meta.getUsername(),
        meta.getPassword(),
        meta.getHostname(),
        meta.getDatabaseName(),
        meta.getDatabaseInterface().getPluginId(),
        meta.getDatabasePortNumberString(),
        properties
      );
    }
  }

}
