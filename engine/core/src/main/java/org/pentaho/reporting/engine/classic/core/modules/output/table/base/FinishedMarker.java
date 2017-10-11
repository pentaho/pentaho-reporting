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
