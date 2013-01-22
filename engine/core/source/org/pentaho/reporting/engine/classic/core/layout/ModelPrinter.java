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

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;

/**
 * Creation-Date: Jan 9, 2007, 2:22:59 PM
 *
 * @author Thomas Morgner
 */
public class ModelPrinter
{
  private static final Log logger = LogFactory.getLog(ModelPrinter.class);
  private static final boolean PRINT_LINEBOX_CONTENTS = false;

  private ModelPrinter()
  {
  }

  public static void printParents(RenderNode box)
  {
    while (box != null)
    {
      final StringBuffer b = new StringBuffer();

      b.append(box.getClass().getName());
      b.append('[');
      b.append(box.getElementType().getClass().getName());
      //b.append(Integer.toHexString(System.identityHashCode(box)));
      b.append(';');
      b.append(box.getName());
      b.append(']');
      b.append("={stateKey=");
      b.append(box.getStateKey());
      b.append(", x=");
      b.append(box.getX());
      b.append(", y=");
      b.append(box.getY());
      b.append(", width=");
      b.append(box.getWidth());
      b.append(", height=");
      b.append(box.getHeight());
      b.append(", min-chunk-width=");
      b.append(box.getMinimumChunkWidth());
      b.append(", computed-width=");
      b.append(box.getComputedWidth());
      b.append(", cached-x=");
      b.append(box.getCachedX());
      b.append(", cached-y=");
      b.append(box.getCachedY());
      b.append(", cached-width=");
      b.append(box.getCachedWidth());
      b.append(", cached-height=");
      b.append(box.getCachedHeight());
      b.append('}');
      logger.debug(b);
      box = box.getParent();
    }
  }

  public static void print(final RenderBox box)
  {
    printBox(box, 0);
  }

