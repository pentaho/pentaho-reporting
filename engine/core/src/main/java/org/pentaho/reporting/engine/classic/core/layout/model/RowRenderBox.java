/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
