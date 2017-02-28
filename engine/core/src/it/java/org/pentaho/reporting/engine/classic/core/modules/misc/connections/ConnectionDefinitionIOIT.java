package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import junit.framework.TestCase;
import org.pentaho.database.model.DatabaseAccessType;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.database.util.DatabaseTypeHelper;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser.DatabaseConnectionCollection;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer.FileDataSourceMgmtWriter;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ConnectionDefinitionIOIT extends TestCase {
  public ConnectionDefinitionIOIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWriteAndLoad() throws IOException, ResourceException {
    // only a limited set of properties is actually persisted. We follow the lead of the platform,
    // which seems to cut out all kettle-specific stuff that has not been abstracted out.
    final IDatabaseConnection connection1 = generateDatabaseConnection();
    final IDatabaseConnection connection2 = generateDatabaseConnection();

    final FileDataSourceMgmtWriter writer = new FileDataSourceMgmtWriter();
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    writer.write( new IDatabaseConnection[] { connection1, connection2 }, bout );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( bout.toByteArray(), DatabaseConnectionCollection.class );
    final DatabaseConnectionCollection resource = (DatabaseConnectionCollection) directly.getResource();
    final IDatabaseConnection[] connections = resource.getConnections();
    assertEquals( 2, connections.length );
    assertEquals( connection1, connections[0] );
    assertEquals( connection2, connections[1] );
  }

  private void assertEquals( final IDatabaseConnection connection1, final IDatabaseConnection connection2 ) {
    assertEquals( connection1.getName(), connection2.getName() );
    assertEquals( connection1.getId(), connection2.getId() );
    assertEquals( connection1.getAccessType(), connection2.getAccessType() );
    assertEquals( connection1.getDataTablespace(), connection2.getDataTablespace() );
    assertEquals( connection1.getDatabaseName(), connection2.getDatabaseName() );
    assertEquals( connection1.getDatabasePort(), connection2.getDatabasePort() );
    assertEquals( connection1.getHostname(), connection2.getHostname() );
    assertEquals( connection1.getIndexTablespace(), connection2.getIndexTablespace() );
    assertEquals( connection1.getInformixServername(), connection2.getInformixServername() );
    assertEquals( connection1.getUsername(), connection2.getUsername() );
    assertEquals( connection1.getPassword(), connection2.getPassword() );
  }

  public static IDatabaseConnection generateDatabaseConnection() {
    final IDatabaseConnection connection = new DatabaseConnection();
    connection.setName( UUID.randomUUID().toString() );
    connection.setId( UUID.randomUUID().toString() );
    connection.setAccessType( DatabaseAccessType.NATIVE );
    connection.setDataTablespace( UUID.randomUUID().toString() );
    connection.setDatabaseName( UUID.randomUUID().toString() );
    connection.setDatabasePort( "12345" );
    connection.setHostname( UUID.randomUUID().toString() );
    connection.setIndexTablespace( UUID.randomUUID().toString() );
    connection.setInformixServername( UUID.randomUUID().toString() );
    connection.setUsername( UUID.randomUUID().toString() );
    connection.setPassword( UUID.randomUUID().toString() );

    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final IDatabaseDialectService dialectService = objectFactory.get( IDatabaseDialectService.class );
    final DatabaseTypeHelper databaseTypeHelper = new DatabaseTypeHelper( dialectService.getDatabaseTypes() );

    connection.setDatabaseType( databaseTypeHelper.getDatabaseTypeByShortName( "GENERIC" ) );
    return connection;
  }
}
