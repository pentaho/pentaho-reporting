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


package org.pentaho.reporting.libraries.css.parser.stylehandler.color;

import org.pentaho.reporting.libraries.css.keys.color.RenderingIntent;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 27.11.2005, 20:34:09
 *
 * @author Thomas Morgner
 */
public class RenderingIntentReadHandler extends OneOfConstantsReadHandler {
  public RenderingIntentReadHandler() {
    super( true );
    addValue( RenderingIntent.ABSOLUTE_COLORIMETRIC );
    addValue( RenderingIntent.PERCEPTUAL );
    addValue( RenderingIntent.RELATIVE_COLORIMETRIC );
    addValue( RenderingIntent.SATURATION );
  }
}
