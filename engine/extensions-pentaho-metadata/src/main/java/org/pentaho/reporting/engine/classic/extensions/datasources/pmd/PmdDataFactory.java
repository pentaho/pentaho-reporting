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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.DataFactoryScriptingSupport;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Performs MQL queries. A MQL-query usually contains all information needed to connect to the database. However the
 * platform also allows to override the connection-information and to provide an own connection instead.
 * <p/>
 * We mirror that case by allowing to provide a connection provider. If no connection provider is given, we use whatever
 * connection information is stored in the MQL data itself.
 *
 * @author Thomas Morgner
 */
public class PmdDataFactory extends SimplePmdDataFactory {
  private DataFactoryScriptingSupport scriptingSupport;

  public PmdDataFactory() {
    scriptingSupport = new DataFactoryScriptingSupport();
  }


  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query      the query.
   * @param parameters the parameters.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return scriptingSupport.containsQuery( query );
  }

  /**
   * @param name
   * @param queryString
   */
  public void setQuery( final String name, final String queryString ) {
    if ( queryString == null ) {
      scriptingSupport.remove( name );
    } else {
      scriptingSupport.setQuery( name, queryString, null, null );
    }
  }

  public void setQuery( final String name, final String queryString,
                        final String queryScriptLanguage, final String queryScript ) {
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
    final String query = scriptingSupport.computeQuery( queryName, parameters );
    if ( query == null ) {
      throw new ReportDataFactoryException( "No such query: " + queryName );
    }

    final TableModel tableModel = super.queryData( query, parameters );
    return scriptingSupport.postProcessResult( queryName, parameters, tableModel );
  }

  public TableModel queryDesignTimeStructure( final String queryName,
                                              final DataRow parameters ) throws ReportDataFactoryException {
    final String query = scriptingSupport.computeQuery( queryName, parameters );
    if ( query == null ) {
      throw new ReportDataFactoryException( "No such query: " + queryName );
    }

    TableModel tableModel = super.queryDesignTimeStructure( query, parameters );
    return scriptingSupport.postProcessResult( queryName, parameters, tableModel );
  }

  protected String computedQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    return scriptingSupport.computeQuery( queryName, parameters );
  }

  protected String translateQuery( final String query ) {
    return scriptingSupport.getQuery( query );
  }

  public String[] getReferencedFields( final String query, final DataRow parameter ) throws ReportDataFactoryException {
    final String[] additionalFields = scriptingSupport.computeAdditionalQueryFields( query, parameter );
    if ( additionalFields == null ) {
      return null;
    }

    final LinkedHashSet<String> fields =
      new LinkedHashSet<String>( Arrays.asList( super.getReferencedFields( query, parameter ) ) );
    fields.addAll( Arrays.asList( additionalFields ) );
    return fields.toArray( new String[ fields.size() ] );
  }

  public ArrayList<Object> getQueryHash( final String queryName, final DataRow parameters ) {
    final ArrayList<Object> queryHash = super.getQueryHash( queryName, parameters );
    queryHash.add( scriptingSupport.getScriptingLanguage( queryName ) );
    queryHash.add( scriptingSupport.getScript( queryName ) );
    return queryHash;
  }

  public PmdDataFactory clone() {
    final PmdDataFactory dataFactory = (PmdDataFactory) super.clone();
    dataFactory.scriptingSupport = (DataFactoryScriptingSupport) scriptingSupport.clone();
    return dataFactory;
  }

  public void close() {
    scriptingSupport.shutdown();
    super.close();
  }
}
