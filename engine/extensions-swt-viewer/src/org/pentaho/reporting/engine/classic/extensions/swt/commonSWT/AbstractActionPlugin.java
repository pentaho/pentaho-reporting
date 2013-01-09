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
 * AbstractActionPlugin.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;

/**
 * The AbstractExportPlugin provides a basic implementation of the ActionPlugin
 * interface.
 * 
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public abstract class AbstractActionPlugin implements ActionPlugin
{
  public static final String ENABLED_PROPERTY = "enabled"; //$NON-NLS-1$
  private PropertyChangeSupport propertyChangeSupport;
  private IconTheme iconTheme;

  /**
   * The base resource class.
   */
  private SwtGuiContext context;
  private ExtendedConfiguration configuration;
  private boolean enabled;

  protected AbstractActionPlugin()
  {
    enabled = true;
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(final boolean enabled)
  {
    final boolean oldEnabled = this.enabled;
    this.enabled = enabled;
    propertyChangeSupport.firePropertyChange(
        AbstractActionPlugin.ENABLED_PROPERTY, oldEnabled, enabled);
  }

  public boolean initialize(final SwtGuiContext context)
  {
    if (context == null)
    {
      throw new NullPointerException(
      "AbstractActionPlugin.initialize(..): Context parameter cannot be null");
    }

    this.context = context;
    this.iconTheme = context.getIconTheme();
    this.configuration = new ExtendedConfigurationWrapper(context
        .getConfiguration());
    return true;
  }

  protected PropertyChangeSupport getPropertyChangeSupport()
  {
    return propertyChangeSupport;
  }

  public SwtGuiContext getContext()
  {
    return context;
  }

  public ExtendedConfiguration getConfig()
  {
    return configuration;
  }

  /**
   * Returns true if the action is separated, and false otherwise. A separated
   * action starts a new action group and will be spearated from previous
   * actions on the menu and toolbar.
   * 
   * @return true, if the action should be separated from previous actions,
   *         false otherwise.
   */
  public boolean isSeparated()
  {
    return getConfig().getBoolProperty(getConfigurationPrefix() + "separated"); //$NON-NLS-1$
  }

  /**
   * Returns true if the action should be added to the toolbar, and false
   * otherwise.
   * 
   * @return true, if the plugin should be added to the toolbar, false
   *         otherwise.
   */
  public boolean isAddToToolbar()
  {
    return getConfig().getBoolProperty(
        getConfigurationPrefix() + "add-to-toolbar"); //$NON-NLS-1$
  }

  /**
   * Returns true if the action should be added to the menu, and false
   * otherwise.
   * 
   * @return A boolean.
   */
  public boolean isAddToMenu()
  {
    final String name = getConfigurationPrefix() + "add-to-menu"; //$NON-NLS-1$
    return getConfig().getBoolProperty(name);
  }

  /**
   * Creates a progress dialog, and tries to assign a parent based on the given
   * preview proxy.
   * 
   * @return the progress dialog.
   */
  protected ReportProgressDialog createProgressDialog()
  {
    final Shell shell = context.getShell();

    final ReportProgressDialog progressDialog = new ReportProgressDialog(shell);
    progressDialog.open();
    return progressDialog;
  }

  public void addPropertyChangeListener(final PropertyChangeListener l)
  {
    propertyChangeSupport.addPropertyChangeListener(l);
  }

  public void addPropertyChangeListener(final String property,
      final PropertyChangeListener l)
  {
    propertyChangeSupport.addPropertyChangeListener(property, l);
  }

  public void removePropertyChangeListener(final PropertyChangeListener l)
  {
    propertyChangeSupport.removePropertyChangeListener(l);
  }

  public IconTheme getIconTheme()
  {
    return iconTheme;
  }

  protected abstract String getConfigurationPrefix();

  /**
   * A sort key used to enforce a certain order within the actions.
   * 
   * @return
   */
  public int getMenuOrder()
  {
    return getConfig().getIntProperty(
        getConfigurationPrefix() + "menu-order", 0); //$NON-NLS-1$
  }

  public int getToolbarOrder()
  {
    return getConfig().getIntProperty(
        getConfigurationPrefix() + "toolbar-order", 0); //$NON-NLS-1$
  }

  public String getRole()
  {
    return getConfig().getConfigProperty(getConfigurationPrefix() + "role"); //$NON-NLS-1$
  }

  public int getRolePreference()
  {
    return getConfig().getIntProperty(
        getConfigurationPrefix() + "role-preference", 0); //$NON-NLS-1$
  }
}
