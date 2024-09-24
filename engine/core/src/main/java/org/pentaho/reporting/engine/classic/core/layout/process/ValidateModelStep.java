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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ValidationResult;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;

@SuppressWarnings( "HardCodedStringLiteral" )
public class ValidateModelStep extends IterateStructuralProcessStep {
  private static class TableValidationInfo {
    private boolean needCheck;
    private boolean seenBody;
    private int seenRow;
    private int requiredAdditionalRows;
    private int currentRowMaxRowSpan;
    private boolean inMainBody;
    private boolean seenRowInMainBody;
    private TableValidationInfo parent;

    private TableValidationInfo( final TableValidationInfo parent ) {
      this.parent = parent;
      this.requiredAdditionalRows = 0;
    }

    public TableValidationInfo pop() {
      return parent;
    }

    public void setInMainBody( final boolean inMainBody ) {
      this.requiredAdditionalRows = 0;
      this.inMainBody = inMainBody;
    }

    public boolean isRequireAdditionalRows() {
      return requiredAdditionalRows > 0;
    }

    public boolean isNeedCheck() {
      return needCheck;
    }

    public void setNeedCheck( final boolean needCheck ) {
      this.needCheck = needCheck;
    }

    public boolean isSeenTableBody() {
      return seenBody;
    }

    public void startTableSection( final boolean seenBody ) {
      this.requiredAdditionalRows = 0;
      this.seenBody = seenBody;
    }

    public boolean isSeenRowInMainBody() {
      return seenRowInMainBody;
    }

    public void addSeenRow() {
      if ( inMainBody ) {
        seenRowInMainBody = true;
      }

      this.seenRow += 1;
      this.currentRowMaxRowSpan = 0;
      if ( this.requiredAdditionalRows > 0 ) {
        // if we have spanned rows pending, reduce the span with each new row started, until every row is consumed.
        this.requiredAdditionalRows -= 1;
      }
    }

    public void rowFinished() {
      this.requiredAdditionalRows += this.currentRowMaxRowSpan;
      this.requiredAdditionalRows -= 1;
    }

