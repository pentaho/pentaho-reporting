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

package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

/**
 * The variable token resolves the given 'string' reference.
 *
 * @author Thomas Morgner
 */
public class VariableToken extends ComputedToken {
  private String variable;

  public VariableToken( final String variable ) {
    if ( variable == null ) {
      throw new NullPointerException();
    }
    this.variable = variable;
  }

  public String getVariable() {
    return variable;
  }
}
