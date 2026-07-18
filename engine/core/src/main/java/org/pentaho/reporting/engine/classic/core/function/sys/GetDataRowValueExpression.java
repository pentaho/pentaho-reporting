/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.function.sys;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

public class GetDataRowValueExpression extends AbstractExpression {
  private String field;

  public GetDataRowValueExpression() {
  }

  public String getField() {
    return field;
  }

  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    if ( getField() != null ) {
      return getDataRow().get( getField() );
    }
    return null;
  }
}
