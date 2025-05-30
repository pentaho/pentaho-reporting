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


package org.pentaho.reporting.engine.classic.core.modules.gui.csv;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Encapsulates the CSVDataExportDialog into a separate plugin.
 *
 * @author Thomas Morgner
 */
public class CSVTableExportPlugin extends AbstractExportActionPlugin {
  private static final Log logger = LogFactory.getLog( CSVTableExportPlugin.class );
  public static final String BASE_RESOURCE_CLASS =
      "org.pentaho.reporting.engine.classic.core.modules.gui.csv.messages.messages"; //$NON-NLS-1$

  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * DefaultConstructor.
   */
  public CSVTableExportPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), CSVTableExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( CSVTableExportPlugin.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( CSVExportGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.csv.export.table."; //$NON-NLS-1$
  }

  /**
   * Creates the report progress dialog used to monitor the export.
   *
   * @return the created dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final ReportProgressDialog progressDialog = super.createProgressDialog();
    progressDialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    progressDialog.setMessage( resources.getString( "cvs-export.progressdialog.message" ) ); //$NON-NLS-1$
    progressDialog.pack();
    LibSwingUtil.positionFrameRandomly( progressDialog );
    return progressDialog;
  }

  /**
   * Shows this dialog and (if the dialog is confirmed) saves the complete report into an comma separated values file.
   *
   * @param report
   *          the report being processed.
   * @return true or false.
   */
  public boolean performExport( final MasterReport report ) {
    if ( report == null ) {
      throw new NullPointerException();
    }

    final boolean result =
        performShowExportDialog( report, "org.pentaho.reporting.engine.classic.core.modules.gui.csv.table.Dialog" ); //$NON-NLS-1$
    if ( result == false ) {
      // user canceled the dialog ...
      return false;
    }

    final ReportProgressDialog progressDialog;
    if ( isProgressDialogEnabled( report,
        "org.pentaho.reporting.engine.classic.core.modules.gui.csv.table.ProgressDialogEnabled" ) ) { //$NON-NLS-1$
      progressDialog = createProgressDialog();
      if ( report.getTitle() == null ) {
        progressDialog.setTitle( getResources().getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( getResources().formatMessage( "ProgressDialog.TITLE", report.getTitle() ) );
      }
    } else {
      progressDialog = null;
    }

    try {
      final Runnable task = new CSVTableExportTask( report, progressDialog, getContext() );
      final Thread worker = new Thread( task );
      worker.start();
      return true;
    } catch ( Exception e ) {
      CSVTableExportPlugin.logger.error( "Failure while preparing the CSV export", e ); //$NON-NLS-1$
      getContext().getStatusListener().setStatus( StatusType.ERROR,
          getResources().getString( "CVSExportPlugin.ERROR_0001_FAILED" ), e ); //$NON-NLS-1$
      return false;
    }
  }

  /**
   * Returns the resourcebundle to be used to translate strings into localized content.
   *
   * @return the resourcebundle for the localization.
   */
  protected ResourceBundleSupport getResources() {
    return resources;
  }

  /**
   * Returns the display name for the CSV dialog.
   *
   * @return The name.
   */
  public String getDisplayName() {
    return resources.getString( "action.export-to-csv.table.name" ); //$NON-NLS-1$
  }

  /**
   * Returns a short description for the CSV dialog.
   *
   * @return The description.
   */
  public String getShortDescription() {
    return resources.getString( "action.export-to-csv.table.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the dialog.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.export-to-csv.table.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the dialog.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.export-to-csv.table.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the action associated with the dialog.
   *
   * @return The key stroke.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.export-to-csv.table.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code for the action associated with the dialog.
   *
   * @return The key code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.export-to-csv.table.mnemonic" ); //$NON-NLS-1$
  }
}
