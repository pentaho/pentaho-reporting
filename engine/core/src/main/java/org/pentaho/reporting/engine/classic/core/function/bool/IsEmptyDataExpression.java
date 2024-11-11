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
