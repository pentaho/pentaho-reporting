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

public class BandMarker implements CellMarker {
  private RenderBox bandBox;
  private SectionType sectionType;
  private int sectionDepth;

  public BandMarker( final RenderBox bandBox, final SectionType sectionType, final int sectionDepth ) {
    this.bandBox = bandBox;
    this.sectionType = sectionType;
    this.sectionDepth = sectionDepth;
  }

  public RenderBox getBandBox() {
    return bandBox;
  }

  public long getContentOffset() {
    return 0;
  }

  public boolean isFinished() {
    return true;
  }

  public boolean isCommited() {
    return true;
  }

  public RenderBox getContent() {
    return null;
  }

  public SectionType getSectionType() {
    return sectionType;
  }

  public int getSectionDepth() {
    return sectionDepth;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "BandMarker" );
    sb.append( "{bandBox=" ).append( bandBox );
    sb.append( ", sectionType=" ).append( sectionType );
    sb.append( ", sectionDepth=" ).append( sectionDepth );
    sb.append( '}' );
    return sb.toString();
  }
}
