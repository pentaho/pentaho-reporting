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


package org.pentaho.reporting.ui.datasources.xpath;

/**
 * A common data-object for representing named queries.
 */
public class DataSetQuery implements Cloneable {
  private String queryName;
  private String query;
  private boolean legacyQuery;

  public DataSetQuery( final String queryName, final String query, final boolean legacyQuery ) {
    this.legacyQuery = legacyQuery;
    //noinspection ConstantConditions
    if ( queryName == null ) {
      throw new IllegalArgumentException( "queryName must not be null" );
    }
    //noinspection ConstantConditions
    if ( query == null ) {
      throw new IllegalArgumentException( "query must not be null" );
    }

    this.queryName = queryName;
    this.query = query;
  }

  public boolean isLegacyQuery() {
    return legacyQuery;
  }

  public void setLegacyQuery( final boolean legacyQuery ) {
    this.legacyQuery = legacyQuery;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName( final String queryName ) {
    //noinspection ConstantConditions
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
    if ( legacyQuery != that.legacyQuery ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int hashCode = ( queryName != null ? queryName.hashCode() : 0 );
    hashCode *= 23 + ( legacyQuery ? 0 : 1 );
    return hashCode;
  }

  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
