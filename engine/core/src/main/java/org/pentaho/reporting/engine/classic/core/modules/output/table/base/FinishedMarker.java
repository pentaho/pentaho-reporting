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


package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class FinishedMarker implements CellMarker {
  public static final FinishedMarker INSTANCE = new FinishedMarker();
  private String text;

  private FinishedMarker() {
  }

  public FinishedMarker( final String text ) {
    this.text = "FinishedMarker: " + text;
  }

  public boolean isFinished() {
    return true;
  }

  public RenderBox getContent() {
    return null;
  }

  public boolean isCommited() {
    return true;
  }

  public long getContentOffset() {
    return 0;
  }

  public SectionType getSectionType() {
    return SectionType.TYPE_INVALID;
  }

  public String toString() {
    if ( text == null ) {
      return super.toString();
    }
    return text;
  }

  public int getSectionDepth() {
    return Integer.MAX_VALUE;
  }
}
