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


package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

public class OneOfAttributeCSSCondition extends AttributeCSSCondition {
  public OneOfAttributeCSSCondition( final String name, final String namespace, final boolean specified,
      final String value ) {
    super( name, namespace, specified, value );
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_ONE_OF_ATTRIBUTE_CONDITION;
  }

  protected String getSelectorIndicator() {
    return "~=";
  }
}
