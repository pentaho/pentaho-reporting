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


package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.table.TableModel;

/**
 * A datafactory that does not do any real work.
 *
 * @author Thomas Morgner
 */
public class EmptyDataFactory extends AbstractDataFactory {
  private static final String[] EMPTY_NAMES = new String[0];

  public EmptyDataFactory() {
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query
   *          the query string
   * @param parameters
   *          the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException
   *           if an error occured while performing the query.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    throw new ReportDataFactoryException( "This factory does not understand any of the queries." );
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    // no op.
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    // none of the queries is executable here.
    return false;
  }

  public String[] getQueryNames() {
    return EMPTY_NAMES;
  }

  public EmptyDataFactory clone() {
    return (EmptyDataFactory) super.clone();
  }
}
