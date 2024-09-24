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

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;

public class ClassCSSCondition extends AttributeCSSCondition {
  public ClassCSSCondition( final String namespace, final String value ) {
    super( "class", namespace, true, value );
  }

  public short getConditionType() {
    return CSSCondition.SAC_CLASS_CONDITION;
  }

  public String print( final NamespaceCollection namespaces ) {
    return "." + getValue();
  }
}
