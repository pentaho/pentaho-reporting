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



package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.libraries.fonts.text.Spacing;

public interface Glyph {
  int SPACE_CHAR = 0;
  int LETTER = 1;

  int getClassification();

  int[] getExtraChars();

  int getBaseLine();

  int getCodepoint();

  int getBreakWeight();

  Spacing getSpacing();

  int getWidth();

  int getHeight();

  int getKerning();

  String toString();
}
