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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.DataFactoryScriptingSupport;

import javax.swing.table.TableModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class SQLReportDataFactory extends SimpleSQLReportDataFactory {
  private DataFactoryScriptingSupport scriptingSupport;

  public SQLReportDataFactory( final Connection connection ) {
    super( connection );
    scriptingSupport = new DataFactoryScriptingSupport();
  }

  public SQLReportDataFactory( final ConnectionProvider connectionProvider ) {
    super( connectionProvider );
    scriptingSupport = new DataFactoryScriptingSupport();
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   *          the query.
   * @param parameters
   *          the parameters.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return scriptingSupport.containsQuery( query );
  }

  /**
   * Sets a query that uses no scripting for customization.
   *
   * @param name
   *          the logical name
   * @param queryString
   *          the SQL string that will be executed.
   */
  public void setQuery( final String name, final String queryString ) {
    if ( queryString == null ) {
      scriptingSupport.remove( name );
    } else {
      scriptingSupport.setQuery( name, queryString, null, null );
    }
  }

  public void setQuery( final String name, final String queryString, final String queryScriptLanguage,
      final String queryScript ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    scriptingSupport.setQuery( name, queryString, queryScriptLanguage, queryScript );
  }

  public void remove( final String name ) {
    scriptingSupport.remove( name );
  }

  public String getGlobalScriptLanguage() {
    return scriptingSupport.getGlobalScriptLanguage();
  }

  public void setGlobalScriptLanguage( final String scriptLanguage ) {
    scriptingSupport.setGlobalScriptLanguage( scriptLanguage );
  }

  public String getGlobalScript() {
    return scriptingSupport.getGlobalScript();
  }

  public void setGlobalScript( final String globalScript ) {
    scriptingSupport.setGlobalScript( globalScript );
  }

  public String getScriptingLanguage( final String name ) {
    return scriptingSupport.getScriptingLanguage( name );
  }

  public String getScript( final String name ) {
    return scriptingSupport.getScript( name );
  }

  public String getQuery( final String name ) {
    return scriptingSupport.getQuery( name );
  }

  public String[] getQueryNames() {
    return scriptingSupport.getQueryNames();
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    scriptingSupport.initialize( this, dataFactoryContext );
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public synchronized TableModel queryData( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException( "Query is null." ); //$NON-NLS-1$
    }
    final String realQuery = scriptingSupport.computeQuery( query, parameters );
    if ( realQuery == null ) {
      throw new ReportDataFactoryException( "Query '" + query + "' is not recognized." ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    TableModel queryResult = super.queryData( realQuery, parameters );
    TableModel postProcessResult = scriptingSupport.postProcessResult( query, parameters, queryResult );
    return postProcessResult;
  }

  public String[] getReferencedFields( final String query, final DataRow parameter ) throws ReportDataFactoryException {
    final String[] additionalFields = scriptingSupport.computeAdditionalQueryFields( query, parameter );
    if ( additionalFields == null ) {
      return null;
    }

    final LinkedHashSet<String> fields =
        new LinkedHashSet<String>( Arrays.asList( super.getReferencedFields( query, parameter ) ) );
    fields.addAll( Arrays.asList( additionalFields ) );
    return fields.toArray( new String[fields.size()] );
  }

  public ArrayList<Object> getQueryHash( final String queryName, final DataRow parameter ) {
    final ArrayList<Object> queryHash = super.getQueryHash( queryName, parameter );
    queryHash.add( scriptingSupport.getScriptingLanguage( queryName ) );
    queryHash.add( scriptingSupport.getScript( queryName ) );
    return queryHash;
  }

  public SQLReportDataFactory clone() {
    final SQLReportDataFactory dataFactory = (SQLReportDataFactory) super.clone();
    dataFactory.scriptingSupport = (DataFactoryScriptingSupport) scriptingSupport.clone();
    return dataFactory;
  }

  protected String computedQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    return scriptingSupport.computeQuery( queryName, parameters );
  }

  protected String translateQuery( final String query ) {
    return scriptingSupport.getQuery( query );
  }
}
