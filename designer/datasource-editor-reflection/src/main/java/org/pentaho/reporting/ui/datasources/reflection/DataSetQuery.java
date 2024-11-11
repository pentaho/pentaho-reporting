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


package org.pentaho.reporting.ui.datasources.reflection;

/**
 * A common data-object for representing named queries.
 */
public class DataSetQuery implements Cloneable {
  private String queryName;
  private String query;

  public DataSetQuery( final String queryName, final String query ) {
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

    return true;
  }

  public int hashCode() {
    return ( queryName != null ? queryName.hashCode() : 0 );
  }

  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
