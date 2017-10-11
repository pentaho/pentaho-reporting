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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A block-render box that layouts all elements on the horizontal axis using the direct child element's width as fixed
 * layout constraint. The element's absolute position is not taken into account.
 *
 * @author Thomas Morgner
 */
public class RowRenderBox extends RenderBox {
  public RowRenderBox( final StyleSheet styleSheet ) {
    super( RenderNode.HORIZONTAL_AXIS, RenderNode.VERTICAL_AXIS, styleSheet, new InstanceID(), BoxDefinition.EMPTY,
        AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null );
  }

  public RowRenderBox( final StyleSheet styleSheet, final InstanceID instanceID, final BoxDefinition boxDefinition,
      final ElementType elementType, final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( RenderNode.HORIZONTAL_AXIS, RenderNode.VERTICAL_AXIS, styleSheet, instanceID, boxDefinition, elementType,
        attributes, stateKey );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_ROWBOX;
  }

  public boolean isEmptyNodesHaveSignificance() {
    return getNodeLayoutProperties().getStyleSheet().getBooleanStyleProperty(
        ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, true );
  }

  public long extendHeight( final RenderNode child, final long heightOffset ) {
    return extendHeightInRowMode( child, heightOffset );
  }
}
