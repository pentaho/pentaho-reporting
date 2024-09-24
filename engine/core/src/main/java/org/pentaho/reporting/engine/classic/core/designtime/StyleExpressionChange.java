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

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

/**
 * Simple bean-like class for holding all the information about an attribute change.
 *
 * @author Thomas Morgner.
 */
public class StyleExpressionChange implements Change {
  private StyleKey styleKey;
  private Expression oldValue;
  private Expression newValue;

  public StyleExpressionChange( final StyleKey styleKey, final Expression oldValue, final Expression newValue ) {
    this.styleKey = styleKey;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public StyleKey getStyleKey() {
    return styleKey;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Expression getOldExpression() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public Expression getNewExpression() {
    return newValue;
  }
}
