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



package org.pentaho.reporting.engine.classic.core.layout.process.text;

import java.awt.font.TextLayout;

public class LineBreakIteratorState {
  private int start;
  private int end;
  private TextLayout textLayout;

  LineBreakIteratorState( final TextLayout textLayout, final int start, final int end ) {
    this.textLayout = textLayout;
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public TextLayout getTextLayout() {
    return textLayout;
  }
}
