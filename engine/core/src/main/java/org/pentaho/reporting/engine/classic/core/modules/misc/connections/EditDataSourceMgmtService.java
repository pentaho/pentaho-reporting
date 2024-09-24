package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Designtime support class.
 */
public class EditDataSourceMgmtService implements DataSourceMgmtService {
  private DataSourceMgmtService parent;
  private Set<String> deletedMembersById;
  private HashMap<String, SerializedConnection> editedMembers;

  public EditDataSourceMgmtService() {
    this( ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceMgmtService.class ) );
  }

  public EditDataSourceMgmtService( final DataSourceMgmtService parent ) {
    ArgumentNullException.validate( "parent", parent );

    this.parent = parent;
    deletedMembersById = new HashSet<String>();
    editedMembers = new HashMap<String, SerializedConnection>();
  }

  public String createDatasource( final IDatabaseConnection databaseConnection ) throws DuplicateDatasourceException,
    DatasourceMgmtServiceException {
    final String name = databaseConnection.getName();
    if ( StringUtils.isEmpty( name ) ) {
      throw new IllegalArgumentException();
    }
    try {
      if ( getDatasourceByName( name ) != null ) {
        throw new DuplicateDatasourceException();
      }
    } catch ( NonExistingDatasourceException nde ) {
      // expected exception as a result of the getDatasourceByName call.
    }

    final String id = UUID.randomUUID().toString();
    databaseConnection.setId( id );
    editedMembers.put( id, new SerializedConnection( databaseConnection ) );
    return id;
  }

  public void deleteDatasourceById( final String id ) throws NonExistingDatasourceException,
    DatasourceMgmtServiceException {
    if ( StringUtils.isEmpty( id ) ) {
      throw new IllegalArgumentException();
    }

    if ( parent.getDatasourceIds().contains( id ) ) {
      deletedMembersById.add( id );
    } else if ( !editedMembers.containsKey( id ) ) {
      throw new NonExistingDatasourceException();
    }
    editedMembers.remove( id );
  }

  public IDatabaseConnection getDatasourceByName( final String name ) throws DatasourceMgmtServiceException {
    final IDatabaseConnection dataSource = parent.getDatasourceByName( name );
    if ( deletedMembersById.contains( dataSource.getId() ) ) {
      throw new NonExistingDatasourceException();
    }
    return dataSource;
  }

  public IDatabaseConnection getDatasourceById( final String id ) throws DatasourceMgmtServiceException {
    if ( deletedMembersById.contains( id ) ) {
      throw new NonExistingDatasourceException();
    }

    return parent.getDatasourceById( id );
  }

  public List<IDatabaseConnection> getDatasources() throws DatasourceMgmtServiceException {
    final ArrayList<IDatabaseConnection> connection = new ArrayList<IDatabaseConnection>( parent.getDatasources() );
    final Iterator<IDatabaseConnection> it = connection.iterator();
    while ( it.hasNext() ) {
      final IDatabaseConnection databaseConnection = it.next();
      if ( deletedMembersById.contains( databaseConnection.getId() ) ) {
        it.remove();
      }
    }
    return connection;
  }

  public List<String> getDatasourceIds() throws DatasourceMgmtServiceException {
    final List<IDatabaseConnection> datasources = getDatasources();
    final List<String> list = new ArrayList<String>();
    for ( int i = 0; i < datasources.size(); i++ ) {
      final IDatabaseConnection connection = datasources.get( i );
      list.add( connection.getId() );
    }
    return list;
  }

  public String updateDatasourceById( final String id, final IDatabaseConnection databaseConnection )
    throws NonExistingDatasourceException, DatasourceMgmtServiceException {
    if ( StringUtils.isEmpty( id ) ) {
      throw new IllegalArgumentException();
    }
    if ( ObjectUtilities.equal( id, databaseConnection.getId() ) == false ) {
      // the Hitachi Vantara-datasources API is ill defined and allows bad behaviour.
      throw new IllegalArgumentException();
    }
    if ( deletedMembersById.contains( id ) ) {
      throw new NonExistingDatasourceException();
    }
    editedMembers.put( id, new SerializedConnection( databaseConnection ) );
    return id;
  }

  public void commit() {
    for ( final String id : deletedMembersById ) {
      parent.deleteDatasourceById( id );
    }

    final Set<String> datasourceIds = new HashSet<String>( parent.getDatasourceIds() );
    for ( final SerializedConnection c : editedMembers.values() ) {
      final IDatabaseConnection connection = c.getConnection();
      if ( datasourceIds.contains( connection.getId() ) ) {
        parent.updateDatasourceById( connection.getId(), connection );
      } else {
        parent.createDatasource( connection );
      }
    }

    deletedMembersById.clear();
    editedMembers.clear();
  }
}
