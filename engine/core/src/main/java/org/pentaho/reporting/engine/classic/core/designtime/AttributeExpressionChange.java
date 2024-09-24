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

/**
 * Simple bean-like class for holding all the information about an attribute change.
 *
 * @author Thomas Morgner.
 */
public class AttributeExpressionChange implements Change {
  private String namespace;
  private String name;
  private Expression oldValue;
  private Expression newValue;

  public AttributeExpressionChange( final String namespace, final String name, final Expression oldValue,
      final Expression newValue ) {
    this.namespace = namespace;
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
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
