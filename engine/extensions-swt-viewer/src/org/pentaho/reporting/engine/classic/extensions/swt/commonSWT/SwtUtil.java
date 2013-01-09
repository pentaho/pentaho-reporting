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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.commonSWT;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * SwingUtil.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class SwtUtil
{
  private SwtUtil()
  {
  }

  /**
   * Position the specified shell at the center of the primary monitor.
   *
   * @param shell             the shell.
   */
  public static void centerShellOnScreen(final Shell shell)
  {
    final Monitor primary = shell.getDisplay().getPrimaryMonitor();
    final Rectangle bounds = primary.getBounds();
    final Rectangle rect = shell.getBounds();
    final int x = bounds.x + (bounds.width - rect.width) / 2;
    final int y = bounds.y + (bounds.height - rect.height) / 2;
    shell.setLocation(x, y);
  }

  /**
   * Positions the specified dialog within its parent.
   *
   * @param dialog the dialog to be positioned on the screen.
   */
  public static void centerDialogInParent(final Shell parent, final Shell dialog)
  {
    final Display display = parent.getDisplay();
    final int width = display.getClientArea().width;
    final int height = display.getClientArea().height;
    dialog.setLocation(((width - dialog.getSize().x) / 2) + 
        display.getClientArea().x, ((height - dialog.getSize().y) / 2) + 
        display.getClientArea().y);
  }
}
