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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Component;
import java.awt.Window;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class DefaultGuiContext extends AbstractGuiContext {
  private Component parent;
  private MasterReport report;

  public DefaultGuiContext( final Component parent, final MasterReport report ) {
    this.parent = parent;
    this.report = report;
  }

  public Window getWindow() {
    if ( parent != null ) {
      return LibSwingUtil.getWindowAncestor( parent );
    }
    return null;
  }

  public MasterReport getReportJob() {
    return report;
  }
}
