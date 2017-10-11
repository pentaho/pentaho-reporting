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
