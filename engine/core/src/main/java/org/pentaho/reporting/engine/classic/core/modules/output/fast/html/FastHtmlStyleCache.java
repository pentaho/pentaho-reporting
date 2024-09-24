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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.util.HashMap;

public class FastHtmlStyleCache {
  private static class CellKey {
    private int row;
    private int col;

    private CellKey( final int row, final int col ) {
      this.row = row;
      this.col = col;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CellKey cellKey = (CellKey) o;

      if ( col != cellKey.col ) {
        return false;
      }
      if ( row != cellKey.row ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = row;
      result = 31 * result + col;
      return result;
    }
  }

  public static class CellStyle {
    private AttributeList cellAttributeList;
    private StyleBuilder.StyleCarrier[] cellStyle;

    public CellStyle( final AttributeList cellAttributeList, final StyleBuilder.StyleCarrier[] cellStyle ) {
      this.cellAttributeList = cellAttributeList;
      this.cellStyle = cellStyle;
    }

    public AttributeList getCellAttributeList() {
      return cellAttributeList;
    }

    public StyleBuilder.StyleCarrier[] getCellStyle() {
      return cellStyle;
    }
  }

  private HashMap<Integer, AttributeList> rowAttributes;
  private HashMap<CellKey, CellStyle> cellAttributes;

  public FastHtmlStyleCache() {
    rowAttributes = new HashMap<Integer, AttributeList>();
    cellAttributes = new HashMap<CellKey, CellStyle>();
  }

  public AttributeList getRowAttributes( int row ) {
    return rowAttributes.get( row );
  }

  public void putRowAttributes( int row, AttributeList attrs ) {
    rowAttributes.put( row, attrs );
  }

  public CellStyle getCellAttributes( int row, int col ) {
    return cellAttributes.get( new CellKey( row, col ) );
  }

  public void putCellAttributes( int row, int col, CellStyle attrs ) {
    cellAttributes.put( new CellKey( row, col ), attrs );
  }
}
