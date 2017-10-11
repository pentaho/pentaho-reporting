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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * The AbstractExportPlugin provides a basic implementation of the ExportPlugin interface.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractActionPlugin implements ActionPlugin {
  public static final String ENABLED_PROPERTY = "enabled"; //$NON-NLS-1$
  private PropertyChangeSupport propertyChangeSupport;

  /**
   * Localized resources.
   */
  private ResourceBundleSupport baseResources;
  private IconTheme iconTheme;

  /**
   * The base resource class.
   */
  private SwingGuiContext context;
  private ExtendedConfiguration configuration;
  private boolean enabled;

  protected AbstractActionPlugin() {
    enabled = true;
    propertyChangeSupport = new PropertyChangeSupport( this );
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( final boolean enabled ) {
    final boolean oldEnabled = this.enabled;
    this.enabled = enabled;
    propertyChangeSupport.firePropertyChange( AbstractActionPlugin.ENABLED_PROPERTY, oldEnabled, enabled );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( context == null ) {
      throw new NullPointerException( "AbstractActionPlugin.initialize(..): Context parameter cannot be null" );
    }
    if ( this.context != context ) {
      this.context = context;
      this.iconTheme = context.getIconTheme();
      this.configuration = new ExtendedConfigurationWrapper( context.getConfiguration() );
      this.baseResources =
          new ResourceBundleSupport( context.getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
              .getClassLoader( SwingCommonModule.class ) );
    }
    return true;
  }

  public void deinitialize( final SwingGuiContext swingGuiContext ) {
    this.context = null;
  }

  public ResourceBundleSupport getBaseResources() {
    return baseResources;
  }

  protected PropertyChangeSupport getPropertyChangeSupport() {
    return propertyChangeSupport;
  }

  public SwingGuiContext getContext() {
    return context;
  }

  public ExtendedConfiguration getConfig() {
    return configuration;
  }

  /**
   * Returns true if the action is separated, and false otherwise. A separated action starts a new action group and will
   * be sepearated from previous actions on the menu and toolbar.
   *
   * @return true, if the action should be separated from previous actions, false otherwise.
   */
  public boolean isSeparated() {
    return getConfig().getBoolProperty( getConfigurationPrefix() + "separated" ); //$NON-NLS-1$
  }

  /**
   * Returns true if the action should be added to the toolbar, and false otherwise.
   *
   * @return true, if the plugin should be added to the toolbar, false otherwise.
   */
  public boolean isAddToToolbar() {
    return getConfig().getBoolProperty( getConfigurationPrefix() + "add-to-toolbar" ); //$NON-NLS-1$
  }

  /**
   * Returns true if the action should be added to the menu, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isAddToMenu() {
    final String name = getConfigurationPrefix() + "add-to-menu"; //$NON-NLS-1$
    return getConfig().getBoolProperty( name );
  }

  /**
   * Creates a progress dialog, and tries to assign a parent based on the given preview proxy.
   *
   * @return the progress dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final Window proxy = context.getWindow();
    if ( proxy instanceof Frame ) {
      return new ReportProgressDialog( (Frame) proxy );
    } else if ( proxy instanceof Dialog ) {
      return new ReportProgressDialog( (Dialog) proxy );
    } else {
      return new ReportProgressDialog();
    }
  }

  public void addPropertyChangeListener( final PropertyChangeListener l ) {
    propertyChangeSupport.addPropertyChangeListener( l );
  }

  public void addPropertyChangeListener( final String property, final PropertyChangeListener l ) {
    propertyChangeSupport.addPropertyChangeListener( property, l );
  }

  public void removePropertyChangeListener( final PropertyChangeListener l ) {
    propertyChangeSupport.removePropertyChangeListener( l );
  }

  public IconTheme getIconTheme() {
    return iconTheme;
  }

  protected abstract String getConfigurationPrefix();

  /**
   * A sort key used to enforce a certain order within the actions.
   *
   * @return
   */
  public int getMenuOrder() {
    return getConfig().getIntProperty( getConfigurationPrefix() + "menu-order", 0 ); //$NON-NLS-1$
  }

  public int getToolbarOrder() {
    return getConfig().getIntProperty( getConfigurationPrefix() + "toolbar-order", 0 ); //$NON-NLS-1$
  }

  public String getRole() {
    return getConfig().getConfigProperty( getConfigurationPrefix() + "role" ); //$NON-NLS-1$
  }

  public int getRolePreference() {
    return getConfig().getIntProperty( getConfigurationPrefix() + "role-preference", 0 ); //$NON-NLS-1$
  }
}
