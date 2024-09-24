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

package org.pentaho.reporting.engine.classic.core.function.bool;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import javax.swing.table.TableModel;

/**
 * An expression that checks, whether the current report has a non-empty datasource.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula instead or make proper use of the No-Data band.
 */
public class IsEmptyDataExpression extends AbstractExpression {
  /**
   * Default Constructor.
   */
  public IsEmptyDataExpression() {
  }

  /**
   * Checks whether the report has a data-source and wether the datasource is empty. A datasource is considered empty,
   * if it contains no rows. The number of columns is not evaluated.
   *
   * @return the value of the function.
   */
  public Object getValue() {

    final ExpressionRuntime runtime = getRuntime();
    if ( runtime == null ) {
      return null;
    }
    final TableModel data = runtime.getData();
    if ( data == null ) {
      return null;
    }
    if ( data.getRowCount() == 0 ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
