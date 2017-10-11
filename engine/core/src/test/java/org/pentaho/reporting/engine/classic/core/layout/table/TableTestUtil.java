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

package org.pentaho.reporting.engine.classic.core.layout.table;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;

public class TableTestUtil {
  public static interface ElementProducer {
    public Element createDataItem( final String text, final int row, final int column );

    public Band createCell( final int row, final int column );
  }

  public static class DefaultElementProducer implements ElementProducer {
    private boolean createText;
    private float width;
    private float height;

    public DefaultElementProducer( final boolean createText ) {
      this.createText = createText;
      this.width = 100;
      this.height = 200;
    }

    public DefaultElementProducer( final float width, final float height ) {
      this.createText = true;
      this.width = width;
      this.height = height;
    }

    public Band createCell( final int row, final int column ) {
      return TableTestUtil.createCell( 1, 1 );
    }

    public Element createDataItem( final String text, final int row, final int column ) {
      if ( createText ) {
        return TableTestUtil.createDataItem( text, width, height );
      }
      return null;
    }
  }

  public static Band createRow( final Element... boxes ) {
    final Band tableRow = new Band();
    tableRow.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    tableRow.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    for ( int i = 0; i < boxes.length; i++ ) {
      tableRow.addElement( boxes[i] );
    }
    return tableRow;
  }

  public static Band createAutoBox( final Element... boxes ) {
    final Band tableRow = new Band();
    tableRow.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_AUTO );
    for ( int i = 0; i < boxes.length; i++ ) {
      tableRow.addElement( boxes[i] );
    }
    return tableRow;
  }

  public static Band createCell( final Element dataItem ) {
    return createCell( dataItem, 1, 1 );
  }

  public static Band createCell( final Element dataItem, final int rowSpan, final int colSpan ) {
    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 150f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, rowSpan );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, colSpan );
    tableCell.addElement( dataItem );
    return tableCell;
  }

  public static Band createCell( final int rowNumber, final int colNumber, final float cellWidth,
      final float cellHeight, final Element... elements ) {
    final Band cell = new Band();
    cell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    cell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, cellWidth );
    cell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, cellHeight );
    cell.setName( "c-" + rowNumber + "-" + colNumber );

    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[i];
      cell.addElement( element );
    }
    return cell;
  }

  public static Element createDataItem( final String text ) {
    return createDataItem( text, 100, 200 );
  }

  public static Element createDataItem( final String text, final float width, final float height ) {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    return label;
  }

  public static Band createTable( final int columns, final int headerRows, final int dataRows ) {
    return createTable( columns, headerRows, dataRows, false );
  }

  public static Band createTable( final int columns, final int headerRows, final int dataRows, final boolean addData ) {
    return createTable( columns, headerRows, dataRows, new DefaultElementProducer( addData ) );
  }

  public static Band createTable( final int columns, final int headerRows, final int dataRows,
      final ElementProducer producer ) {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );

    if ( headerRows > 0 ) {
      final Band tableHeader = new Band();
      tableHeader.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_HEADER );

      for ( int r = 0; r < headerRows; r += 1 ) {
        final Band row = new Band();
        row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
        row.setName( "r-" + r );

        for ( int cellNumber = 0; cellNumber < columns; cellNumber++ ) {
          final Band cell = producer.createCell( r, cellNumber );
          if ( cell == null ) {
            continue;
          }

          cell.setName( "hr-" + r + "-" + cellNumber );
          final Element dataItem = producer.createDataItem( "Head-" + r + "-" + cellNumber, r, cellNumber );
          if ( dataItem != null ) {
            cell.addElement( dataItem );
          }
          row.addElement( cell );
        }
        tableHeader.addElement( row );
      }
      table.addElement( tableHeader );
    }

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    for ( int r = 0; r < dataRows; r += 1 ) {
      final Band row = new Band();
      row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
      row.setName( "r-" + ( r + headerRows ) );

      for ( int cellNumber = 0; cellNumber < columns; cellNumber++ ) {
        final Band cell = producer.createCell( r + headerRows, cellNumber );
        if ( cell == null ) {
          continue;
        }
        cell.setName( "dr-" + r + "-" + cellNumber );

        Element dataItem = producer.createDataItem( "Data-" + r + "-" + cellNumber, r + headerRows, cellNumber );
        if ( dataItem != null ) {
          cell.addElement( dataItem );
        }
        row.addElement( cell );
      }
      tableBody.addElement( row );
    }
    table.addElement( tableBody );
    return table;
  }

  public static Band createCell( final int rowSpan, final int colSpan ) {
    return createCell( 150, 20, rowSpan, colSpan );
  }

  public static Band createCell( final float width, final float height, final int rowSpan, final int colSpan ) {
    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, rowSpan );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, colSpan );
    return tableCell;
  }
}
