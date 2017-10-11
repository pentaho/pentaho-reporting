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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public abstract class AbstractScriptableDataFactory extends AbstractDataFactory {
  private static final Log logger = LogFactory.getLog( AbstractScriptableDataFactory.class );
  private final DataFactoryScriptingSupport scriptingSupport;

  protected AbstractScriptableDataFactory() {
    scriptingSupport = new DataFactoryScriptingSupport();
  }

  public final TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException( "Query is null." ); //$NON-NLS-1$
    }
    final String realQuery = scriptingSupport.computeQuery( query, parameters );
    if ( realQuery == null ) {
      throw new ReportDataFactoryException( "Query '" + query + "' is not recognized." ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return queryDataInternal( realQuery, parameters );
  }

  protected abstract TableModel queryDataInternal( final String realQuery, final DataRow parameters )
    throws ReportDataFactoryException;

  public boolean isQueryExecutable( final String query, final DataRow dataRow ) {
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
    setQuery( name, queryString, null, null );
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

  public final String[] getReferencedFields( final String query, final DataRow parameter ) {
    try {
      final String[] additionalFields = scriptingSupport.computeAdditionalQueryFields( query, parameter );
      if ( additionalFields == null ) {
        return null;
      }

      final String realQuery = scriptingSupport.computeQuery( query, parameter );
      if ( realQuery == null ) {
        throw new ReportDataFactoryException( "Query '" + query + "' is not recognized." ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      String[] referencedFieldsInternal = getReferencedFieldsInternal( realQuery, parameter );
      if ( referencedFieldsInternal == null ) {
        return null;
      }

      final LinkedHashSet<String> fields = new LinkedHashSet<String>();
      fields.addAll( Arrays.asList( referencedFieldsInternal ) );
      fields.addAll( Arrays.asList( additionalFields ) );
      return fields.toArray( new String[fields.size()] );
    } catch ( final ReportDataFactoryException rx ) {
      logger.debug( "Failed to compute referenced fields", rx ); // NON-NLS
      return null;
    }
  }

  protected abstract String[] getReferencedFieldsInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException;

  public final Object getQueryHash( final String query, final DataRow parameter ) {
    try {
      final String realQuery = scriptingSupport.computeQuery( query, parameter );
      if ( realQuery == null ) {
        throw new ReportDataFactoryException( "Query '" + query + "' is not recognized." ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      Object queryHashInternal = getQueryHashInternal( realQuery, parameter );
      if ( queryHashInternal == null ) {
        return null;
      }

      final ArrayList<Object> queryHash = new ArrayList<Object>();
      queryHash.add( getClass().getName() );
      queryHash.add( queryHashInternal );
      queryHash.add( scriptingSupport.getScriptingLanguage( query ) );
      queryHash.add( scriptingSupport.getScript( query ) );
      return queryHash;
    } catch ( final ReportDataFactoryException rx ) {
      logger.debug( "Failed to compute query hash", rx ); // NON-NLS
      return null;
    }
  }

  protected abstract Object getQueryHashInternal( final String realQuery, final DataRow parameter )
    throws ReportDataFactoryException;

}
