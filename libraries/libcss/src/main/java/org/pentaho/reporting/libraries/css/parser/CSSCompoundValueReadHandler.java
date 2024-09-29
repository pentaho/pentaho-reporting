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


package org.pentaho.reporting.libraries.css.parser;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.w3c.css.sac.LexicalUnit;

import java.util.Map;

/**
 * Creation-Date: 26.11.2005, 19:45:45
 *
 * @author Thomas Morgner
 */
public interface CSSCompoundValueReadHandler {
  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit );

  public StyleKey[] getAffectedKeys();
}
