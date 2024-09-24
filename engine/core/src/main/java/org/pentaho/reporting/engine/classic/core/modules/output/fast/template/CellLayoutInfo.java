/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;

public class CellLayoutInfo extends TableRectangle {
  private CellBackground background;

  public CellLayoutInfo( TableRectangle rect, final CellBackground background ) {
    setRect( rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2() );
    this.background = background;
  }

  public CellLayoutInfo( final int x1, final int y1, final CellBackground background ) {
    setRect( x1, y1, x1, y1 );
    this.background = background;
  }

  public CellBackground getBackground() {
    return background;
  }
}
