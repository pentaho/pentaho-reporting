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

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.color.ColorReadHandler;

/**
 * Creation-Date: 03.12.2005, 19:06:09
 *
 * @author Thomas Morgner
 */
public class TextOverlineReadHandler extends AbstractCompoundValueReadHandler {
  public TextOverlineReadHandler() {
    addHandler( TextStyleKeys.TEXT_OVERLINE_STYLE, new TextDecorationStyleReadHandler() );
    addHandler( TextStyleKeys.TEXT_OVERLINE_COLOR, new ColorReadHandler() );
    addHandler( TextStyleKeys.TEXT_OVERLINE_WIDTH, new TextDecorationWidthReadHandler() );
    addHandler( TextStyleKeys.TEXT_OVERLINE_MODE, new TextDecorationModeReadHandler() );
  }
}
