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



package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 28.11.2005, 19:21:46
 *
 * @author Thomas Morgner
 */
public class DropInitialBeforeAlignReadHandler extends AlignmentBaselineReadHandler {
  public DropInitialBeforeAlignReadHandler() {
    addValue( new CSSConstant( "caps-height" ) );
  }
}
