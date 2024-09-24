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
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A renderbox that behaves like a huge canvas. Elements can be painted freely on this box.
 *
 * @author Thomas Morgner
 */
public final class CanvasRenderBox extends RenderBox {
  public CanvasRenderBox( final StyleSheet styleSheet, final InstanceID instanceID, final BoxDefinition boxDefinition,
      final ElementType elementType, final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceID, boxDefinition, elementType,
        attributes, stateKey );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_CANVAS;
  }

  public long extendHeight( final RenderNode child, final long heightOffset ) {
    return extendHeightInRowMode( child, heightOffset );
  }
}
