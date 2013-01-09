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
 * ActionPlugin.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.beans.PropertyChangeListener;
import javax.swing.KeyStroke;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The Action-Plugin interface defines the common properties for the Control and Export-Plugins. Implementing
 * just this interface will not make sense, you have to use either the Control- or ExportActionPlugin interface
 * instead.
 *
 * @author Thomas Morgner
 */
public interface ActionPlugin
{
  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName ();

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription ();

  /**
   * Returns the ImageDescriptor for the action.
   *
   * @return The icon.
   */
  public ImageDescriptor getImageDescriptor ();

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey ();

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey ();

  /**
   * Returns true if the action is separated, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isSeparated ();

  /**
   * Returns true if the action should be added to the toolbar, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isAddToToolbar ();

  /**
   * Returns true if the action should be added to the menu, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isAddToMenu ();

  public boolean isEnabled();

  public void addPropertyChangeListener (PropertyChangeListener l);

  public void addPropertyChangeListener (String property, PropertyChangeListener l);

  public void removePropertyChangeListener (PropertyChangeListener l);

  /**
   * A sort key used to enforce a certain order within the actions.
   *
   * @return
   */
  public int getMenuOrder ();

  public int getToolbarOrder ();

  public String getRole();

  public int getRolePreference ();

  public boolean initialize(final SwtGuiContext context);
}
