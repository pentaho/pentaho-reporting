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

package org.pentaho.reporting.libraries.fonts.text.classifier;

/**
 * Creation-Date: 26.06.2006, 16:36:50
 *
 * @author Thomas Morgner
 */
public class LinebreakClassificationProducer implements GlyphClassificationProducer {

  public LinebreakClassificationProducer() {
  }

  public int getClassification( final int codepoint ) {
    if ( isLinebreak( codepoint ) ) {
      return GlyphClassificationProducer.SPACE_CHAR;
    }
    return GlyphClassificationProducer.LETTER;
  }

  protected boolean isLinebreak( final int codepoint ) {
    if ( codepoint == 0xa || codepoint == 0xd ) {
      return true;
    } else {
      return false;
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void reset() {

  }
}
