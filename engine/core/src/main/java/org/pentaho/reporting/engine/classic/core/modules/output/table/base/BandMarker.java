/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
