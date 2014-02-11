/*
 *
 *  * This program is free software; you can redistribute it and/or modify it under the
 *  * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  * Foundation.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License along with this
 *  * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  * or from the Free Software Foundation, Inc.,
 *  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  * See the GNU Lesser General Public License for more details.
 *  *
 *  * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 *
 */

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

public class TextHelper
{
  private static class AttributedStringChunk
  {
    private String text;
    private Map<AttributedCharacterIterator.Attribute, Object> attributes;

    private AttributedStringChunk(final String text,
                                  final Map<AttributedCharacterIterator.Attribute, Object> attributes)
    {
      if (text.length() == 0)
      {
        this.text = "\u0200";
      }
      else
      {
        this.text = text;
      }
      this.attributes = attributes;
    }

    public String getText()
    {
      return text;
    }

    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes()
    {
      return attributes;
    }
  }

  private String text;

  public RenderableComplexText create(RenderBox lineBoxContainer,
                                      int start, int end)
  {
    return new RenderableComplexText
        (lineBoxContainer.getStyleSheet(), lineBoxContainer.getInstanceId(),
            lineBoxContainer.getElementType(), lineBoxContainer.getAttributes(), text.substring(start, end));
  }

  public AttributedString computeText(RenderBox lineBoxContainer)
  {
    List<AttributedStringChunk> attr = new ArrayList<AttributedStringChunk>();
    computeText(lineBoxContainer, attr);
    if (attr.isEmpty()) {
      attr.add(new AttributedStringChunk("", computeStyle(lineBoxContainer.getStyleSheet())));
    }

    StringBuilder text = new StringBuilder();
    for (AttributedStringChunk chunk : attr)
    {
      text.append(chunk.getText());
    }

    this.text = text.toString();

    AttributedString str = new AttributedString(text.toString());
    int startPosition = 0;
    for (AttributedStringChunk chunk : attr)
    {
      int length = chunk.getText().length();
      int endIndex = startPosition + length;
      str.addAttributes(chunk.getAttributes(), startPosition, endIndex);
      startPosition = endIndex;
    }

    Object ws = lineBoxContainer.getStyleSheet().getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE);
    if (WhitespaceCollapse.PRESERVE_BREAKS.equals(ws)) {
      // linebreaks disabled

    }
    else if (WhitespaceCollapse.COLLAPSE.equals(ws)) {
      // normal linebreaks, but duplicate spaces removed
    }
    else if (WhitespaceCollapse.DISCARD.equals(ws)) {
      // all whitespaces removed
    }


    return str;
  }

  public String getText()
  {
    return text;
  }

  private void computeText(RenderBox box, List<AttributedStringChunk> chunks)
  {
    RenderNode node = box.getFirstChild();
    while (node != null)
    {
      if (node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT)
      {
        final RenderableComplexText complexNode = (RenderableComplexText) node;
        chunks.add(new AttributedStringChunk(complexNode.getRawText(), computeStyle(node.getStyleSheet())));
      }
      else if (node instanceof RenderBox)
      {
        computeText((RenderBox) node, chunks);
      }
      node = node.getNext();
    }
  }

  private Map<AttributedCharacterIterator.Attribute, Object> computeStyle(StyleSheet layoutContext)
  {
    Map<AttributedCharacterIterator.Attribute, Object> result = new HashMap<AttributedCharacterIterator.Attribute, Object>();
    // Determine font style
    if (layoutContext.getBooleanStyleProperty(TextStyleKeys.ITALIC))
    {
      result.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    }
    else
    {
      result.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
    }
    if (layoutContext.getBooleanStyleProperty(TextStyleKeys.BOLD))
    {
      result.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    }
    else
    {
      result.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
    }
    result.put(TextAttribute.FAMILY, layoutContext.getStyleProperty(TextStyleKeys.FONT));
    result.put(TextAttribute.SIZE, layoutContext.getIntStyleProperty(TextStyleKeys.FONTSIZE, 12));
    result.put(TextAttribute.UNDERLINE, layoutContext.getBooleanStyleProperty(TextStyleKeys.UNDERLINED));
    result.put(TextAttribute.STRIKETHROUGH, layoutContext.getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH));
    return result;
  }
}
