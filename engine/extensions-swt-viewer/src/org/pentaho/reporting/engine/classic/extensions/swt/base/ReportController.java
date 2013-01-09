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

package org.pentaho.reporting.engine.classic.extensions.swt.base;

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
 * ReportController.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 * A report controler. This provides some means of configuring the
 * preview components.
 * <p>
 * The controler should use the propertyChange events provided by
 * the PreviewProxyBase and the ReportPane to update its state.
 * <p>
 * To force a new repagination, use the <code>refresh</code> method of
 * the PreviewProxyBase.
 *
 * @author Thomas Morgner
 */
public interface ReportController
{
  /**
   * Returns the graphical representation of the controler.
   * This component will be added between the menu bar and
   * the toolbar.
   * <p>
   * Changes to this property are not detected automaticly,
   * you have to call "refreshController" whenever you want to
   * display a completly new control panel.
   *
   * @return the controler component.
   */
  public JComponent getControlPanel();

  /**
   * Returns the menus that should be inserted into the menubar.
   * <p>
   * Changes to this property are not detected automaticly,
   * you have to call "refreshControler" whenever the contents
   * of the menu array changed.
   *
   * @return the menus as array, never null.
   */
  public JMenu[] getMenus();

  /**
   * Defines, whether the controler component is placed between
   * the preview pane and the toolbar.
   *
   * @return true, if this is a inner component.
   */
  public boolean isInnerComponent ();

  /**
   * Returns the location for the report controler, one of
   * BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.EAST
   * or BorderLayout.WEST.
   *
   * @return the location;
   */
  public String getControllerLocation ();

  public void initialize (PreviewPane pane);
}
