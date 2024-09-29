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


package org.pentaho.reporting.engine.classic.core.modules.gui.html;

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
 * Encapsulates the HtmlExportDialog into a separate plugin.
 *
 * @author Thomas Morgner
 */
public class HtmlStreamExportPlugin extends AbstractExportActionPlugin {
  private static final Log logger = LogFactory.getLog( HtmlStreamExportPlugin.class );
  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * DefaultConstructor.
   */
  public HtmlStreamExportPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), HtmlExportGUIModule.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( HtmlExportGUIModule.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( HtmlExportGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.html.export.stream."; //$NON-NLS-1$
  }

  /**
   * Creates the progress dialog that monitors the export process.
   *
   * @return the progress monitor dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final ReportProgressDialog progressDialog = super.createProgressDialog();
    progressDialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    progressDialog.setMessage( resources.getString( "html-export.progressdialog.message" ) ); //$NON-NLS-1$
    progressDialog.pack();
    LibSwingUtil.positionFrameRandomly( progressDialog );
    return progressDialog;
  }

  /**
   * Shows this dialog and (if the dialog is confirmed) saves the complete report into an Excel file.
   *
   * @param report
   *          the report being processed.
   * @return true or false.
   */
  public boolean performExport( final MasterReport report ) {
    final boolean result =
        performShowExportDialog( report, "org.pentaho.reporting.engine.classic.core.modules.gui.html.stream.Dialog" ); //$NON-NLS-1$
    if ( result == false ) {
      // user canceled the dialog ...
      return false;
    }

    final ReportProgressDialog progressDialog;
    if ( isProgressDialogEnabled( report,
        "org.pentaho.reporting.engine.classic.core.modules.gui.html.stream.ProgressDialogEnabled" ) ) {
      progressDialog = createProgressDialog();
      final String title = report.getTitle();
      if ( title == null ) {
        progressDialog.setTitle( getResources().getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( getResources().formatMessage( "ProgressDialog.TITLE", title ) );
      }
    } else {
      progressDialog = null;
    }

    try {
      final HtmlStreamExportTask task = new HtmlStreamExportTask( report, progressDialog, getContext() );
      final Thread worker = new Thread( task );
      worker.start();
      return true;
    } catch ( Exception e ) {
      HtmlStreamExportPlugin.logger.error( "Failure while preparing the HTML export", e ); //$NON-NLS-1$
      getContext().getStatusListener().setStatus( StatusType.ERROR,
          getResources().getString( "HtmlStreamExportPlugin.USER_FAILED" ), e ); //$NON-NLS-1$
      return false;
    }
  }

  /**
   * Returns the action display name.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.export-to-html.stream.name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( "action.export-to-html.stream.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.export-to-html.stream.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.export-to-html.stream.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.export-to-html.stream.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code for the action.
   *
   * @return The key code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.export-to-html.stream.mnemonic" ); //$NON-NLS-1$
  }

  /**
   * Returns the resourcebundle to be used to translate strings into localized content.
   *
   * @return the resourcebundle for the localization.
   */
  protected ResourceBundleSupport getResources() {
    return resources;
  }
}
