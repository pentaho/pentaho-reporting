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


package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 18:30:42
 *
 * @author Thomas Morgner
 */
public class DefaultKerningProducer implements KerningProducer {
  private int lastCodePoint;
  private FontMetrics fontMetrics;

  public DefaultKerningProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    this.fontMetrics = fontMetrics;
  }

  public long getKerning( final int codePoint ) {
    if ( codePoint == ClassificationProducer.START_OF_TEXT || codePoint == ClassificationProducer.END_OF_TEXT ) {
      lastCodePoint = 0;
      return 0;
    }

    final long d = fontMetrics.getKerning( lastCodePoint, codePoint );
    lastCodePoint = codePoint;
    return d;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
