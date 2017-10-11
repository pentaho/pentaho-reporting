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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumn;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnGroup;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Another static processing step which validates the table structure and computes the cell positions within the table.
 *
 * @author Thomas Morgner
 */
public class TableValidationStep extends IterateStructuralProcessStep {
  private static final long MAX_AUTO = StrictGeomUtility.toInternalValue( 0x80000000000L );

  private static class TableInfoStructure {
    private TableRenderBox table;
    private TableInfoStructure parent;
    private TableColumnModel columnModel;
    private TableSectionRenderBox sectionRenderBox;
    private IntList rowSpans;
    private TableRowModel rowModel;
    protected int tableCellPosition;
    protected int rowIndex;
    private boolean bodySection;
    private boolean headerOrFooterSection;

    public TableInfoStructure( final TableRenderBox table, final TableInfoStructure parent ) {
      this.table = table;
      this.parent = parent;
      this.columnModel = table.getColumnModel();
      this.rowSpans = new IntList( 10 );
    }

    public void resetCellPosition() {
      this.tableCellPosition = 0;
    }

    private boolean isCellAreaClear( final int pos, final int colSpan ) {
      final int maxIdx = Math.min( pos + colSpan, rowSpans.size() );
      for ( int i = pos; i < maxIdx; i++ ) {
        if ( rowSpans.get( tableCellPosition ) > 0 ) {
          return false;
        }
      }
      return true;
    }

    public int increaseCellPosition( final int colSpan, final int rowSpan ) {
      // find insert-position for the cell. This skips cells that block the location via a row-span.
      while ( true ) {
        // we are past the point of defined cells. Adding new cells is guaranteed to not have row-spans.
        if ( tableCellPosition >= rowSpans.size() ) {
          break;
        }

        if ( isCellAreaClear( tableCellPosition, colSpan ) ) {
          break;
        }

        tableCellPosition += 1;
      }

      final int retval = tableCellPosition;
      // set the cell...
      for ( int i = tableCellPosition; i < tableCellPosition + colSpan; i++ ) {
        if ( i < rowSpans.size() ) {
          rowSpans.set( i, Math.max( rowSpan, rowSpans.get( i ) ) );
        } else {
          rowSpans.add( rowSpan );
        }
      }

      tableCellPosition += colSpan;
      return retval;
    }

    public TableInfoStructure pop() {
      return parent;
    }

    public TableSectionRenderBox getSectionRenderBox() {
      return sectionRenderBox;
    }

    public void setSectionRenderBox( final TableSectionRenderBox sectionRenderBox ) {
      this.rowSpans.clear();
      this.sectionRenderBox = sectionRenderBox;
      if ( this.sectionRenderBox != null ) {
        this.rowModel = sectionRenderBox.getRowModel();
        this.bodySection = ( sectionRenderBox.getDisplayRole() == TableSectionRenderBox.Role.BODY );
        this.headerOrFooterSection = !bodySection;
      } else {
        this.rowModel = null;
        this.bodySection = false;
      }
      this.rowIndex = -1;
    }

    public boolean isHeaderOrFooterSection() {
      return headerOrFooterSection;
    }

    public void setHeaderOrFooterSection( final boolean headerOrFooterSection ) {
      this.headerOrFooterSection = headerOrFooterSection;
    }

    public boolean isBodySection() {
      return bodySection;
    }

    public TableRenderBox getTable() {
      return table;
    }

    public TableColumnModel getColumnModel() {
      return columnModel;
    }

    public void updateDefinedSize( final int rowSpan, final long preferredSize ) {
      rowModel.updateDefinedSize( rowIndex, rowSpan, preferredSize );
    }
  }

  private TableInfoStructure currentTable;
  private TableColumnGroup currentColumnGroup;

  public TableValidationStep() {
  }

  public void validate( final LogicalPageBox box ) {
    currentTable = null;
    startProcessing( box );
    if ( currentTable != null ) {
      throw new IllegalStateException();
    }
  }

