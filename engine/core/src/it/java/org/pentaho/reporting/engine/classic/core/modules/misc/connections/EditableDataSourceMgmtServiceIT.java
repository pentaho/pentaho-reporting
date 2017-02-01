package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import junit.framework.TestCase;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import java.util.List;

public class EditableDataSourceMgmtServiceIT extends TestCase {
  public EditableDataSourceMgmtServiceIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCreateDataSource() {
    InMemoryDataSourceMgmtService backend = new InMemoryDataSourceMgmtService();
    EditDataSourceMgmtService editor = new EditDataSourceMgmtService( backend );

    IDatabaseConnection c = ConnectionDefinitionIOIT.generateDatabaseConnection();
    editor.createDatasource( c );
    editor.commit();
    List<String> datasourceIds = backend.getDatasourceIds();
    assertEquals( 1, datasourceIds.size() );
    final String dsId = datasourceIds.get( 0 );
    assertEquals( c.getDatabaseName(), backend.getDatasourceById( dsId ).getDatabaseName() );
    assertEquals( c.getName(), backend.getDatasourceById( dsId ).getName() );

    assertEquals( c.getDatabaseName(), backend.getDatasourceByName( c.getName() ).getDatabaseName() );
    assertEquals( c.getName(), backend.getDatasourceByName( c.getName() ).getName() );
  }

  public void testUpdateDataSource() {
    InMemoryDataSourceMgmtService backend = new InMemoryDataSourceMgmtService();
    IDatabaseConnection c = ConnectionDefinitionIOIT.generateDatabaseConnection();
    String realId = backend.createDatasource( c );

    EditDataSourceMgmtService editor = new EditDataSourceMgmtService( backend );

    IDatabaseConnection c2 = ConnectionDefinitionIOIT.generateDatabaseConnection();
    c2.setId( realId );
    c2.setName( "Name" );
    editor.updateDatasourceById( realId, c2 );
    editor.commit();

    List<String> datasourceIds = backend.getDatasourceIds();
    assertEquals( 1, datasourceIds.size() );
    assertEquals( realId, datasourceIds.get( 0 ) );

    assertEquals( c2.getDatabaseName(), backend.getDatasourceById( realId ).getDatabaseName() );
    assertEquals( c2.getName(), backend.getDatasourceById( realId ).getName() );

    assertEquals( c2.getDatabaseName(), backend.getDatasourceByName( c2.getName() ).getDatabaseName() );
    assertEquals( c2.getName(), backend.getDatasourceByName( c2.getName() ).getName() );
  }

  public void testRemoveDataSource() {
    InMemoryDataSourceMgmtService backend = new InMemoryDataSourceMgmtService();
    IDatabaseConnection c = ConnectionDefinitionIOIT.generateDatabaseConnection();
    String realId = backend.createDatasource( c );

    EditDataSourceMgmtService editor = new EditDataSourceMgmtService( backend );
    editor.deleteDatasourceById( realId );
    editor.commit();
    List<String> datasourceIds = backend.getDatasourceIds();
    assertEquals( 0, datasourceIds.size() );
  }

  public void testRemoveThenAddDataSource() {
    InMemoryDataSourceMgmtService backend = new InMemoryDataSourceMgmtService();
    IDatabaseConnection r = ConnectionDefinitionIOIT.generateDatabaseConnection();
    String realId = backend.createDatasource( r );

    EditDataSourceMgmtService editor = new EditDataSourceMgmtService( backend );
    editor.deleteDatasourceById( realId );

    IDatabaseConnection c2 = ConnectionDefinitionIOIT.generateDatabaseConnection();
    c2.setName( r.getName() );
    editor.createDatasource( c2 );
    editor.commit();

    List<String> datasourceIds = backend.getDatasourceIds();
    assertEquals( 1, datasourceIds.size() );

    final String dsId = datasourceIds.get( 0 );
    assertEquals( c2.getDatabaseName(), backend.getDatasourceById( dsId ).getDatabaseName() );
    assertEquals( c2.getName(), backend.getDatasourceById( dsId ).getName() );

    assertEquals( c2.getDatabaseName(), backend.getDatasourceByName( c2.getName() ).getDatabaseName() );
    assertEquals( c2.getName(), backend.getDatasourceByName( c2.getName() ).getName() );
  }

  public void testRemoveThenAddThenRemoveDataSource() {
    InMemoryDataSourceMgmtService backend = new InMemoryDataSourceMgmtService();
    IDatabaseConnection r = ConnectionDefinitionIOIT.generateDatabaseConnection();
    String realId = backend.createDatasource( r );

    EditDataSourceMgmtService editor = new EditDataSourceMgmtService( backend );
    editor.deleteDatasourceById( realId );

    IDatabaseConnection c2 = ConnectionDefinitionIOIT.generateDatabaseConnection();
    c2.setName( r.getName() );
    String id = editor.createDatasource( c2 );
    editor.deleteDatasourceById( id );
    editor.commit();

    List<String> datasourceIds = backend.getDatasourceIds();
    assertEquals( 0, datasourceIds.size() );
  }

}
