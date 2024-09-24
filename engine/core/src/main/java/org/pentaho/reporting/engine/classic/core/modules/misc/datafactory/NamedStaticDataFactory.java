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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;

/**
 * A NamedStaticDataFactory provides an query-aliasing facility to decouple the report definitions from the underlying
 * datasource implentation. The reports no longer need to specify the raw-query (which is in fact just an implementation
 * detail) and can use a symbolic name in the report definition instead.
 *
 * @author Thomas Morgner
 */
public class NamedStaticDataFactory extends StaticDataFactory {
  private LinkedHashMap<String, String> querymappings;

  /**
   * Defaultconstructor.
   */
  public NamedStaticDataFactory() {
    querymappings = new LinkedHashMap<String, String>();
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return querymappings.containsKey( query );
  }

  /**
   * Adds an query-alias to this factory.
   *
   * @param alias
   *          the alias
   * @param queryString
   *          the real query string that should be used when the alias is specified as query.
   */
  public void setQuery( final String alias, final String queryString ) {
    if ( queryString == null ) {
      querymappings.remove( alias );
    } else {
      querymappings.put( alias, queryString );
    }
  }

  /**
   * Derives the factory. The derived factory does no longer share properties with its parent and changes to either
   * factory will not be reflected in the other factory.
   *
   * @return the derived factory.
   */
  public DataFactory derive() {
    return clone();
  }

  /**
   * Returns a clone of the factory.
   *
   * @return the clone.
   */
  public NamedStaticDataFactory clone() {
    final NamedStaticDataFactory nds = (NamedStaticDataFactory) super.clone();
    nds.querymappings = (LinkedHashMap<String, String>) querymappings.clone();
    return nds;
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   *          the alias-name of the query.
   * @param parameters
   *          the set of parameters.
   * @return the tablemodel.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException( "Query is null." ); //$NON-NLS-1$
    }
    final String realQuery = getQuery( query );
    if ( realQuery == null ) {
      throw new ReportDataFactoryException( "Query '" + query + "' is not recognized." ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return super.queryData( realQuery, parameters );
  }

  /**
   * Returns the query for the given alias-name or null, if there is no such alias defined.
   *
   * @param name
   *          the alias name.
   * @return the real query or null.
   */
  public String getQuery( final String name ) {
    return querymappings.get( name );
  }

  /**
   * Returns all known alias-names.
   *
   * @return all alias-names as string-array.
   */
  public String[] getQueryNames() {
    return querymappings.keySet().toArray( new String[querymappings.size()] );
  }

  public String translateQuery( final String queryName ) {
    return querymappings.get( queryName );
  }
}
