/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
