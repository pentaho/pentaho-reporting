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

package org.pentaho.reporting.libraries.css.parser.stylehandler.border;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.w3c.css.sac.LexicalUnit;

import java.util.Map;

/**
 * Creation-Date: 26.11.2005, 19:39:19
 *
 * @author Thomas Morgner
 */
public class BackgroundReadHandler implements CSSCompoundValueReadHandler {
  public BackgroundReadHandler() {
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    // todo this is a complex parsing task
    return null;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[ 0 ];
  }
}
