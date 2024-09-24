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

package org.pentaho.reporting.engine.classic.core.filter;

import java.awt.Shape;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A shape filter.
 *
 * @author Thomas Morgner.
 */
public class ShapeFilter implements DataFilter {
  /**
   * The data source.
   */
  private DataSource dataSource;

  /**
   * Default constructor.
   */
  public ShapeFilter() {
  }

  /**
   * Returns the data source for the filter.
   *
   * @return The data source.
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Sets the data source for the filter.
   *
   * @param dataSource
   *          The data source.
   */
  public void setDataSource( final DataSource dataSource ) {
    this.dataSource = dataSource;
  }

  /**
   * Returns the current value for the data source.
   * <P>
   * The returned object, unless it is null, will be an instance of ImageReference.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return The value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return null;
    }
    final Object o = ds.getValue( runtime, element );
    if ( o instanceof Shape ) {
      return o;
    }
    return null;
  }

  /**
   * Clones the filter.
   *
   * @return A clone of this filter.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ShapeFilter clone() throws CloneNotSupportedException {
    final ShapeFilter r = (ShapeFilter) super.clone();
    if ( dataSource != null ) {
      r.dataSource = dataSource.clone();
    }
    return r;
  }

}
