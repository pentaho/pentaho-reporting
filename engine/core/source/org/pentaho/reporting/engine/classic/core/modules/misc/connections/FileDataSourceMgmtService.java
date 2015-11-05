package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser.DatabaseConnectionCollection;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer.DataSourceMgmtWriter;
import org.pentaho.reporting.engine.classic.core.util.ConfigurationPropertyLookupParser;
import org.pentaho.reporting.libraries.base.boot.SingletonHint;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings( "HardCodedStringLiteral" )
@SingletonHint
public class FileDataSourceMgmtService implements DataSourceMgmtService {
  private static final Log logger = LogFactory.getLog( FileDataSourceMgmtService.class );
  private HashMap<String, SerializedConnection> connectionsByName;
  private HashMap<String, SerializedConnection> connectionsById;
  private File target;
  private long lastModifiedDate;

  public FileDataSourceMgmtService() {
    connectionsById = new HashMap<String, SerializedConnection>();
    connectionsByName = new HashMap<String, SerializedConnection>();
    lastModifiedDate = -1;
  }

  protected File createTargetFile() {
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final ConfigurationPropertyLookupParser parser = new ConfigurationPropertyLookupParser( globalConfig );
    final String fileName =
        parser
            .translateAndLookup( globalConfig
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.misc.connections.file-data.targetLocation" ) );
    final File file = new File( fileName );
    file.getParentFile().mkdirs();
    return file;
  }

  public File getTarget() {
    return target;
  }

  public void setTarget( final File target ) {
    this.target = target;
  }

  public synchronized String createDatasource( final IDatabaseConnection databaseConnection )
    throws DuplicateDatasourceException, DatasourceMgmtServiceException {
    load();

    String name = databaseConnection.getName();
    if ( name == null ) {
      name = generateName();
    }
    if ( connectionsByName.containsKey( name ) ) {
      throw new DuplicateDatasourceException();
    }

    final String id = UUID.randomUUID().toString();
    databaseConnection.setId( id );
    databaseConnection.setName( name );
    final SerializedConnection serializedConnection = new SerializedConnection( databaseConnection );
    connectionsById.put( id, serializedConnection );
    connectionsByName.put( name, serializedConnection );

    writeChanges();

    return id;
  }

  private String generateName() {
    // todo: Maybe we can have a better strategy here later
    return UUID.randomUUID().toString();
  }

  public synchronized void deleteDatasourceById( final String id ) throws NonExistingDatasourceException,
    DatasourceMgmtServiceException {
    load();

    final SerializedConnection connection = connectionsById.get( id );
    if ( connection == null ) {
      throw new NonExistingDatasourceException();
    }
    final IDatabaseConnection databaseConnection = connection.getConnection();
    connectionsByName.remove( databaseConnection.getName() );
    connectionsById.remove( databaseConnection.getId() );

    writeChanges();
  }

  public IDatabaseConnection getDatasourceByName( final String name ) throws DatasourceMgmtServiceException {
    load();

    final SerializedConnection connection = connectionsByName.get( name );
    if ( connection == null ) {
      throw new NonExistingDatasourceException();
    }
    return connection.getConnection();
  }

  public IDatabaseConnection getDatasourceById( final String id ) throws DatasourceMgmtServiceException {
    load();

    final SerializedConnection connection = connectionsById.get( id );
    if ( connection == null ) {
      throw new NonExistingDatasourceException();
    }
    return connection.getConnection();
  }

  public List<IDatabaseConnection> getDatasources() throws DatasourceMgmtServiceException {
    load();

    final ArrayList<IDatabaseConnection> connections = new ArrayList<IDatabaseConnection>();
    final Collection<SerializedConnection> values = connectionsById.values();
    for ( final SerializedConnection co : values ) {
      connections.add( co.getConnection() );
    }
    return connections;
  }

  public List<String> getDatasourceIds() throws DatasourceMgmtServiceException {
    load();

    final ArrayList<String> connections = new ArrayList<String>();
    final Collection<SerializedConnection> values = connectionsById.values();
    for ( final SerializedConnection co : values ) {
      connections.add( co.getConnection().getId() );
    }
    return connections;
  }

  public String updateDatasourceById( final String id, final IDatabaseConnection databaseConnection )
    throws NonExistingDatasourceException, DatasourceMgmtServiceException {
    load();

    final SerializedConnection connection = connectionsById.get( id );
    if ( connection == null ) {
      throw new NonExistingDatasourceException();
    }

    final String name = connection.getConnection().getName();
    databaseConnection.setId( id );
    final SerializedConnection serializedConnection = new SerializedConnection( databaseConnection );
    connectionsById.put( id, serializedConnection );
    connectionsByName.remove( name );
    connectionsByName.put( serializedConnection.getConnection().getName(), serializedConnection );

    writeChanges();

    return id;
  }

  protected void load() {
    if ( target == null ) {
      target = createTargetFile();
    }
    if ( target == null ) {
      return;
    }

    if ( target.lastModified() == lastModifiedDate ) {
      return;
    }
    if ( !target.exists() ) {
      return;
    }

    synchronized ( this ) {
      try {
        final ResourceManager mgr = new ResourceManager();
        mgr.registerDefaults();
        final ResourceKey key = mgr.createKey( target );
        final Resource resource = mgr.create( key, null, DatabaseConnectionCollection.class );
        final DatabaseConnectionCollection collection = (DatabaseConnectionCollection) resource.getResource();
        for ( final IDatabaseConnection connection : collection.getConnections() ) {
          final String id = connection.getId();
          final String name = connection.getName();
          if ( name == null ) {
            logger.warn( "Skipping invalid connection definition, name is empty." );
            continue;
          }
          if ( id == null ) {
            logger.warn( "Skipping invalid connection definition, id is empty." );
            continue;
          }
          final SerializedConnection value = new SerializedConnection( connection );
          connectionsById.put( id, value );
          connectionsByName.put( name, value );
        }
      } catch ( ResourceException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.error( "Unable to parse datasource declaration.", e );
        } else {
          logger.error( "Unable to parse datasource declaration: " + e );
        }
      } catch ( IOException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.error( "Unable to parse datasource declaration.", e );
        } else {
          logger.error( "Unable to parse datasource declaration: " + e );
        }
      } finally {
        lastModifiedDate = target.lastModified();
      }
    }
  }

  protected void writeChanges() {
    final List<IDatabaseConnection> datasources = getDatasources();
    final IDatabaseConnection[] connections = datasources.toArray( new IDatabaseConnection[datasources.size()] );
    final DataSourceMgmtWriter writer =
        ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceMgmtWriter.class );

    try {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      writer.write( connections, bout );

      if ( target == null ) {
        target = createTargetFile();
      }
      if ( target == null ) {
        return;
      }

      final FileOutputStream fout = new FileOutputStream( target );
      try {
        fout.write( bout.toByteArray() );
      } finally {
        fout.close();
      }
    } catch ( IOException e ) {
      logger.error( "Unable to write datasource declaration.", e );
    }
  }
}
