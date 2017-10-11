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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.connectioneditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.database.model.DatabaseAccessType;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.database.model.IDatabaseType;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.database.util.DatabaseTypeHelper;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.ui.database.Messages;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.swing.SwingXulLoader;

import java.awt.*;
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
  private XulDialog dialog;
  private XulDatabaseHandler handler;
  private DatabaseMeta meta;
  private String id;
  private final DatabaseTypeHelper databaseTypeHelper;

  public XulDatabaseDialog( final Window parent ) throws XulException {
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

    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final IDatabaseDialectService dialectService = objectFactory.get( IDatabaseDialectService.class );
    this.databaseTypeHelper = new DatabaseTypeHelper( dialectService.getDatabaseTypes() );

  }

  private int convertAccessTypeToKettle( final DatabaseAccessType type ) {
    switch( type ) {
      case NATIVE:
        return DatabaseMeta.TYPE_ACCESS_NATIVE;
      case JNDI:
        return DatabaseMeta.TYPE_ACCESS_JNDI;
      case OCI:
        return DatabaseMeta.TYPE_ACCESS_OCI;
      case ODBC:
        return DatabaseMeta.TYPE_ACCESS_ODBC;
      case PLUGIN:
        return DatabaseMeta.TYPE_ACCESS_PLUGIN;
      default:
        throw new IllegalStateException();
    }
  }

  private DatabaseAccessType convertAccessTypeFromKettle( final int type ) {
    switch( type ) {
      case DatabaseMeta.TYPE_ACCESS_NATIVE:
        return DatabaseAccessType.NATIVE;
      case DatabaseMeta.TYPE_ACCESS_JNDI:
        return DatabaseAccessType.JNDI;
      case DatabaseMeta.TYPE_ACCESS_OCI:
        return DatabaseAccessType.OCI;
      case DatabaseMeta.TYPE_ACCESS_ODBC:
        return DatabaseAccessType.ODBC;
      case DatabaseMeta.TYPE_ACCESS_PLUGIN:
        return DatabaseAccessType.PLUGIN;
      default:
        throw new IllegalStateException();
    }
  }

  private Properties convertToKettle( final Map<String, String> attrs ) {
    final Properties p = new Properties();
    p.putAll( attrs );
    return p;
  }

  private void setData( final IDatabaseConnection def ) {
    if ( def == null ) {
      this.id = null;
      this.meta = new DatabaseMeta();
      this.meta.setDatabaseType( "GENERIC" );
      return;
    }

    this.id = def.getId();
    this.meta = new DatabaseMeta();
    final IDatabaseType databaseType = def.getDatabaseType();
    if ( databaseType != null ) {
      this.meta.setDatabaseType( databaseType.getShortName() );
    } else {
      final String kettleType = def.getAttributes().get( "_kettle_native_plugin_id" );
      if ( kettleType == null ) {
        this.meta.setDatabaseType( "GENERIC" );
      } else {
        this.meta.setDatabaseType( kettleType );
      }
    }
    this.meta.setAccessType( convertAccessTypeToKettle( def.getAccessType() ) );
    this.meta.setAttributes( convertToKettle( def.getAttributes() ) );
    this.meta.setUsername( def.getUsername() );
    this.meta.setPassword( def.getPassword() );
    this.meta.setName( def.getName() );
    this.meta.setDataTablespace( def.getDataTablespace() );
    this.meta.setDBName( def.getDatabaseName() );
    this.meta.setDBPort( def.getDatabasePort() );
    this.meta.setHostname( def.getHostname() );
    this.meta.setIndexTablespace( def.getIndexTablespace() );
    this.meta.setServername( def.getInformixServername() );
  }

  private IDatabaseConnection getData() {
    if ( this.meta == null ) {
      return null;
    }

    final DatabaseConnection connection = new DatabaseConnection();
    connection.setAccessType( convertAccessTypeFromKettle( meta.getAccessType() ) );
    final Properties attributes = meta.getAttributes();
    for ( final Map.Entry e : attributes.entrySet() ) {
      connection.getAttributes().put( (String) e.getKey(), (String) e.getValue() );
    }

    connection.setUsername( meta.getUsername() );
    connection.setPassword( meta.getPassword() );
    connection.setName( meta.getName() );
    connection.setId( id );
    connection.setDataTablespace( meta.getDataTablespace() );
    connection.setDatabaseName( meta.getDatabaseName() );
    connection.setDatabasePort( meta.getDatabasePortNumberString() );
    connection.setHostname( meta.getHostname() );
    connection.setIndexTablespace( meta.getIndexTablespace() );
    connection.setInformixServername( meta.getServername() );

    final String shortName = meta.getDatabaseInterface().getPluginId();
    connection.setDatabaseType( databaseTypeHelper.getDatabaseTypeByShortName( shortName ) );
    connection.getAttributes().put( "_kettle_native_plugin_id", shortName );
    return connection;
  }

  public IDatabaseConnection open( final IDatabaseConnection definition ) {
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
      this.meta = database;
      return getData();
    } catch ( Exception e ) {
      log.error( e.getMessage(), e );
      return null;
    }
  }

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();

    final DatabaseInterface[] databaseInterfaces = DatabaseMeta.getDatabaseInterfaces();
    DebugLog.logHere();
  }
}
