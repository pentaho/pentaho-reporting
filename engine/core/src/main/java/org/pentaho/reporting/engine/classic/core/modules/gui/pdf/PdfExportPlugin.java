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

package org.pentaho.reporting.engine.classic.core.modules.gui.pdf;

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
 * Encapsulates the PDF export into a separate export plugin.
 *
 * @author Thomas Morgner
 */
public class PdfExportPlugin extends AbstractExportActionPlugin {
  private static final Log logger = LogFactory.getLog( PdfExportPlugin.class );

  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * The base resource class.
   */
  public static final String BASE_RESOURCE_CLASS =
      "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.messages.messages"; //$NON-NLS-1$
  public static final String PROGRESS_DIALOG_ENABLE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.ProgressDialogEnabled"; //$NON-NLS-1$

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.export."; //$NON-NLS-1$
  }

  /**
   * DefaultConstructor.
   */
  public PdfExportPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), PdfExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( PdfExportPlugin.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( PdfExportGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  /**
   * Creates the progress dialog that monitors the export process.
   *
   * @return the progress monitor dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final ReportProgressDialog progressDialog = super.createProgressDialog();
    progressDialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    progressDialog.setMessage( resources.getString( "pdf-export.progressdialog.message" ) ); //$NON-NLS-1$
    progressDialog.pack();
    LibSwingUtil.positionFrameRandomly( progressDialog );
    return progressDialog;
  }

  /**
   * Shows this dialog and (if the dialog is confirmed) saves the complete report into an PDF file.
   *
   * @param report
   *          the report being processed.
   * @return true or false.
   */
  public boolean performExport( final MasterReport report ) {
    final boolean result =
        performShowExportDialog( report, "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.Dialog" ); //$NON-NLS-1$
    if ( result == false ) {
      // user canceled the dialog ...
      return false;
    }

    final ReportProgressDialog progressDialog;
    if ( isProgressDialogEnabled( report,
        "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.ProgressDialogEnabled" ) ) {
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
      final PdfExportTask task = new PdfExportTask( report, progressDialog, getContext() );
      final Thread worker = new Thread( task );
      worker.start();
      return true;
    } catch ( Exception e ) {
      PdfExportPlugin.logger.error( "Failure while preparing the PDF export", e ); //$NON-NLS-1$
      getContext().getStatusListener().setStatus( StatusType.ERROR,
          resources.getString( "PdfExportPlugin.USER_FAILED" ), e ); //$NON-NLS-1$
      return false;
    }
  }

  /**
   * Returns the display name.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.save-as.name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( "action.save-as.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.export-to-pdf.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.export-to-pdf.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.save-as.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The key code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.save-as.mnemonic" ); //$NON-NLS-1$
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
