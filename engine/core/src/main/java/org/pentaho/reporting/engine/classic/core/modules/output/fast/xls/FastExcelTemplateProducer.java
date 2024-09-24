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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.CellLayoutInfo;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.TemplatingOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;
import java.util.HashMap;

public class FastExcelTemplateProducer implements FastExportTemplateProducer {
  private final OutputProcessorMetaData metaData;
  private final SheetLayout sheetLayout;
  private final FastExcelPrinter excelPrinter;
  private final CellBackgroundProducer cellBackgroundProducer;
  private final HashMap<InstanceID, CellLayoutInfo> layout;
  private final ArrayList<CellLayoutInfo> backgroundCells;
  private long[] cellHeights;

  public FastExcelTemplateProducer( final OutputProcessorMetaData metaData, final SheetLayout sheetLayout,
      final FastExcelPrinter excelPrinter ) {
    this.metaData = metaData;
    this.sheetLayout = sheetLayout;
    this.excelPrinter = excelPrinter;
    this.layout = new HashMap<InstanceID, CellLayoutInfo>();
    this.backgroundCells = new ArrayList<CellLayoutInfo>();
    this.cellBackgroundProducer =
        new CellBackgroundProducer( metaData
            .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ), metaData
            .isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) );
  }

  public FormattedDataBuilder createDataBuilder() {
    return new FastExcelFormattedDataBuilder( layout, backgroundCells, cellHeights, excelPrinter );
  }

  public void produceTemplate( final LogicalPageBox pageBox ) {
    TableContentProducer contentProducer =
        TemplatingOutputProcessor.produceTableLayout( pageBox, sheetLayout, metaData );
    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int columnCount = contentProducer.getColumnCount();
    final int startRow = contentProducer.getFinishedRows();

    final int finishRow = contentProducer.getFilledRows();

    cellHeights = new long[finishRow - startRow];

    for ( int row = startRow; row < finishRow; row++ ) {
      cellHeights[row - startRow] = sheetLayout.getRowHeight( row );

      for ( short col = 0; col < columnCount; col++ ) {
        final CellMarker.SectionType sectionType = contentProducer.getSectionType( row, col );
        final RenderBox content = contentProducer.getContent( row, col );
        if ( content == null ) {
          final RenderBox backgroundBox = contentProducer.getBackground( row, col );
          final CellBackground background;
          if ( backgroundBox != null ) {
            background =
                cellBackgroundProducer.getBackgroundForBox( pageBox, sheetLayout, col, row, 1, 1, true, sectionType,
                    backgroundBox );
          } else {
            background = cellBackgroundProducer.getBackgroundAt( pageBox, sheetLayout, col, row, true, sectionType );
          }
          if ( background != null ) {
            backgroundCells.add( new CellLayoutInfo( col, row, background ) );
          }
          continue;
        }

        if ( content.isCommited() == false ) {
          throw new InvalidReportStateException( "Uncommited content encountered" );
        }

        final long contentOffset = contentProducer.getContentOffset( row, col );
        final TableRectangle rect =
            sheetLayout.getTableBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content
                .getHeight(), null );
        if ( rect.isOrigin( col, row ) == false ) {
          // A spanned cell ..
          continue;
        }

        final CellBackground bg =
            cellBackgroundProducer.getBackgroundForBox( pageBox, sheetLayout, rect.getX1(), rect.getY1(), rect
                .getColumnSpan(), rect.getRowSpan(), false, sectionType, content );
        layout.put( content.getInstanceId(), new CellLayoutInfo( rect, bg ) );
        content.setFinishedTable( true );
      }
    }
  }
}
