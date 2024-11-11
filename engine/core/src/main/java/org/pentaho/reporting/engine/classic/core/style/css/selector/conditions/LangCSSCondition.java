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


package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.LangCondition;

/**
 * Creation-Date: 24.11.2005, 19:54:48
 *
 * @author Thomas Morgner
 */
public class LangCSSCondition implements LangCondition, CSSCondition {
  private String lang;

  public LangCSSCondition( final String value ) {
    this.lang = value;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_LANG_CONDITION;
  }

  /**
   * Returns the language
   */
  public String getLang() {
    return lang;
  }

  public String print( final NamespaceCollection namespaces ) {
    return ":lang(" + lang + ")";
  }
}
