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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.olap.Result;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.DataFactoryScriptingSupport;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public abstract class AbstractNamedMDXDataFactory extends AbstractMDXDataFactory {
  private DataFactoryScriptingSupport scriptingSupport;

  public AbstractNamedMDXDataFactory() {
    this.scriptingSupport = new DataFactoryScriptingSupport();
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
   * @throws org.pentaho.reporting.engine.classic.core.ReportDataFactoryException if an error occured while performing
   *                                                                              the query.
   */
  public Result performQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    final String query = scriptingSupport.computeQuery( queryName, parameters );
    if ( query == null ) {
      throw new ReportDataFactoryException( "No such query: " + queryName );
    }

    return super.performQuery( query, parameters );
  }

  protected TableModel postProcess( final String queryName, final DataRow parameters, final TableModel tableModel )
    throws ReportDataFactoryException {
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

  public ArrayList<Object> getQueryHash( final String queryName, final DataRow parameters )
    throws ReportDataFactoryException {
    final ArrayList<Object> queryHash = super.getQueryHash( queryName, parameters );
    queryHash.add( scriptingSupport.getScriptingLanguage( queryName ) );
    queryHash.add( scriptingSupport.getScript( queryName ) );
    return queryHash;
  }

  public void close() {
    scriptingSupport.shutdown();
    super.close();
  }

  public AbstractNamedMDXDataFactory clone() {
    final AbstractNamedMDXDataFactory dataFactory = (AbstractNamedMDXDataFactory) super.clone();
    //noinspection unchecked
    dataFactory.scriptingSupport = (DataFactoryScriptingSupport) scriptingSupport.clone();
    return dataFactory;
  }
}
