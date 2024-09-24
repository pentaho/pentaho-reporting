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
 * The raw data source allows direct access to the filtered raw data. It is mainly used in the table exports, where
 * access to Number and Date objects is a requirement.
 * <p/>
 * There is no enforced requirement to implement this interface, all exports will be able to work without it. But raw
 * datasources definitly improve the quality and value of the generated output, so it is generally a good idea to
 * implement it.
 *
 * @author Thomas Morgner
 */
public interface RawDataSource extends DataSource {
  /**
   * Returns the unformated raw value. Whether that raw value is useable for the export is beyond the scope of this API
   * definition, but providing access to {@link Number} or {@link java.util.Date} objects is a good idea.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the raw data.
   */
  public Object getRawValue( ExpressionRuntime runtime, final ReportElement element );

  /**
   * Returns information about the formatstring that was used to transform a raw-value into a formatted text. Not all
   * elements will make use of a format-string. These elements will return
   * {@link org.pentaho.reporting.engine .classic.core.filter.FormatSpecification#TYPE_UNDEFINED} in that case.
   *
   * @param runtime
   *          the Expression runtime used to possibly compute the raw-value.
   * @param element
   *          the element to which this datasource is added.
   * @param formatSpecification
   *          the format specification (can be null). @return a filled format specififcation. If the
   *          <code>formatSpecification</code> parameter was not null, this given instance is reused.
   */
  public FormatSpecification getFormatString( ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification );
}
