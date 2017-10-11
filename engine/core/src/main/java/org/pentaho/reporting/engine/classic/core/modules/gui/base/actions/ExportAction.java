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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;

/**
 * Creation-Date: 16.11.2006, 17:52:48
 *
 * @author Thomas Morgner
 */
public class ExportAction extends AbstractAction {
  private static final Log logger = LogFactory.getLog( ExportAction.class );

  private class EnableChangeListener implements PropertyChangeListener {
    protected EnableChangeListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( actionPlugin.isEnabled() );
    }
  }

  private ExportActionPlugin actionPlugin;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public ExportAction( final ExportActionPlugin actionPlugin, final PreviewPane previewPane ) {
    if ( actionPlugin == null ) {
      throw new NullPointerException();
    }
    if ( previewPane == null ) {
      throw new NullPointerException();
    }

    this.actionPlugin = actionPlugin;
    this.previewPane = previewPane;
    putValue( Action.NAME, actionPlugin.getDisplayName() );
    putValue( Action.SHORT_DESCRIPTION, actionPlugin.getShortDescription() );
    putValue( Action.ACCELERATOR_KEY, actionPlugin.getAcceleratorKey() );
    putValue( Action.MNEMONIC_KEY, actionPlugin.getMnemonicKey() );
    putValue( Action.SMALL_ICON, actionPlugin.getSmallIcon() );
    putValue( SwingCommonModule.LARGE_ICON_PROPERTY, actionPlugin.getLargeIcon() );
    this.actionPlugin.addPropertyChangeListener( "enabled", new EnableChangeListener() ); //$NON-NLS-1$

    setEnabled( actionPlugin.isEnabled() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( actionPlugin.isEnabled() == false ) {
      return;
    }

    final MasterReport reportJob = previewPane.getReportJob();
    if ( reportJob == null ) {
      return;
    }

    actionPlugin.performExport( (MasterReport) reportJob.clone() );
  }
}
