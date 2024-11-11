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
