/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;

/**
 * Creation-Date: 16.11.2006, 17:52:48
 *
 * @author Thomas Morgner
 */
public class ControlAction extends AbstractAction {
  private class EnableChangeListener implements PropertyChangeListener {
    protected EnableChangeListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( actionPlugin.isEnabled() );
    }
  }

  private ControlActionPlugin actionPlugin;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public ControlAction( final ControlActionPlugin actionPlugin, final PreviewPane previewPane ) {
    if ( actionPlugin == null ) {
      throw new NullPointerException();
    }
    if ( previewPane == null ) {
      throw new NullPointerException();
    }

    this.actionPlugin = actionPlugin;
    this.actionPlugin.addPropertyChangeListener( "enabled", new EnableChangeListener() ); //$NON-NLS-1$
    this.previewPane = previewPane;
    putValue( Action.NAME, actionPlugin.getDisplayName() );
    putValue( Action.SHORT_DESCRIPTION, actionPlugin.getShortDescription() );
    putValue( Action.ACCELERATOR_KEY, actionPlugin.getAcceleratorKey() );
    putValue( Action.MNEMONIC_KEY, actionPlugin.getMnemonicKey() );
    putValue( Action.SMALL_ICON, actionPlugin.getSmallIcon() );
    putValue( SwingCommonModule.LARGE_ICON_PROPERTY, actionPlugin.getLargeIcon() );

    setEnabled( actionPlugin.isEnabled() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    actionPlugin.configure( previewPane );
  }
}
