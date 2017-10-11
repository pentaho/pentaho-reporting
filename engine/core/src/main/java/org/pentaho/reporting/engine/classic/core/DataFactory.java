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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

import javax.swing.table.TableModel;
import java.io.Serializable;

/**
 * Creates a tablemodel on request. If the returned tablemodel is a
 * {@link org.pentaho.reporting.engine.classic.core .util.CloseableTableModel} the tablemodel must remain open until the
 * DataFactory remains open. The TableModel should not be disposed until the data-factory has been closed.
 *
 * @author Thomas Morgner
 */
public interface DataFactory extends Serializable, Cloneable {
  /**
   * An internal query parameter that holds the maximum number of rows a query should return.
   */
  public static final String QUERY_LIMIT = "::org.pentaho.reporting::query-limit";
  /**
   * An internal query parameter that holds the query timeout value. This is passed to the data-source. The handling of
   * this parameter is implementation dependent.
   */
  public static final String QUERY_TIMEOUT = "::org.pentaho.reporting::query-timeout";
  public static final String QUERY_SORT = "::org.pentaho.reporting::query-sort";

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query
   *          the query string, never null.
   * @param parameters
   *          the parameters for the query, never null.
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException
   *           if an error occured while performing the query.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException;

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive();

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close();

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   *          the query, never null.
   * @param parameters
   *          the parameters, never null.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters );

  /**
   * Returns all known query-names. A data-factory may accept more than the query-names returned here.
   *
   * @return the known query names.
   */
  public String[] getQueryNames();

  /**
   * Attempts to cancel the query process that is generating the data for this data factory. If it is not possible to
   * cancel the query, this call should be ignored.
   */
  public void cancelRunningQuery();

  /**
   * Initializes the data factory and provides new context information. Initialize is always called before the
   * datafactory has been opened by calling DataFactory#open.
   *
   * @param dataFactoryContext
   *          the current data-factory context, holding the configuration, resource-manager, context-key and
   *          resource-bundle-factory.
   */
  public void initialize( DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException;

  public Object clone();

  /**
   * @return the metadata object for this dataFactory
   */
  public DataFactoryMetaData getMetaData();
}
