/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ConfigurationEditorDialog;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.undo.EditReportConfigUndoEntry;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class EditReportConfigurationAction extends AbstractReportContextAction {
  public EditReportConfigurationAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditReportConfigurationAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditReportConfigurationAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditReportConfigurationAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "EditReportConfigurationAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getReportDesignerContext().getActiveContext();
    if ( activeContext == null ) {
      // has no effect
      return;
    }

    final ConfigurationEditorDialog dialog;

    final Window window = LibSwingUtil.getWindowAncestor( getReportDesignerContext().getView().getParent() );
    if ( window instanceof JDialog ) {
      dialog = new ConfigurationEditorDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new ConfigurationEditorDialog( (JFrame) window );
    } else {
      dialog = new ConfigurationEditorDialog();
    }

    final HierarchicalConfiguration config =
      (HierarchicalConfiguration) activeContext.getContextRoot().getReportConfiguration();
    final HashMap oldConfig = copyConfig( config );

    if ( dialog.performEdit( config ) ) {
      final HashMap newConfig = copyConfig( config );
      activeContext.getUndo().addChange( ActionMessages.getString( "EditReportConfigurationAction.Text" ),
        new EditReportConfigUndoEntry( oldConfig, newConfig ) );
      activeContext.getContextRoot().notifyNodeStructureChanged();
    }


  }

  private HashMap copyConfig( final HierarchicalConfiguration config ) {
    final Enumeration configProperties = config.getConfigProperties();
    final HashMap oldConfig = new HashMap();
    while ( configProperties.hasMoreElements() ) {
      final String key = (String) configProperties.nextElement();
      if ( config.isLocallyDefined( key ) ) {
        oldConfig.put( key, config.getConfigProperty( key ) );
      }
    }
    return oldConfig;
  }
}
