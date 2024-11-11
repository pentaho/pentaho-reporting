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
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class AddParametersAction extends AbstractReportContextAction {

  public AddParametersAction() {
    putValue( Action.NAME, ActionMessages.getString( "AddParametersAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "AddParametersAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AddParametersAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AddParametersAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getParameterIcon() );
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    super.updateActiveContext( oldContext, newContext );
    if ( newContext == null ) {
      setEnabled( false );
      return;
    }
    final AbstractReportDefinition definition = newContext.getReportDefinition();
    if ( definition instanceof SubReport ) {
      setEnabled( false );
    } else {
      setEnabled( true );
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    if ( activeContext.getReportDefinition() instanceof MasterReport ) {
      try {
        EditParametersAction.performEditMasterReportParameters( getReportDesignerContext(), null );
      } catch ( ReportDataFactoryException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

}
