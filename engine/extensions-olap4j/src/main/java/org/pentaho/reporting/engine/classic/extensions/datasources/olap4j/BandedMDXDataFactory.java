/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

import javax.swing.table.TableModel;
import java.sql.SQLException;

public class BandedMDXDataFactory extends AbstractNamedMDXDataFactory {
  public BandedMDXDataFactory( final OlapConnectionProvider connectionProvider ) {
    super( connectionProvider );
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param queryName  the query name
   * @param parameters the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException if an error occured while performing the query.
   */
  public TableModel queryData( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    try {
      final QueryResultWrapper cellSet = performQuery( queryName, parameters );
      return postProcess( queryName, parameters,
        new BandedMDXTableModel( cellSet, extractQueryLimit( parameters ), isMembersOnAxisSorted() ) );
    } catch ( final SQLException sqE ) {
      throw new ReportDataFactoryException( "Failed to execute query", sqE );
    }
  }
}
