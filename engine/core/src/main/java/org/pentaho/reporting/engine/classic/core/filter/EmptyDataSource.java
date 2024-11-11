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
 * A data source that always returns null.
 *
 * @author Thomas Morgner
 */
public final class EmptyDataSource implements DataSource {
  /**
   * Default-Constructor.
   */
  public EmptyDataSource() {
  }

  /**
   * Returns the value for the data source (always null in this case).
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return always null.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return null;
  }

  /**
   * Clones the data source.
   *
   * @return a clone.
   * @noinspection CloneDoesntCallSuperClone
   */
  public EmptyDataSource clone() {
    return this;
  }

}
