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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;

/**
 * This is a dummy plugin. When the system encounters this plugin, it will insert the Zoom-Combobox instead.
 *
 * @author Thomas Morgner
 */
public class ZoomListActionPlugin extends AbstractActionPlugin {
  private class ReportJobListener implements PropertyChangeListener {
    protected ReportJobListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( component != null ) {
        component.setEnabled( Boolean.TRUE.equals( evt.getNewValue() ) );
      }
    }
  }

  private ReportJobListener jobListener;
  private JComboBox component;

  public ZoomListActionPlugin() {
    jobListener = new ReportJobListener();
  }

  public JComboBox getComponent() {
    return component;
  }

  public void setComponent( final JComboBox component ) {
    this.component = component;
  }

  public boolean initialize( final SwingGuiContext context ) {
    super.initialize( context );
    context.getEventSource().addPropertyChangeListener( PreviewPane.PAGINATED_PROPERTY, jobListener );
    return true;
  }

  public void deinitialize( final SwingGuiContext swingGuiContext ) {
    super.deinitialize( swingGuiContext );
    swingGuiContext.getEventSource().removePropertyChangeListener( PreviewPane.PAGINATED_PROPERTY, jobListener );
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.zoom-list."; //$NON-NLS-1$
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return null;
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return null;
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    return null;
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    return null;
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return null;
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return null;
  }

}
