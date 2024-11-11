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
