/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class BlockRenderBox extends RenderBox {
  public BlockRenderBox() {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, SimpleStyleSheet.EMPTY_STYLE, new InstanceID(),
        BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null );
  }

  public BlockRenderBox( final StyleSheet styleSheet, final InstanceID instanceID, final BoxDefinition boxDefinition,
      final ElementType elementType, final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceID, boxDefinition, elementType,
        attributes, stateKey );
    // hardcoded for now, content forms lines, which flow from top to bottom
    // and each line flows horizontally (later with support for LTR and RTL)

    // // Major axis vertical means, all childs will be placed below each other
    // setMajorAxis(VERTICAL_AXIS);
    // // Minor axis horizontal: All childs may be shifted to the left or right
    // // to do some text alignment
    // setMinorAxis(HORIZONTAL_AXIS);
  }

  protected boolean isBlockForPagebreakPurpose() {
    return true;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_BLOCK;
  }
}
