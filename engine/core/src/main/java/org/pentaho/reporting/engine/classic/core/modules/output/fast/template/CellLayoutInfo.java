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
