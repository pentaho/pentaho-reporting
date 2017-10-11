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
