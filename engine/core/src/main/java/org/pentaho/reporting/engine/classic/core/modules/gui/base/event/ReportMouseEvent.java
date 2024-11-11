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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class ReportMouseEvent extends EventObject {
  private MouseEvent event;

  public ReportMouseEvent( final RenderNode source, final MouseEvent event ) {
    super( source );
    this.event = event;
  }

  public RenderNode getSourceNode() {
    return (RenderNode) getSource();
  }

  public MouseEvent getSourceEvent() {
    return event;
  }
}
