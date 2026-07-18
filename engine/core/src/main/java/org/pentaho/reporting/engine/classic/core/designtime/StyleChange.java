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



package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;

/**
 * Simple bean-like class for holding all the information about an attribute change.
 *
 * @author Thomas Morgner.
 */
public class StyleChange implements Change {
  private StyleKey styleKey;
  private Object oldValue;
  private Object newValue;

  public StyleChange( final StyleKey styleKey, final Object oldValue, final Object newValue ) {
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

  public Object getNewValue() {
    return newValue;
  }
}
