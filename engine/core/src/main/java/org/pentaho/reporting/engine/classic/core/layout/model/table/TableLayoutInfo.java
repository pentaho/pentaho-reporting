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

package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;

public class TableLayoutInfo {
  private RenderLength rowSpacing;

  private boolean displayEmptyCells;
  private boolean collapsingBorderModel;
  private boolean autoLayout;

  public TableLayoutInfo() {
  }

  public RenderLength getRowSpacing() {
    return rowSpacing;
  }

  public void setRowSpacing( final RenderLength rowSpacing ) {
    this.rowSpacing = rowSpacing;
  }

  public boolean isDisplayEmptyCells() {
    return displayEmptyCells;
  }

  public void setDisplayEmptyCells( final boolean displayEmptyCells ) {
    this.displayEmptyCells = displayEmptyCells;
  }

  public boolean isCollapsingBorderModel() {
    return collapsingBorderModel;
  }

  public void setCollapsingBorderModel( final boolean collapsingBorderModel ) {
    this.collapsingBorderModel = collapsingBorderModel;
  }

  public boolean isAutoLayout() {
    return autoLayout;
  }

  public void setAutoLayout( final boolean autoLayout ) {
    this.autoLayout = autoLayout;
  }
}
