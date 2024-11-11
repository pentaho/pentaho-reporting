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


package org.pentaho.reporting.engine.classic.extensions.modules.java14print;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.print.PrintingPlugin;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A replacement to use the JDK 1.4 printing API. This class does nothing special yet.
 *
 * @author Thomas Morgner
 */
public class Java14PrintingPlugin extends PrintingPlugin {
  /**
   * Default constructor.
   */
  public Java14PrintingPlugin() {
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.extensions.modules.java14print.print.";
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( ObjectUtilities.isJDK14() == false ) {
      return false;
    }
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( Java14PrintModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  /**
   * Exports a report.
   *
   * @param report
   *          the report.
   * @return true, if the export was successful, false otherwise.
   */
  public boolean performExport( final MasterReport report ) {

    // need to connect to the report pane to receive state updates ...
    final ReportProgressDialog progressDialog;
    if ( "true".equals( report.getReportConfiguration().getConfigProperty( PROGRESS_DIALOG_ENABLE_KEY, "false" ) ) ) {
      progressDialog = createProgressDialog();
      if ( report.getTitle() == null ) {
        progressDialog.setTitle( getResources().getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( getResources().formatMessage( "ProgressDialog.TITLE", report.getTitle() ) );
      }
    } else {
      progressDialog = null;
    }

    final Java14RepaginateAndPrintExportTask task =
        new Java14RepaginateAndPrintExportTask( report, progressDialog, getContext().getStatusListener() );
    final Thread worker = new Thread( task );
    worker.start();
    return true;

  }
}
