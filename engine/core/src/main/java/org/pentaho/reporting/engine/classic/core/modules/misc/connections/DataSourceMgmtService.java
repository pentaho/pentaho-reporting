/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/
package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.pentaho.database.model.IDatabaseConnection;

import java.util.List;

/**
 * A design-time interface.
 */
public interface DataSourceMgmtService {
  /**
   * Creates a JDBC datasource in a given repository and return an id
   *
   * @param databaseConnection
   * @return id
   * @throws DuplicateDatasourceException
   * @throws DatasourceMgmtServiceException
   */
  public String createDatasource( IDatabaseConnection databaseConnection ) throws DuplicateDatasourceException,
    DatasourceMgmtServiceException;

  /**
   * Permanently deletes a JDBC datasource from a repository by id
   *
   * @param id
   * @throws NonExistingDatasourceException
   * @throws DatasourceMgmtServiceException
   */
  public void deleteDatasourceById( String id ) throws NonExistingDatasourceException, DatasourceMgmtServiceException;

  /**
   * Retrieves a JDBC datasource form the repository by name
   *
   * @param name
   * @return IDatabaseConnection
   * @throws DatasourceMgmtServiceException
   */
  public IDatabaseConnection getDatasourceByName( String name ) throws DatasourceMgmtServiceException;

  /**
   * Retrieves a JDBC datasource form the repository by id
   *
   * @param id
   * @return IDatabaseConnection
   * @throws DatasourceMgmtServiceException
   */
  public IDatabaseConnection getDatasourceById( String id ) throws DatasourceMgmtServiceException;

  /**
   * Retrieves all JDBC datasources from the repository
   *
   * @return databaseConnection List
   * @throws DatasourceMgmtServiceException
   */
  public List<IDatabaseConnection> getDatasources() throws DatasourceMgmtServiceException;

  /**
   * Retrieves all JDBC datasource ids from the repository
   *
   * @return list of ids
   * @throws DatasourceMgmtServiceException
   */
  public List<String> getDatasourceIds() throws DatasourceMgmtServiceException;

  /**
   * Updates a given JDBC datasource by id
   *
   * @param id
   * @param databaseConnection
   * @return id
   * @throws NonExistingDatasourceException
   * @throws DatasourceMgmtServiceException
   */
  public String updateDatasourceById( String id, IDatabaseConnection databaseConnection )
    throws NonExistingDatasourceException, DatasourceMgmtServiceException;
}
