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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextDecorationStyle;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 02.12.2005, 20:09:00
 *
 * @author Thomas Morgner
 */
public class TextDecorationStyleReadHandler extends OneOfConstantsReadHandler {
  public TextDecorationStyleReadHandler() {
    super( false );
    addValue( TextDecorationStyle.DASHED );
    addValue( TextDecorationStyle.DOT_DASH );
    addValue( TextDecorationStyle.DOT_DOT_DASH );
    addValue( TextDecorationStyle.DOTTED );
    addValue( TextDecorationStyle.DOUBLE );
    addValue( TextDecorationStyle.NONE );
    addValue( TextDecorationStyle.SOLID );
    addValue( TextDecorationStyle.WAVE );
  }
}
