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


package org.pentaho.reporting.engine.classic.demo.util;

import javax.swing.Action;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;


/**
 * The DemoControler interface provides limited access to the containing demo frame. This way, a DemoPreviewHandler is
 * able to control the export action or the contents of the statusbar.
 *
 * @author Thomas Morgner
 */
public interface DemoController
{
  public JStatusBar getStatusBar();

  public Action getExportAction();
}
