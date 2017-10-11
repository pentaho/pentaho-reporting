/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;

public abstract class AbstractNamedDataFactory<T> extends AbstractDataFactory {
  private LinkedHashMap<String, T> queries;

  public AbstractNamedDataFactory() {
    queries = new LinkedHashMap<String, T>();
  }

  protected T mapQuery( final String query ) throws ReportDataFactoryException {
    T t = queries.get( query );
    if ( t == null ) {
      throw new ReportDataFactoryException( "No such Query" );
    }
    return t;
  }

  public final TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    return queryDataInternal( mapQuery( query ), parameters );
  }

  protected abstract TableModel queryDataInternal( final T query, final DataRow parameters )
    throws ReportDataFactoryException;

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queries.containsKey( query );
  }

  public String[] getQueryNames() {
    return queries.keySet().toArray( new String[queries.size()] );
  }

  public final String[] getReferencedFields( final String query, final DataRow dataRow )
    throws ReportDataFactoryException {
    final T queryObject = mapQuery( query );
    return getReferencedFieldsInternal( queryObject, dataRow );
  }

  protected abstract String[] getReferencedFieldsInternal( T query, DataRow dataRow ) throws ReportDataFactoryException;

  public final Object getQueryHash( final String query, final DataRow dataRow ) throws ReportDataFactoryException {
    final T queryObject = mapQuery( query );
    return getQueryHashInternal( queryObject, dataRow );
  }

  protected abstract Object getQueryHashInternal( final T queryObject, final DataRow dataRow )
    throws ReportDataFactoryException;
}
