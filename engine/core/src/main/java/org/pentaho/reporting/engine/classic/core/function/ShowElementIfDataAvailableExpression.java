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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;

/**
 * This functions checks the tablemodel and hides the named elements, if there is no data available.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula instead.
 */
public class ShowElementIfDataAvailableExpression extends AbstractElementFormatFunction {
  /**
   * Default Constructor.
   */
  public ShowElementIfDataAvailableExpression() {
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, isDataAvailable() );
      return true;
    }
    return false;
  }

  /**
   * Computes the visibility state.
   *
   * @return true, if there is data available, false otherwise.
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
