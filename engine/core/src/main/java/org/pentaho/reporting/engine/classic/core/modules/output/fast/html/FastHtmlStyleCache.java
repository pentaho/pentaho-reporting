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
