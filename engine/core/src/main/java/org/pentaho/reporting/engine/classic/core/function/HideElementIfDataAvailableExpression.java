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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;

/**
 * This functions checks the tablemodel and shows the named band, if there is no data available.
 *
 * @author Thomas Morgner
 * @deprecated Use a Style-Expression or make proper use of the No-Data-Band
 */
public class HideElementIfDataAvailableExpression extends AbstractElementFormatFunction {
  /**
   * Default Constructor.
   */
  public HideElementIfDataAvailableExpression() {
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, !isDataAvailable() );
      return true;
    }
    return false;
  }

  /**
   * Computes, if there is data available. This checks the report's data-row and searches for the number of rows in the
   * underlying tablemodel.
   *
   * @return true, if the tablemodel contains data, false otherwise.
   */
  private boolean isDataAvailable() {
    final ExpressionRuntime runtime = getRuntime();
    if ( runtime == null ) {
      return false;
    }
    final TableModel data = runtime.getData();
    if ( data == null ) {
      return false;
    }
    if ( data.getRowCount() == 0 ) {
      return false;
    }
    return true;
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    if ( isDataAvailable() ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