  public static void printBox(final RenderBox box, final int level)
  {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append(box.getClass().getName());
    b.append('[');
    b.append(box.getElementType().getClass().getName());
    //b.append(Integer.toHexString(System.identityHashCode(box)));
    b.append(';');
    b.append(box.getName());
    b.append(']');
    b.append("={stateKey=");
    b.append(box.getStateKey());
    b.append(", pinned=");
    b.append(box.getPinned());
    b.append('}');
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("-layout x=");
    b.append(box.getX());
    b.append(", y=");
    b.append(box.getY());
    b.append(", width=");
    b.append(box.getWidth());
    b.append(", height=");
    b.append(box.getHeight());
    b.append(", min-chunk-width=");
    b.append(box.getMinimumChunkWidth());
    b.append(", computed-width=");
    b.append(box.getComputedWidth());
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("-cached-layout cached-x=");
    b.append(box.getCachedX());
    b.append(", cached-y=");
    b.append(box.getCachedY());
    b.append(", cached-width=");
    b.append(box.getCachedWidth());
    b.append(", cached-height=");
    b.append(box.getCachedHeight());
    b.append(", content-area-x1=");
    b.append(box.getContentAreaX1());
    b.append(", content-area-x2=");
    b.append(box.getContentAreaX2());

    logger.debug(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- boxDefinition=");
    b.append(box.getBoxDefinition());
    logger.debug(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- nodeLayoutProperties=");
    b.append(box.getNodeLayoutProperties());
    logger.debug(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- staticBoxLayoutProperties=");
    b.append(box.getStaticBoxLayoutProperties());
    logger.debug(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- breakContext=");
    b.append(box.getBreakContext());
    logger.debug(b.toString());

    if (box instanceof LogicalPageBox)
    {
      final LogicalPageBox pageBox = (LogicalPageBox) box;
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- PageBox={PageOffset=");
      b.append(pageBox.getPageOffset());
      b.append(", PageHeight=");
      b.append(pageBox.getPageHeight());
      b.append(", PageEnd=");
      b.append(pageBox.getPageEnd());
      b.append(", PageWidth=");
      b.append(pageBox.getPageWidth());
      b.append('}');
      logger.debug(b.toString());

      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- PageBreaks={");
      b.append(pageBox.getAllVerticalBreaks());
      b.append('}');
      logger.debug(b.toString());
    }

    if (box instanceof TableSectionRenderBox)
    {
      final TableSectionRenderBox pageBox = (TableSectionRenderBox) box;
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- Role: ");
      b.append(pageBox.getDisplayRole());
      logger.debug(b.toString());
    }

    if (box instanceof TableCellRenderBox)
    {
      final TableCellRenderBox pageBox = (TableCellRenderBox) box;
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- Column-Index=");
      b.append(pageBox.getColumnIndex());
      b.append(", ColSpan=");
      b.append(pageBox.getColSpan());
      b.append(", RowSpan=");
      b.append(pageBox.getRowSpan());
      logger.debug(b.toString());
    }

    if (box.isOpen())
    {
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- WARNING: THIS BOX IS STILL OPEN");
      logger.debug(b.toString());
    }

    if (box.isFinishedTable() || box.isFinishedPaginate())
    {
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- INFO: THIS BOX IS FINISHED: ");
      if (box.isFinishedTable())
      {
        b.append("- TABLE ");
      }
      if (box.isFinishedPaginate())
      {
        b.append("- PAGE ");
      }
      logger.debug(b.toString());
    }
    if (box.isCommited())
    {
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- INFO: THIS BOX IS COMMITED");
      logger.debug(b.toString());
    }

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    logger.debug(b.toString());

    if (box instanceof ParagraphRenderBox)
    {
      if (PRINT_LINEBOX_CONTENTS)
      {
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        logger.debug("----------------  START PARAGRAPH POOL CONTAINER -------------------------------------");
        printBox(paraBox.getPool(), level + 1);
        logger.debug("---------------- FINISH PARAGRAPH POOL CONTAINER -------------------------------------");

        if (paraBox.isComplexParagraph())
        {
          logger.debug("----------------  START PARAGRAPH LINEBOX CONTAINER -------------------------------------");
          printBox(paraBox.getLineboxContainer(), level + 1);
          logger.debug("---------------- FINISH PARAGRAPH LINEBOX CONTAINER -------------------------------------");
        }
      }
    }

    if (box instanceof LogicalPageBox)
    {
      final LogicalPageBox lbox = (LogicalPageBox) box;
      printBox(lbox.getHeaderArea(), level + 1);
      printBox(lbox.getWatermarkArea(), level + 1);
    }
    printChilds(box, level);
    if (box instanceof LogicalPageBox)
    {
      final LogicalPageBox lbox = (LogicalPageBox) box;
      printBox(lbox.getRepeatFooterArea(), level + 1);
      printBox(lbox.getFooterArea(), level + 1);
    }
  }

  private static void printChilds(final RenderBox box, final int level)
  {
    RenderNode childs = box.getFirstChild();
    while (childs != null)
    {
      if (childs instanceof RenderBox)
      {
        printBox((RenderBox) childs, level + 1);
      }
      else if (childs instanceof RenderableText)
      {
        printText((RenderableText) childs, level + 1);
      }
      else
      {
        printNode(childs, level + 1);
      }
      childs = childs.getNext();
    }
  }

  private static void printNode(final RenderNode node, final int level)
  {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append(node.getClass().getName());
    b.append('[');
    //b.append(Integer.toHexString(System.identityHashCode(node)));
    b.append(']');
    b.append("={x=");
    b.append(node.getX());
    b.append(", y=");
    b.append(node.getY());
    b.append(", width=");
    b.append(node.getWidth());
    b.append(", height=");
    b.append(node.getHeight());
    b.append(", min-chunk-width=");
    b.append(node.getMinimumChunkWidth());
    b.append(", computed-width=");
    b.append(node.getComputedWidth());

    if (node instanceof FinishedRenderNode)
    {
      final FinishedRenderNode fn = (FinishedRenderNode) node;
      b.append(", layouted-width=");
      b.append(fn.getLayoutedWidth());
      b.append(", layouted-height=");
      b.append(fn.getLayoutedHeight());
    }
    b.append('}');
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- cacheSize={x=");
    b.append(node.getCachedX());
    b.append(", y=");
    b.append(node.getCachedY());
    b.append(", width=");
    b.append(node.getCachedWidth());
    b.append(", height=");
    b.append(node.getCachedHeight());
    b.append('}');
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- nodeLayoutProperties=");
    b.append(node.getNodeLayoutProperties());
    logger.debug(b.toString());
    logger.debug("");
  }

  private static void printText(final RenderableText text, final int level)
  {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("Text");
    b.append('[');
    //b.append(Integer.toHexString(System.identityHashCode(text)));
    b.append(']');
    b.append("={x=");
    b.append(text.getX());
    b.append(", y=");
    b.append(text.getY());
    b.append(", width=");
    b.append(text.getWidth());
    b.append(", height=");
    b.append(text.getHeight());
    b.append(", min-chunk-width=");
    b.append(text.getMinimumChunkWidth());
    b.append(", computed-width=");
    b.append(text.getComputedWidth());
    b.append(", text='");
    b.append(text.getRawText());
    b.append("'}");
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- cacheSize={x=");
    b.append(text.getCachedX());
    b.append(", y=");
    b.append(text.getCachedY());
    b.append(", width=");
    b.append(text.getCachedWidth());
    b.append(", height=");
    b.append(text.getCachedHeight());
    b.append('}');
    logger.debug(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- nodeLayoutProperties=");
    b.append(text.getNodeLayoutProperties());
    logger.debug(b.toString());
  }

}
