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
