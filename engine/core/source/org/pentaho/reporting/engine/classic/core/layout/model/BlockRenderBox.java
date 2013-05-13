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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class BlockRenderBox extends RenderBox
{
  public BlockRenderBox()
  {
    super(RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS,
        SimpleStyleSheet.EMPTY_STYLE, new InstanceID(), BoxDefinition.EMPTY,
        AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null);
  }

  public BlockRenderBox(final StyleSheet styleSheet,
                        final InstanceID instanceID,
                        final BoxDefinition boxDefinition,
                        final ElementType elementType,
                        final ReportAttributeMap attributes,
                        final ReportStateKey stateKey)
  {
    super(RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceID, boxDefinition, elementType,
        attributes, stateKey);
    // hardcoded for now, content forms lines, which flow from top to bottom
    // and each line flows horizontally (later with support for LTR and RTL)

//    // Major axis vertical means, all childs will be placed below each other
//    setMajorAxis(VERTICAL_AXIS);
//    // Minor axis horizontal: All childs may be shifted to the left or right
//    // to do some text alignment
//    setMinorAxis(HORIZONTAL_AXIS);
  }

  protected boolean isBlockForPagebreakPurpose()
  {
    return true;
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_BOX_BLOCK;
  }
}
