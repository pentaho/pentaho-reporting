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
