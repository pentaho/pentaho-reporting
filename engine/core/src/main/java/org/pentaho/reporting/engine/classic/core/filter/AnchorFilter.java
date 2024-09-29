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


package org.pentaho.reporting.engine.classic.core.filter;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * The AnchorFilter is deprecated and does no longer work. Use the style-key "anchor" instead.
 *
 * @author Thomas Morgner
 * @deprecated The anchor filter is deprecated now. Use the stylekey "anchor" instead.
 */
public class AnchorFilter implements DataFilter {
  /**
   * The data source from where to get the values for the anchor.
   */
  private DataSource dataSource;

  /**
   * DefaultConstructor.
   */
  public AnchorFilter() {
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public AnchorFilter clone() throws CloneNotSupportedException {
    final AnchorFilter af = (AnchorFilter) super.clone();
    if ( dataSource == null ) {
      af.dataSource = null;
    } else {
      af.dataSource = dataSource.clone();
    }
    return af;
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return null;
  }

  /**
   * Returns the assigned DataSource for this Target.
   *
   * @return The datasource.
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Assigns a DataSource for this Target.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( final DataSource ds ) {
    this.dataSource = ds;
  }
}
