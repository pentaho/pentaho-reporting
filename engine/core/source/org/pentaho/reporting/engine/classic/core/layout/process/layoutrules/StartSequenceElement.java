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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

/**
 * Represents the opening of an inline element and represents the respective border. There is no break after that
 * element.
 *
 * @author Thomas Morgner
 */
public class StartSequenceElement implements InlineSequenceElement
{
  public static final InlineSequenceElement INSTANCE = new StartSequenceElement();

  private StartSequenceElement()
  {
  }

  /**
   * The width of the element. This is the minimum width of the element.
   *
   * @return
   */
  public long getMinimumWidth(final RenderNode node)
  {
    final InlineRenderBox box = (InlineRenderBox) node;
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();
    return blp.getBorderLeft() + bdef.getPaddingLeft() + blp.getMarginLeft();
  }

  /**
   * The extra-space width for an element. Some elements can expand to fill some more space (justified text is a good
   * example, adding some space between the letters of each word to reduce the inner-word spacing).
   *
   * @return
   */
  public long getMaximumWidth(final RenderNode node)
  {
    return getMinimumWidth(node);
  }


  public boolean isPreserveWhitespace(final RenderNode node)
  {
    final InlineRenderBox box = (InlineRenderBox) node;
    return box.getStaticBoxLayoutProperties().isPreserveSpace();
  }

  public int getClassification()
  {
    return START;
  }

  public Classification getType()
  {
    return Classification.START;
  }
}
