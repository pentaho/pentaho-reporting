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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model;

public class Query<T> {
  private String name;
  private T query;
  private String queryLanguage;
  private String queryScript;

  public Query( final String name, final T query ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    this.name = name;
    this.query = query;
  }

  public Query( final String name, final T query, final String queryLanguage, final String queryScript ) {
    this( name, query );
    this.queryLanguage = queryLanguage;
    this.queryScript = queryScript;
  }

  public Query<T> updateName( final String name ) {
    return new Query<T>( name, query, queryLanguage, queryScript );
  }

  public Query<T> updateQuery( final T query ) {
    return new Query<T>( name, query, queryLanguage, queryScript );
  }

  public Query<T> updateQueryScript( final String queryLanguage, final String queryScript ) {
    return new Query<T>( name, query, queryLanguage, queryScript );
  }

  public String getName() {
    return name;
  }

  public T getQuery() {
    return query;
  }

  public String getQueryLanguage() {
    return queryLanguage;
  }

  public String getQueryScript() {
    return queryScript;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final Query query1 = (Query) o;

    if ( !name.equals( query1.name ) ) {
      return false;
    }
    if ( query != null ? !query.equals( query1.query ) : query1.query != null ) {
      return false;
    }
    if ( queryLanguage != null ? !queryLanguage.equals( query1.queryLanguage ) : query1.queryLanguage != null ) {
      return false;
    }
    if ( queryScript != null ? !queryScript.equals( query1.queryScript ) : query1.queryScript != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + ( query != null ? query.hashCode() : 0 );
    result = 31 * result + ( queryLanguage != null ? queryLanguage.hashCode() : 0 );
    result = 31 * result + ( queryScript != null ? queryScript.hashCode() : 0 );
    return result;
  }
}
