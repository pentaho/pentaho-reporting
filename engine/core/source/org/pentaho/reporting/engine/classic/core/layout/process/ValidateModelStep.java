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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
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
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;

public class ValidateModelStep extends IterateStructuralProcessStep
{
  private static class TableValidationInfo
  {
    private boolean needCheck;
    private boolean seenBody;
    private int seenRow;
    private boolean inMainBody;
    private TableValidationInfo parent;

    private TableValidationInfo(final TableValidationInfo parent)
    {
      this.parent = parent;
    }

    public TableValidationInfo pop()
    {
      return parent;
    }

    public boolean isInMainBody()
    {
      return inMainBody;
    }

    public void setInMainBody(final boolean inMainBody)
    {
      this.inMainBody = inMainBody;
    }

    public boolean isNeedCheck()
    {
      return needCheck;
    }

    public void setNeedCheck(final boolean needCheck)
    {
      this.needCheck = needCheck;
    }

    public boolean isSeenBody()
    {
      return seenBody;
    }

    public void setSeenBody(final boolean seenBody)
    {
      this.seenBody = seenBody;
    }

    public boolean isSeenRow()
    {
      return seenRow > 0;
    }

    public void addSeenRow()
    {
      this.seenRow += 1;
    }

    public String toString()
    {
      final StringBuilder sb = new StringBuilder();
      sb.append("TableValidationInfo");
      sb.append("{needCheck=").append(needCheck);
      sb.append(", seenBody=").append(seenBody);
      sb.append(", seenRow=").append(seenRow);
      sb.append(", inMainBody=").append(inMainBody);
      sb.append(", parent=").append(parent);
      sb.append('}');
      return sb.toString();
    }
  }

  private static final Log logger = LogFactory.getLog(ValidateModelStep.class);

  private boolean result;
  private TableValidationInfo validationInfo;

  public ValidateModelStep()
  {
  }

  public boolean isLayoutable(final LogicalPageBox root)
  {
    validationInfo = null;
    result = true;
    // do not validate the header or footer or watermark sections..
    processBoxChilds(root);
    logger.debug("Validation result: " + result);
    return result;
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isValidateModelCacheValid())
    {
      result &= box.isValidateModelResult();
      return false;
    }

    if (box.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Canvas: Box is open: " + box);
      }
      result = false;
      return false;
    }

    if (box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0)
    {
      return false;
    }

    return true;
  }

  protected void finishCanvasBox(final CanvasRenderBox box)
  {
    box.setValidateModelResult(result);
  }

  protected boolean validateBlockOrAutoBox(final RenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isValidateModelCacheValid())
    {
      result &= box.isValidateModelResult();
      return false;
    }

    if (box.isOpen())
    {
      if (box.getNext() != null)
      {
        // if this box is not the last box in a sequence of boxes, then we cannot finish the layouting
        if (logger.isDebugEnabled())
        {
          logger.debug("Block: Box is open with next element pending : " + box);
        }
        result = false;
        return false;
      }
      else if (box.getStaticBoxLayoutProperties().isPlaceholderBox())
      {
        if (box.getFirstChild() == null)
        {
          if (logger.isDebugEnabled())
          {
            logger.debug("Block: Open Box is placeholder : " + box);
          }
          result = false;
          return false;
        }
      }
      else if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("Block: Paragraph is open: " + box);
        }
        result = false;
        return false;
      }
    }
    else if (box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0)
    {
      return false;
    }

    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return validateBlockOrAutoBox(box);
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    box.setValidateModelResult(result);
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      return validateBlockOrAutoBox(box);
    }
    if ((box.getLayoutNodeType() & LayoutNodeTypes.TYPE_BOX_TABLE) == LayoutNodeTypes.TYPE_BOX_TABLE ||
        (box.getLayoutNodeType() & LayoutNodeTypes.TYPE_BOX_TABLE_SECTION) == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      return true;
    }

    // todo: Handle tables properly
    return validateInlineRowOrTableCellBox(box);
  }

  protected void finishAutoBox(final RenderBox box)
  {
    box.setValidateModelResult(result);
  }

  private boolean validateInlineRowOrTableCellBox(final RenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isValidateModelCacheValid())
    {
      result &= box.isValidateModelResult();
      return false;
    }

    if (box.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Inline: Box is open : " + box);
      }
      result = false;
      return false;
    }
    if (box.getAppliedContentRefCount() == 0 && box.getTableRefCount() == 0)
    {
      return false;
    }
    return true;
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return validateInlineRowOrTableCellBox(box);
  }

  protected void finishTableCellBox(final TableCellRenderBox box)
  {
    box.setValidateModelResult(result);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return validateInlineRowOrTableCellBox(box);
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    box.setValidateModelResult(result);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return validateInlineRowOrTableCellBox(box);
  }

  protected void finishRowBox(final RenderBox box)
  {
    box.setValidateModelResult(result);
  }

  protected boolean startTableBox(final TableRenderBox table)
  {
    validationInfo = new TableValidationInfo(this.validationInfo);

    if (result == false)
    {
      return false;
    }

    if (table.isValidateModelCacheValid())
    {
      result &= table.isValidateModelResult();
      return true;
    }

    if (table.isOpen())
    {
      if (table.isAutoLayout())
      {
        // Auto-Layout means, we have to see the complete table.
        // Yes, that is expensive ..
        if (logger.isDebugEnabled())
        {
          logger.debug("Table: Open Table and AutoLayout: " + table);
        }

        result = false;
        return false;
      }
      else if (table.getColumnModel().isIncrementalModeSupported() == false)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("Table: Open Table and incremental mode not supported: " + table);
        }
        result = false;
        return false;
      }
    }

    validationInfo.setNeedCheck(true);
    return true;
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    if (box.getDisplayRole() == TableSectionRenderBox.Role.BODY)
    {
      validationInfo.setInMainBody(true);
    }

    validationInfo.setSeenBody(true);
    return true;
  }

  protected void finishTableSectionBox(final TableSectionRenderBox box)
  {
    validationInfo.setInMainBody(false);
  }

  protected void finishTableBox(final TableRenderBox table)
  {
    try
    {
      if (table.isValidateModelCacheValid())
      {
        return;
      }

      table.setValidateModelResult(result);

      if (validationInfo.isNeedCheck() == false)
      {
        return;
      }

      if (validationInfo.isSeenBody() && validationInfo.isSeenRow())
      {
        return;
      }

      if (logger.isDebugEnabled())
      {
        logger.debug("Table-Box: " + validationInfo);
      }
      result = false;
    }
    finally
    {
      this.validationInfo = validationInfo.pop();
    }
  }

  protected boolean startTableRowBox(final TableRowRenderBox row)
  {
    if (row.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Table-Row: Box is open.");
      }
      result = false;
      return false;
    }

    validationInfo.addSeenRow();
    return true;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return result;
  }


  protected boolean isResult()
  {
    return result;
  }

  protected void setResult(final boolean result)
  {
    this.result = result;
  }
}