  private boolean abortIfNoTable( final RenderBox box ) {
    if ( box.getTableRefCount() == 0 ) {
      return false;
    }

    if ( box.getTableValidationAge() == box.getChangeTracker() ) {
      return false;
    }

    box.setTableValidationAge( box.getChangeTracker() );
    return true;
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return abortIfNoTable( box );
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return abortIfNoTable( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return abortIfNoTable( box );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return abortIfNoTable( box );
  }

  protected boolean startRowBox( final RenderBox box ) {
    return abortIfNoTable( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    if ( currentTable != null ) {
      if ( box.getParent().getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
        currentTable.setHeaderOrFooterSection( false );
      }
      return true;
    }
    return abortIfNoTable( box );
  }

  protected void finishAutoBox( final RenderBox box ) {
    if ( currentTable != null ) {
      box.setContainsReservedContent( currentTable.isHeaderOrFooterSection() );
    }
  }

  protected boolean startTableBox( final TableRenderBox table ) {
    final long changeTracker = table.getChangeTracker();
    final long age = table.getTableValidationAge();
    if ( changeTracker == age ) {
      return false;
    }

    currentTable = new TableInfoStructure( table, currentTable );
    return true;
  }

  protected void finishTableBox( final TableRenderBox table ) {
    final long changeTracker = table.getChangeTracker();
    final long age = table.getTableValidationAge();
    if ( changeTracker == age ) {
      return;
    }

    // currentTable.columnModel.validatePreferredSizes(table);
    table.setTableValidationAge( age );
    table.setPredefinedColumnsValidated( true );
    currentTable = currentTable.pop();
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    if ( currentTable == null ) {
      return false;
    }

    if ( currentTable.table.isPredefinedColumnsValidated() ) {
      return false;
    }

    currentColumnGroup = new TableColumnGroup( box.getBoxDefinition().getBorder() );
    currentColumnGroup.setColSpan( box.getColSpan() );
    return true;
  }

  protected void processTableColumn( final TableColumnNode node ) {
    if ( currentTable == null ) {
      return;
    }

    if ( currentTable.table.isPredefinedColumnsValidated() ) {
      return;
    }

    final Border border = node.getBoxDefinition().getBorder();
    final RenderLength length = node.getBoxDefinition().getMinimumWidth();

    if ( currentColumnGroup != null ) {
      currentColumnGroup.addColumn( new TableColumn( border, length, false ) );
    } else {
      final TableColumnGroup currentColumnGroup = new TableColumnGroup( BoxDefinition.EMPTY.getBorder() );
      currentColumnGroup.addColumn( new TableColumn( border, length, false ) );
      currentTable.columnModel.addColumnGroup( currentColumnGroup );
    }
  }

  protected void finishTableColumnGroupBox( final TableColumnGroupNode box ) {
    if ( currentTable == null ) {
      return;
    }

    if ( currentTable.table.isPredefinedColumnsValidated() ) {
      return;
    }

    while ( currentColumnGroup.getColumnCount() < box.getColSpan() ) {
      currentColumnGroup.addColumn( new TableColumn( currentColumnGroup.getBorder(), RenderLength.AUTO, false ) );
    }

    currentTable.columnModel.addColumnGroup( currentColumnGroup );
    currentColumnGroup = null;
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    if ( currentTable == null ) {
      return false;
    }
    if ( currentTable.getSectionRenderBox() != null ) {
      return true;
    }

    currentTable.setSectionRenderBox( box );
    box.setContainsReservedContent( box.getDisplayRole() != TableSectionRenderBox.Role.BODY );
    box.getRowModel().initialize( currentTable.getTable() );
    return true;
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    if ( currentTable == null ) {
      return;
    }
    if ( currentTable.getSectionRenderBox() != box ) {
      return;
    }

    final IntList rowSpans = currentTable.rowSpans;
    int missingRows = 0;
    for ( int i = 0; i < rowSpans.size(); i++ ) {
      final int value = rowSpans.get( i );
      if ( missingRows < value ) {
        missingRows = value;
      }
    }

    for ( int i = 0; i < missingRows; i += 1 ) {
      currentTable.rowModel.addRow();
    }

    box.getRowModel().validatePreferredSizes();
    currentTable.setSectionRenderBox( null );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    if ( currentTable == null ) {
      return false;
    }
    if ( currentTable.getSectionRenderBox() == null ) {
      return false;
    }

    currentTable.resetCellPosition();
    box.setBodySection( currentTable.isBodySection() );

    // check if this is the first row ...
    if ( currentTable.rowIndex == -1 ) {
      if ( box.getRowIndex() != -1 ) {
        currentTable.rowIndex = box.getRowIndex();
        return true;
      }
    }

    currentTable.rowIndex += 1;
    box.setRowIndex( currentTable.rowIndex );
    if ( currentTable.rowModel.getRowCount() <= currentTable.rowIndex ) {
      currentTable.rowModel.addRow();
    }
    return true;
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    if ( currentTable == null ) {
      return;
    }
    if ( currentTable.getSectionRenderBox() == null ) {
      return;
    }

    final IntList rowSpans = currentTable.rowSpans;
    int maxRowSpan = 0;
    for ( int i = 0; i < rowSpans.size(); i++ ) {
      final int value = rowSpans.get( i );
      maxRowSpan = Math.max( maxRowSpan, value );
    }

    for ( int i = 0; i < rowSpans.size(); i++ ) {
      final int value = rowSpans.get( i );
      rowSpans.set( i, Math.max( 0, value - 1 ) );
    }
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    if ( currentTable == null ) {
      return false;
    }
    if ( currentTable.getSectionRenderBox() == null ) {
      return false;
    }
    final int rowSpan = box.getRowSpan();
    final int colSpan = box.getColSpan();

    final int startPos = currentTable.increaseCellPosition( colSpan, rowSpan );
    while ( currentTable.columnModel.getColumnCount() < ( startPos + colSpan ) ) {
      currentTable.columnModel.addAutoColumn();
    }

    box.setColumnIndex( startPos );
    box.setBodySection( currentTable.isBodySection() );

    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final long preferredHeight = boxDefinition.getPreferredHeight().resolve( 0 );
    final long minHeight = boxDefinition.getMinimumHeight().resolve( 0 );
    final long maxHeight = boxDefinition.getMaximumHeight().resolve( 0, MAX_AUTO );

    final long preferredSize = ProcessUtility.computeLength( minHeight, maxHeight, preferredHeight );
    currentTable.updateDefinedSize( rowSpan, preferredSize );
    return true;
  }

}
