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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LogLayouterStateAction extends AbstractReportContextAction implements SettingsListener {
  public LogLayouterStateAction() {
    putValue( Action.NAME, ActionMessages.getString( "LogLayouterStateAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "LogLayouterStateAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "LogLayouterStateAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "LogLayouterStateAction.Accelerator" ) );

    setVisible( WorkspaceSettings.getInstance().isDebugFeaturesVisible() );
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  public void settingsChanged() {
    setVisible( WorkspaceSettings.getInstance().isDebugFeaturesVisible() );
  }

  public void actionPerformed( final ActionEvent e ) {
    try {
      final LogicalPageBox layout = getActiveContext().getSharedRenderer().getLayouter().layout();
      ModelPrinter.INSTANCE.print( layout );
    } catch ( Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }
}
