/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

public class SheetLayoutTableCellDefinition {
  protected static final int LINE_HINT_NONE = 0;
  protected static final int LINE_HINT_VERTICAL = 1;
  protected static final int LINE_HINT_HORIZONTAL = 2;

  private int lineType; // 0 none, 1 horizontal, 2 vertical
  private long coordinate;

  public SheetLayoutTableCellDefinition() {
    this.lineType = LINE_HINT_NONE;
  }

  public SheetLayoutTableCellDefinition( final int lineType, final long coordinate ) {
    this.lineType = lineType;
    this.coordinate = coordinate;
  }

  public int getLineType() {
    return lineType;
  }

  public long getCoordinate() {
    return coordinate;
  }
}
