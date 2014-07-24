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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.TextCache;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.layout.text.RenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.RotationUtils;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

/**
 * Creation-Date: 31.07.2007, 19:09:52
 *
 * @author Thomas Morgner
 */
public final class RevalidateTextEllipseProcessStep extends IterateStructuralProcessStep
{
  private long contentAreaX1;
  private long contentAreaX2;
  private TextCache textCache;
  private RenderableTextFactory textFactory;
  private CodePointBuffer buffer;
  private String ellipseOverride;
  private int[] bufferArray;

  public RevalidateTextEllipseProcessStep(final OutputProcessorMetaData metaData)
  {
    this.bufferArray = new int[500];
    this.textCache = new TextCache(500);
    this.textFactory = new DefaultRenderableTextFactory(metaData);
  }

  public String getEllipseOverride()
  {
    return ellipseOverride;
  }

  public void setEllipseOverride(final String ellipseOverride)
  {
    this.ellipseOverride = ellipseOverride;
  }

  public void compute(final RenderBox box,
                      final long contentAreaX1,
                      final long contentAreaX2)
  {
    this.contentAreaX1 = contentAreaX1;
    this.contentAreaX2 = contentAreaX2;
    startProcessing(box);
  }

  public long getContentAreaX1()
  {
    return contentAreaX1;
  }

  public long getContentAreaX2()
  {
    return contentAreaX2;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    // compute the box's text-ellipse if necessary.
    if (box.getTextEllipseBox() != null)
    {
      return false;
    }

    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();
    final long boxContentX2 = (box.getX() + box.getWidth() - bdef.getPaddingRight() - sblp.getBorderRight());
    final RenderBox textEllipseBox;
    
    /*
    final boolean hasRotation = RotationUtils.hasRotation( box );
    
    long contentAreaRotation = getContentAreaX2();
    if ( hasRotation && RotationUtils.getRotation( box.getParent() ) != 180 &&
        RotationUtils.getRotation( box.getParent() ) != -180){
      if (RotationUtils.getRotation( box.getParent() ) % 90 == 0 ){
        // vertical
        contentAreaRotation = box.getParent().getOverflowAreaHeight();
      }else{
        contentAreaRotation = (long)( contentAreaRotation * Math.abs( Math.cos( Math.toRadians( RotationUtils.getRotation( box.getParent() ) ) ) ) +
            box.getParent().getOverflowAreaHeight() * Math.abs( Math.sin( Math.toRadians( RotationUtils.getRotation( box.getParent() ) ) ) ) );
      }
    }*/
    
    if (boxContentX2 > getContentAreaX2() )
    { 
      // This is an overflow. Compute the text-ellipse ..
      textEllipseBox = processTextEllipse(box, getContentAreaX2(), RotationUtils.hasRotation( box.getParent() ));
      box.setTextEllipseBox(textEllipseBox);
    }else{
      textEllipseBox = null;
    }
    return true;
  }

  private RenderBox processTextEllipse(final RenderBox box, final long x2, final boolean hasRotation)
  {    
    final StyleSheet style = box.getStyleSheet();
    final String reslit = (String) style.getStyleProperty(TextStyleKeys.RESERVED_LITERAL, ellipseOverride);
    if (reslit == null || "".equals(reslit))
    {
      // oh, no ellipse. Thats nice.
      return null;
    }

    final RenderBox textEllipse = (RenderBox) box.derive(false);
    final ReportAttributeMap map = box.getAttributes();
    final TextCache.Result result = textCache.get
        (style.getId(), style.getChangeTracker(), map.getChangeTracker(), reslit);
    if (result != null)
    {
      textEllipse.addGeneratedChilds(result.getText());
      textEllipse.addGeneratedChilds(result.getFinish());
      performTextEllipseLayout(textEllipse, x2, hasRotation);
      return textEllipse;
    }
    if (buffer != null)
    {
      buffer.setCursor(0);
    }

    buffer = Utf16LE.getInstance().decodeString(reslit, buffer);
    bufferArray = buffer.getBuffer(bufferArray);

    textFactory.startText();
    final RenderNode[] renderNodes = textFactory.createText
        (bufferArray, 0, buffer.getLength(), style, box.getElementType(), box.getInstanceId(), map);
    final RenderNode[] finishNodes = textFactory.finishText();

    textEllipse.addGeneratedChilds(renderNodes);
    textEllipse.addGeneratedChilds(finishNodes);
    textCache.store(style.getId(), style.getChangeTracker(),
        map.getChangeTracker(), reslit, style, map, renderNodes, finishNodes);
    performTextEllipseLayout(textEllipse, x2, hasRotation);
    return textEllipse;
  }

  private void performTextEllipseLayout(final RenderBox box, final long x2, final boolean hasRotation)
  {
    // we do assume that the text-ellipse box is a simple box with no sub-boxes.
    if (box == null)
    {
      return;
    }
    long x = x2;
    RenderNode node = box.getLastChild();
    if(hasRotation){//rotate 90
      long y = x2;
      while (node != null)
      {
        final long nodeWidth = x2;
        node.setX(0);
        node.setHeight(0);
        node.setY(y - nodeWidth);
        node.setHeight(x2);
  
        node = node.getNext();
        y -= nodeWidth;
      }
      box.setX(x);
      box.setWidth(x2 - x);
      box.setContentAreaX1(x);
      box.setContentAreaX2(x2 - x);
    }else{
      while (node != null)
      {
        final long nodeWidth = node.getMaximumBoxWidth();
        node.setX(x - nodeWidth);
        node.setWidth(node.getMaximumBoxWidth());
        node.setY(box.getY());
        node.setHeight(box.getHeight());
  
        node = node.getNext();
        x -= nodeWidth;
      }
      box.setX(x);
      box.setWidth(x2 - x);
      box.setContentAreaX1(x);
      box.setContentAreaX2(x2 - x);
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    startProcessing(box.getEffectiveLineboxContainer());
  }
}
