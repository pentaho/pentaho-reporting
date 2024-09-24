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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

/**
 * A common data-object for representing named queries.
 */
public class DataSetQuery<T> implements Cloneable {
  private String queryName;
  private T query;
  private String scriptLanguage;
  private String script;

  public DataSetQuery( final String queryName, final T query ) {
    this( queryName, query, null, null );
  }

  public DataSetQuery( final String queryName, final T query, final String scriptLanguage, final String script ) {
    if ( queryName == null ) {
      throw new IllegalArgumentException( "queryName must not be null" );
    }
    if ( query == null ) {
      throw new IllegalArgumentException( "query must not be null" );
    }

    this.queryName = queryName;
    this.query = query;
    this.scriptLanguage = scriptLanguage;
    this.script = script;
  }

  public String getScriptLanguage() {
    return scriptLanguage;
  }

  public void setScriptLanguage( final String scriptLanguage ) {
    this.scriptLanguage = scriptLanguage;
  }

  public String getScript() {
    return script;
  }

  public void setScript( final String script ) {
    this.script = script;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName( final String queryName ) {
    if ( queryName == null ) {
      throw new IllegalArgumentException( "queryName must not be null" );
    }

    this.queryName = queryName;
  }

  public T getQuery() {
    return query;
  }

  public void setQuery( final T query ) {
    this.query = query;
  }

  public String toString() {
    return queryName.length() > 0 ? queryName : " ";
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DataSetQuery that = (DataSetQuery) o;

    if ( queryName != null ? !queryName.equals( that.queryName ) : that.queryName != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( queryName != null ? queryName.hashCode() : 0 );
  }

  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
