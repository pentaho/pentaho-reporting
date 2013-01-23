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

@SuppressWarnings("HardCodedStringLiteral")
public class ModelPrinter
{
  public static final ModelPrinter INSTANCE = new ModelPrinter();

  private static final Log logger = LogFactory.getLog(ModelPrinter.class);
  private static final boolean PRINT_LINEBOX_CONTENTS = false;

  public ModelPrinter()
  {
  }

  protected void print (final String s)
  {
    logger.debug(s);
  }

  public void printParents(RenderNode box)
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
      print(b.toString());
      box = box.getParent();
    }
  }

  public void print(final RenderBox box)
  {
    printBox(box, 0);
  }

  public void printBox(final RenderBox box, final int level)
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
    print(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- layout x=");
    b.append(box.getX());
    b.append(", y=");
    b.append(box.getY());
    b.append(", width=");
    b.append(box.getWidth());
    b.append(", height=");
    b.append(box.getHeight());
    b.append(", min-chunk-width=");
    b.append(box.getMinimumChunkWidth());
    b.append(", y2=");
    b.append(box.getY() + box.getHeight());
    print(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- cached-layout cached-x=");
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
    print(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- staticBoxLayoutProperties=");
    b.append(box.getStaticBoxLayoutProperties());
    print(b.toString());
    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- breakContext=");
    b.append(box.getBreakContext());
    print(b.toString());

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
      print(b.toString());

      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- PageBreaks={");
      b.append(pageBox.getAllVerticalBreaks());
      b.append('}');
      print(b.toString());
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
      print(b.toString());
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
      print(b.toString());
    }

    if (box.isOpen())
    {
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- WARNING: THIS BOX IS STILL OPEN");
      print(b.toString());
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
      print(b.toString());
    }
    if (box.isCommited())
    {
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- INFO: THIS BOX IS COMMITED");
      print(b.toString());
    }

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    print(b.toString());

    if (box instanceof ParagraphRenderBox)
    {
      if (PRINT_LINEBOX_CONTENTS)
      {
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        print("----------------  START PARAGRAPH POOL CONTAINER -------------------------------------");
        printBox(paraBox.getPool(), level + 1);
        print("---------------- FINISH PARAGRAPH POOL CONTAINER -------------------------------------");

        if (paraBox.isComplexParagraph())
        {
          print("----------------  START PARAGRAPH LINEBOX CONTAINER -------------------------------------");
          printBox(paraBox.getLineboxContainer(), level + 1);
          print("---------------- FINISH PARAGRAPH LINEBOX CONTAINER -------------------------------------");
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

  private void printChilds(final RenderBox box, final int level)
  {
    RenderNode childs = box.getFirstChild();
    while (childs != null)
    {
      if (childs instanceof RenderBox)
      {
        printBox((RenderBox) childs, level + 1);
      }
      else
      {
        printNode(childs, level + 1);
      }
      childs = childs.getNext();
    }
  }

  private void printNode(final RenderNode node, final int level)
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
    print(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- layout x=");
    b.append(node.getX());
    b.append(", y=");
    b.append(node.getY());
    b.append(", width=");
    b.append(node.getWidth());
    b.append(", height=");
    b.append(node.getHeight());
    b.append(", min-chunk-width=");
    b.append(node.getMinimumChunkWidth());
    b.append(", y2=");
    b.append(node.getY() + node.getHeight());
    print(b.toString());

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- cached-layout cached-x=");
    b.append(node.getCachedX());
    b.append(", cached-y=");
    b.append(node.getCachedY());
    b.append(", cached-width=");
    b.append(node.getCachedWidth());
    b.append(", cached-height=");
    b.append(node.getCachedHeight());

    if (node instanceof FinishedRenderNode)
    {
      final FinishedRenderNode fn = (FinishedRenderNode) node;
      b.append(", layouted-width=");
      b.append(fn.getLayoutedWidth());
      b.append(", layouted-height=");
      b.append(fn.getLayoutedHeight());
    }
    b.append('}');
    print(b.toString());

    if (node instanceof RenderableText)
    {
      final RenderableText text = (RenderableText) node;
      b = new StringBuffer();
      for (int i = 0; i < level; i++)
      {
        b.append("   ");
      }
      b.append("- text='");
      b.append(text.getRawText());
      b.append("'");
      print(b.toString());
    }

    b = new StringBuffer();
    for (int i = 0; i < level; i++)
    {
      b.append("   ");
    }
    b.append("- nodeLayoutProperties=");
    b.append(node.getNodeLayoutProperties());
    print(b.toString());
    print(" ");
  }

  public static RenderBox getRoot (RenderNode node)
  {
    RenderBox parent = node.getParent();
    RenderBox retval = node.getParent();
    while (parent != null)
    {
      retval = parent;
      parent = parent.getParent();
    }
    return retval;
  }
}
