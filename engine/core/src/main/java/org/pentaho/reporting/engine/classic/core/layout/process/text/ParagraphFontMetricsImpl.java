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


package org.pentaho.reporting.engine.classic.core.layout.process.text;

import java.awt.font.TextLayout;

public class ParagraphFontMetricsImpl implements ParagraphFontMetrics {
  private float ascent = 0;
  private float descent = 0;
  private float leading = 0;

  public ParagraphFontMetricsImpl() {
  }

  public void update( TextLayout textLayout ) {
    ascent = Math.max( ascent, textLayout.getAscent() );
    descent = Math.max( descent, textLayout.getDescent() );
    leading = Math.max( leading, textLayout.getLeading() );
  }

  public float getLineHeight() {
    return ascent + descent + leading;
  }

  public float getBaseline() {
    return ascent + leading;
  }

  public float getAscent() {
    return ascent;
  }
}
