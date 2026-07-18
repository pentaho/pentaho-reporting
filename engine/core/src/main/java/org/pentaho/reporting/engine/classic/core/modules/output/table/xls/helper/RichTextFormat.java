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



package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

public class RichTextFormat {
  private int position;
  private HSSFFontWrapper font;

  public RichTextFormat( final int position, final HSSFFontWrapper font ) {
    if ( font == null ) {
      throw new NullPointerException();
    }
    this.position = position;
    this.font = font;
  }

  public int getPosition() {
    return position;
  }

  public HSSFFontWrapper getFont() {
    return font;
  }

}
