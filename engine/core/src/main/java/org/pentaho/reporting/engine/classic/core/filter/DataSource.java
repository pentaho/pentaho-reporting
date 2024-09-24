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

import java.io.Serializable;

/**
 * A DataSource is a producer in the data chain. Common Sources are StaticSources (predefined data), ReportDataSources
 * (data filled from the reports data set) or FunctionDataSource (the data is filled by querying an assigned function).
 * <p/>
 * All DataSources have to support the Cloneable interface so that a report can be completley cloned with all assigned
 * filters and DataSources. Reports are cloned before they are processed to remove the side effect when having multiple
 * report processors working on the same object.
 *
 * @author Thomas Morgner
 */
public interface DataSource extends Serializable, Cloneable {
  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element for which the data is computed.
   * @return the value.
   */
  public Object getValue( ExpressionRuntime runtime, final ReportElement element );

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public DataSource clone() throws CloneNotSupportedException;

}
