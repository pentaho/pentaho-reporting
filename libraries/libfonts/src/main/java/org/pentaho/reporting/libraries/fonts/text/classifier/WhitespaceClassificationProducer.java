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



package org.pentaho.reporting.libraries.fonts.text.classifier;

/**
 * Creation-Date: 26.06.2006, 16:36:50
 *
 * @author Thomas Morgner
 */
public class WhitespaceClassificationProducer implements GlyphClassificationProducer {
  public WhitespaceClassificationProducer() {
  }

  public int getClassification( final int codepoint ) {
    if ( Character.isWhitespace( (char) codepoint ) ) {
      return GlyphClassificationProducer.SPACE_CHAR;
    }
    return GlyphClassificationProducer.LETTER;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void reset() {

  }
}
