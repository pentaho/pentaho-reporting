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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.pentaho.reporting.engine.classic.demo.ClassicEngineDemoBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * A Window close handler that either closes the frame, or if the frame is not embedded, shuts down the VM.
 *
 * @author Thomas Morgner
 */
public class DefaultCloseHandler extends WindowAdapter
{
  public DefaultCloseHandler()
  {
  }

  /**
   * Handles the window closing event.
   *
   * @param event the window event.
   */
  public void windowClosing(final WindowEvent event)
  {
    final Configuration configuration = ClassicEngineDemoBoot.getInstance().getGlobalConfig();
    if ("false".equals(configuration.getConfigProperty(AbstractDemoFrame.EMBEDDED_KEY, "false")))
    {
      System.exit(0);
    }
    else
    {
      event.getWindow().setVisible(false);
    }
  }
}