    public void seenTableCell( final int rowSpan ) {
      this.currentRowMaxRowSpan = Math.max( rowSpan, this.currentRowMaxRowSpan );
    }

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "TableValidationInfo" );
      sb.append( "{needCheck=" ).append( needCheck );
      sb.append( ", seenBody=" ).append( seenBody );
      sb.append( ", seenRow=" ).append( seenRow );
      sb.append( ", inMainBody=" ).append( inMainBody );
      sb.append( ", parent=" ).append( parent );
      sb.append( '}' );
      return sb.toString();
    }

  }

  private static final Log logger = LogFactory.getLog( ValidateModelStep.class );

  private ValidationResult result;
  private TableValidationInfo validationInfo;

  public ValidateModelStep() {
  }

  public boolean isLayoutable( final LogicalPageBox root ) {
    validationInfo = null;
    setResult( ValidationResult.OK );
    // do not validate the header or footer or watermark sections..
    processBoxChilds( root );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "Validation result: " + getResult() );
    }
    return getResult() == ValidationResult.OK;
  }

  public void setResult( final ValidationResult result ) {
    this.result = result;
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return false;
    }

    if ( box.isValidateModelCacheValid() ) {
      setResult( box.isValidateModelResult() );
      return false;
    }

    if ( box.isOpen() ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Canvas: Box is open: " + box );
      }
      setResult( ValidationResult.CANVAS_BOX_OPEN );
      box.setValidateModelResult( getResult() );
      return false;
    }

    if ( box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0 ) {
      return false;
    }

    return true;
  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  protected boolean validateBlockOrAutoBox( final RenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return false;
    }

    if ( box.isValidateModelCacheValid() ) {
      setResult( box.isValidateModelResult() );
      return false;
    }

    if ( box.isOpen() ) {
      if ( box.getNext() != null ) {
        // if this box is not the last box in a sequence of boxes, then we cannot finish the layouting
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Block: Box is open with next element pending : " + box );
        }
        setResult( ValidationResult.BOX_OPEN_NEXT_PENDING );
        box.setValidateModelResult( getResult() );
        return false;
      } else if ( box.getStaticBoxLayoutProperties().isPlaceholderBox() ) {
        if ( box.getFirstChild() == null ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Block: Open Box is placeholder : " + box );
          }
          setResult( ValidationResult.PLACEHOLDER_BOX_OPEN );
          box.setValidateModelResult( getResult() );
          return false;
        }
      } else if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Block: Paragraph is open: " + box );
        }
        setResult( ValidationResult.PARAGRAPH_BOX_OPEN );
        box.setValidateModelResult( getResult() );
        return false;
      }
    } else if ( box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0 ) {
      return false;
    }

    // pending complex content, must validate childs, but in itself it is not a indicator for invalid content.
    return true;
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return validateBlockOrAutoBox( box );
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    if ( ( box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      return validateBlockOrAutoBox( box );
    }

    if ( ( box.getLayoutNodeType() & LayoutNodeTypes.TYPE_BOX_TABLE ) == LayoutNodeTypes.TYPE_BOX_TABLE
        || ( box.getLayoutNodeType() & LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return true;
    }

    return validateInlineRowOrTableCellBox( box );
  }

  protected void finishAutoBox( final RenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  private boolean validateInlineRowOrTableCellBox( final RenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return false;
    }

    if ( box.isValidateModelCacheValid() ) {
      setResult( box.isValidateModelResult() );
      return false;
    }

    if ( box.isOpen() ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Inline: Box is open : " + box );
      }
      setResult( ValidationResult.CELL_BOX_OPEN );
      box.setValidateModelResult( getResult() );
      return false;
    }
    if ( box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0 ) {
      return false;
    }
    return true;
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    int rowSpan = box.getRowSpan();
    validationInfo.seenTableCell( rowSpan );
    return validateInlineRowOrTableCellBox( box );
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return validateInlineRowOrTableCellBox( box );
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  protected boolean startRowBox( final RenderBox box ) {
    return validateInlineRowOrTableCellBox( box );
  }

  protected void finishRowBox( final RenderBox box ) {
    if ( getResult() != ValidationResult.OK ) {
      return;
    }
    box.setValidateModelResult( getResult() );
  }

  protected boolean startTableBox( final TableRenderBox table ) {
    validationInfo = new TableValidationInfo( this.validationInfo );

    if ( getResult() != ValidationResult.OK ) {
      return false;
    }

    if ( table.isValidateModelCacheValid() ) {
      setResult( table.isValidateModelResult() );
      return true;
    }

    if ( table.isOpen() ) {
      if ( table.isAutoLayout() ) {
        // Auto-Layout means, we have to see the complete table.
        // Yes, that is expensive ..
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Table: Open Table and AutoLayout: " + table );
        }

        setResult( ValidationResult.TABLE_BOX_OPEN );
        table.setValidateModelResult( getResult() );
        return false;
      } else if ( table.getColumnModel().isIncrementalModeSupported() == false ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Table: Open Table and incremental mode not supported: " + table );
        }
        setResult( ValidationResult.TABLE_BOX_OPEN );
        table.setValidateModelResult( getResult() );
        return false;
      } else if ( table.isPreventPagination() ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Table: Open Table and incremental mode not supported: " + table );
        }
        setResult( ValidationResult.TABLE_BOX_PREVENTS_PAGINATION );
        table.setValidateModelResult( getResult() );
        return false;
      }
    }

    validationInfo.setNeedCheck( true );
    return true;
  }

  protected void finishTableBox( final TableRenderBox table ) {
    try {
      if ( table.isValidateModelCacheValid() ) {
        return;
      }

      if ( getResult() != ValidationResult.OK ) {
        return;
      }

      if ( validationInfo.isNeedCheck() == false ) {
        return;
      }

      if ( table.isOpen() && validationInfo.isSeenTableBody() == false && validationInfo.isSeenRowInMainBody() == false ) {
        // A table that is open, cannot be processed until it has at least a body and one real row of data in it.
        setResult( ValidationResult.TABLE_BOX_MISSING_DATA );
        table.setValidateModelResult( getResult() );
        return;
      }

      if ( logger.isDebugEnabled() ) {
        logger.debug( "Table-Box: " + validationInfo );
      }

      table.setValidateModelResult( getResult() );
    } finally {
      this.validationInfo = validationInfo.pop();
    }
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    if ( box.getDisplayRole() == TableSectionRenderBox.Role.BODY ) {
      validationInfo.setInMainBody( true );
    }

    validationInfo.startTableSection( true );
    return true;
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    if ( box.isOpen() && validationInfo.isRequireAdditionalRows() ) {
      setResult( ValidationResult.TABLE_BODY_MISSING_ROWS );
      box.setValidateModelResult( getResult() );
    }
    validationInfo.setInMainBody( false );
  }

  protected boolean startTableRowBox( final TableRowRenderBox row ) {
    if ( row.isOpen() ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Table-Row: Box is open." );
      }
      setResult( ValidationResult.TABLE_ROW_OPEN );
      row.setValidateModelResult( getResult() );
      return false;
    }

    validationInfo.addSeenRow();
    return true;
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    validationInfo.rowFinished();
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return ( getResult() == ValidationResult.OK );
  }

  protected ValidationResult getResult() {
    return result;
  }
}
