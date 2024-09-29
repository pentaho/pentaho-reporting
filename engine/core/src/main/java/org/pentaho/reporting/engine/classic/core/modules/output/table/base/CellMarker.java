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


package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public interface CellMarker {
  public enum SectionType {
    TYPE_INVALID, TYPE_NORMALFLOW, TYPE_HEADER, TYPE_FOOTER, TYPE_REPEAT_FOOTER;
  }

  public long getContentOffset();

  public boolean isFinished();

  public boolean isCommited();

  public RenderBox getContent();

  public SectionType getSectionType();

  public int getSectionDepth();
}
