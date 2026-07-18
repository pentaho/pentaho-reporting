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



package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontEffects;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 17:27:30
 *
 * @author Thomas Morgner
 */
public class FontEffectReadHandler extends OneOfConstantsReadHandler {
  public FontEffectReadHandler() {
    super( false );
    addValue( FontEffects.EMBOSS );
    addValue( FontEffects.ENGRAVE );
    addValue( FontEffects.NONE );
    addValue( FontEffects.OUTLINE );
  }
}
