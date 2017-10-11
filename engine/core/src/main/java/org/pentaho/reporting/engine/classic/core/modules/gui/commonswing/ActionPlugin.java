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

import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * The Action-Plugin interface defines the common properties for the Control and Export-Plugins. Implementing just this
 * interface will not make sense, you have to use either the Control- or ExportActionPlugin interface instead.
 *
 * @author Thomas Morgner
 */
public interface ActionPlugin {
  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName();

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription();

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon();

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon();

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey();

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey();

  /**
   * Returns true if the action is separated, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isSeparated();

  /**
   * Returns true if the action should be added to the toolbar, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isAddToToolbar();

  /**
   * Returns true if the action should be added to the menu, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isAddToMenu();

  public boolean isEnabled();

  public void addPropertyChangeListener( PropertyChangeListener l );

  public void addPropertyChangeListener( String property, PropertyChangeListener l );

  public void removePropertyChangeListener( PropertyChangeListener l );

  /**
   * A sort key used to enforce a certain order within the actions.
   *
   * @return
   */
  public int getMenuOrder();

  public int getToolbarOrder();

  public String getRole();

  public int getRolePreference();

  public boolean initialize( final SwingGuiContext context );

  public void deinitialize( SwingGuiContext swingGuiContext );
}
