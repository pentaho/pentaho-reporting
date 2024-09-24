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
