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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.event;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.util.EventObject;

public class ReportActionEvent extends EventObject {
  private RenderNode node;

  public ReportActionEvent( final Object source, final RenderNode node ) {
    super( source );
    this.node = node;
  }

  public RenderNode getNode() {
    return node;
  }

  public Object getActionParameter() {
    return node.getAttributes().getAttribute( AttributeNames.Swing.NAMESPACE, AttributeNames.Swing.ACTION );
  }
}
