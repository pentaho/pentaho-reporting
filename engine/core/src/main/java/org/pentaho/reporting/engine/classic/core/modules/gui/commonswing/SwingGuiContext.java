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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Window;

import org.pentaho.reporting.engine.classic.core.modules.gui.common.GuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;

/**
 * Extends the common GUI-Context by a way to get access to the calling window. This is a neccessary evil to support
 * modal dialogs. Try to use this handle to tamper with the calling dialog in any other way, and you will suffer weird
 * and unhappy consequences.
 *
 * @author Thomas Morgner
 */
public interface SwingGuiContext extends GuiContext {
  /**
   * Returns the calling window.
   *
   * @return the calling window, or null, if there is none.
   */
  public Window getWindow();

  public StatusListener getStatusListener();

  public ReportEventSource getEventSource();
}
