/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
