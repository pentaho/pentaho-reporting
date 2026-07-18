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



package org.pentaho.reporting.libraries.css.values;

import java.io.Serializable;

/**
 * Creation-Date: 23.11.2005, 11:32:55
 *
 * @author Thomas Morgner
 */
public interface CSSValue extends Serializable {
  public String getCSSText();

  public CSSType getType();
}
