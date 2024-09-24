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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
