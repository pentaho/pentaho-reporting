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


package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.TextTransform;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 15:12:04
 *
 * @author Thomas Morgner
 */
public class TextTransformResolveHandler extends ConstantsResolveHandler {
  public TextTransformResolveHandler() {
    addNormalizeValue( TextTransform.CAPITALIZE );
    addNormalizeValue( TextTransform.LOWERCASE );
    addNormalizeValue( TextTransform.NONE );
    addNormalizeValue( TextTransform.UPPERCASE );
    setFallback( TextTransform.NONE );
  }
}
