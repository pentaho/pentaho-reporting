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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextTransform;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 03.12.2005, 19:56:04
 *
 * @author Thomas Morgner
 */
public class TextTransformReadHandler extends OneOfConstantsReadHandler {
  public TextTransformReadHandler() {
    super( false );
    addValue( TextTransform.CAPITALIZE );
    addValue( TextTransform.LOWERCASE );
    addValue( TextTransform.NONE );
    addValue( TextTransform.UPPERCASE );
  }
}
