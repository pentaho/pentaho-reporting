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

package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.TableModel;

/**
 * A datafactory that allows to use a tablemodel passed in as a parameter as primary report-datasource.
 *
 * @author Thomas Morgner
 */
public class ExternalDataFactory extends AbstractDataFactory {
  private static final String[] EMPTY_NAMES = new String[0];

  public ExternalDataFactory() {
  }

  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final Object o = parameters.get( query );
    if ( o instanceof TableModel ) {
      return (TableModel) o;
    }
    throw new ReportDataFactoryException( "The parameter given was no valid datasource: " + query );
  }

  public DataFactory derive() {
    return this;
  }

  public void close() {

  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    final Object o = parameters.get( query );
    return o instanceof TableModel;
  }

  public String[] getQueryNames() {
    return EMPTY_NAMES;
  }
}
