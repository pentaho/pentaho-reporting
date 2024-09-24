/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.ui.datasources.pmd.util;

/**
 * A common data-object for representing named queries.
 */
public class DataSetQuery implements Cloneable {
  private String queryName;
  private String query;
  private String scriptLanguage;
  private String script;

  public DataSetQuery( final String queryName, final String query, final String scriptLanguage, final String script ) {
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

  public String getQuery() {
    return query;
  }

  public void setQuery( final String query ) {
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
