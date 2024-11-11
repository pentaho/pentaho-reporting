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

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class SaveReportAsAction extends AbstractSaveReportAction {

  @Override
  protected void init() {
    putValue( Action.NAME, ActionMessages.getString( "SaveAsReport.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SaveAsReport.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getMnemonic( "SaveAsReport.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getSaveIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getKeyStroke( "SaveAsReport.Accelerator" ) );
  }

  @Override
  protected File getTarget( MasterReport report, Component parent ) {
    ResourceKey definitionSource = report.getDefinitionSource();
    File target = SaveReportUtilities.getCurrentFile( definitionSource );

    // Prompt for the filename
    target = SaveReportUtilities.promptReportFilename( parent, null );

    return target;
  }

}
