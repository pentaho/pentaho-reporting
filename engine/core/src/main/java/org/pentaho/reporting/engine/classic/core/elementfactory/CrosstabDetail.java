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


package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemCountFunction;

import java.io.Serializable;

public class CrosstabDetail implements Serializable, Cloneable {
  private String title;
  private String field;
  private Class<? extends AggregationFunction> aggregation;

  public CrosstabDetail( final String fieldName ) {
    this( fieldName, null, ItemCountFunction.class );
  }

  public CrosstabDetail( final String field, final String title, final Class<? extends AggregationFunction> aggregation ) {
    this.title = title;
    this.field = field;
    this.aggregation = aggregation;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( final String title ) {
    this.title = title;
  }

  public String getField() {
    return field;
  }

  public void setField( final String field ) {
    this.field = field;
  }

  public Class<? extends AggregationFunction> getAggregation() {
    return aggregation;
  }

  public void setAggregation( final Class<? extends AggregationFunction> aggregation ) {
    this.aggregation = aggregation;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CrosstabDetail that = (CrosstabDetail) o;

    if ( aggregation != null ? !aggregation.equals( that.aggregation ) : that.aggregation != null ) {
      return false;
    }
    if ( field != null ? !field.equals( that.field ) : that.field != null ) {
      return false;
    }
    if ( title != null ? !title.equals( that.title ) : that.title != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + ( field != null ? field.hashCode() : 0 );
    result = 31 * result + ( aggregation != null ? aggregation.hashCode() : 0 );
    return result;
  }

  public CrosstabDetail clone() {
    try {
      return (CrosstabDetail) super.clone();
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }
}
