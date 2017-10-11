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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors.  All rights reserved.
 */

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
