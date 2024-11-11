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


package org.pentaho.reporting.engine.classic.core.sorting;

import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.io.Serializable;

public class SortConstraint implements Serializable {
  private String field;
  private boolean ascending;

  public SortConstraint( final String field, final boolean ascending ) {
    ArgumentNullException.validate( "field", field );

    this.field = field;
    this.ascending = ascending;
  }

  public String getField() {
    return field;
  }

  public boolean isAscending() {
    return ascending;
  }

  public String toString() {
    return "SortConstraint{" + "field='" + field + '\'' + ", ascending=" + ascending + '}';
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final SortConstraint that = (SortConstraint) o;

    if ( ascending != that.ascending ) {
      return false;
    }
    if ( !field.equals( that.field ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = field.hashCode();
    result = 31 * result + ( ascending ? 1 : 0 );
    return result;
  }
}
