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

/**
 * Creation-Date: 04.10.2007, 14:54:24
 *
 * @author Thomas Morgner
 */
public class ContentMarker implements CellMarker {
  private RenderBox content;
  private long effectiveShift;
  private SectionType sectionType;

  public ContentMarker( final RenderBox content, final long effectiveShift, final SectionType sectionType ) {
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.effectiveShift = effectiveShift;
    this.sectionType = sectionType;
    this.content = content;
  }

  public long getContentOffset() {
    return effectiveShift;
  }

  public RenderBox getContent() {
    return content;
  }

  public boolean isCommited() {
    return content.isCommited();
  }

  public boolean isFinished() {
    return content.isFinishedTable();
  }

  public SectionType getSectionType() {
    return sectionType;
  }

  public String toString() {
    return content.toString();
  }

  public int getSectionDepth() {
    return Integer.MAX_VALUE;
  }
}
