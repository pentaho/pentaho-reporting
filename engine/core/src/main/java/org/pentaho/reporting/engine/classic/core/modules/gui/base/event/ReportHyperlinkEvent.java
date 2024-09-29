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

import java.util.EventObject;

public class ReportHyperlinkEvent extends EventObject {
  private RenderNode sourceNode;
  private String target;
  private String window;
  private String title;

  public ReportHyperlinkEvent( final Object source, final RenderNode sourceNode, final String target,
      final String window, final String title ) {
    super( source );
    this.sourceNode = sourceNode;
    this.target = target;
    this.window = window;
    this.title = title;
  }

  public RenderNode getSourceNode() {
    return sourceNode;
  }

  public String getTarget() {
    return target;
  }

  public String getWindow() {
    return window;
  }

  public String getTitle() {
    return title;
  }
}
