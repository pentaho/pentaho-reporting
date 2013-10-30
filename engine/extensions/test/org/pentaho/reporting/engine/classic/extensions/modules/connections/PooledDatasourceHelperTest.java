package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import java.sql.Connection;
import java.util.HashMap;

import junit.framework.TestCase;
import org.apache.commons.dbcp.PoolingDataSource;
import org.pentaho.database.model.DatabaseAccessType;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.database.util.DatabaseTypeHelper;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;

public class PooledDatasourceHelperTest extends TestCase
{
  public PooledDatasourceHelperTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCreatePool() throws Exception
  {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final IDatabaseDialectService dialectService = objectFactory.get(IDatabaseDialectService.class);
    final DatabaseTypeHelper databaseTypeHelper = new DatabaseTypeHelper(dialectService.getDatabaseTypes());

    final DatabaseConnection con = new DatabaseConnection();
    con.setId("Memory");
    con.setName("Memory");
    con.setAccessType(DatabaseAccessType.NATIVE);
    con.setDatabaseType(databaseTypeHelper.getDatabaseTypeByShortName("GENERIC"));
    con.setUsername("pentaho_user");
    con.setPassword("password");
    final HashMap<String, String> attrs = new HashMap<String, String>();
    attrs.put(DatabaseConnection.ATTRIBUTE_CUSTOM_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
    attrs.put(DatabaseConnection.ATTRIBUTE_CUSTOM_URL, "jdbc:hsqldb:mem:SampleData");
    con.setAttributes(attrs);

    final PoolingDataSource poolingDataSource = PooledDatasourceHelper.setupPooledDataSource(con);
    final Connection connection = poolingDataSource.getConnection();
    connection.close();

  }
}
