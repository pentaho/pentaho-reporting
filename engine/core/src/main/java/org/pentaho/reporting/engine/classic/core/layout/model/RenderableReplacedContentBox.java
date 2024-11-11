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

public class RenderableReplacedContentBox extends RenderBox {
  private RenderableReplacedContent content;

  public RenderableReplacedContentBox( final RenderableReplacedContent content ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, SimpleStyleSheet.EMPTY_STYLE, new InstanceID(),
        BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null );
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.content = content;
    close();
  }

  public RenderableReplacedContentBox( final StyleSheet styleSheet, final InstanceID instanceId,
      final BoxDefinition boxDefinition, final ElementType elementType, final ReportAttributeMap attributes,
      final ReportStateKey stateKey, final RenderableReplacedContent content ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceId, boxDefinition, elementType,
        attributes, stateKey );
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.content = content;
    close();
  }

  public RenderableReplacedContent getContent() {
    return content;
  }

  public boolean isEmpty() {
    return false;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_CONTENT;
  }
}
