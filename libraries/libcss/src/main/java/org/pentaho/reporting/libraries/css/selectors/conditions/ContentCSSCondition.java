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

package org.pentaho.reporting.libraries.css.selectors.conditions;

import org.w3c.css.sac.ContentCondition;

/**
 * Creation-Date: 24.11.2005, 20:20:54
 *
 * @author Thomas Morgner
 */
public class ContentCSSCondition implements CSSCondition, ContentCondition {
  private String data;

  public ContentCSSCondition( final String data ) {
    this.data = data;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_CONTENT_CONDITION;
  }

  /**
   * Returns the content.
   */
  public String getData() {
    return data;
  }
}
