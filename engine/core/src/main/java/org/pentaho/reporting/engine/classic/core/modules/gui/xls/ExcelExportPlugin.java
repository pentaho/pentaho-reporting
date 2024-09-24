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

package org.pentaho.reporting.engine.classic.core.modules.gui.xls;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

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
 * Encapsulates the ExcelExportDialog into a separate plugin.
 *
 * @author Thomas Morgner
 */
public class ExcelExportPlugin extends AbstractExportActionPlugin {
  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * The base resource class.
   */
  public static final String BASE_RESOURCE_CLASS =
      "org.pentaho.reporting.engine.classic.core.modules.gui.xls.messages.messages"; //$NON-NLS-1$

  /**
   * DefaultConstructor.
   */
  public ExcelExportPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), ExcelExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( ExcelExportPlugin.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( ExcelExportGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.xls.export.xls."; //$NON-NLS-1$
  }

  /**
   * Creates the progress dialog that monitors the export process.
   *
   * @return the progress monitor dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final ReportProgressDialog progressDialog = super.createProgressDialog();
    progressDialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    progressDialog.setMessage( resources.getString( "excel-export.progressdialog.message" ) ); //$NON-NLS-1$
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
        performShowExportDialog( report, "org.pentaho.reporting.engine.classic.core.modules.gui.xls.Dialog" ); //$NON-NLS-1$
    if ( result == false ) {
      // user canceled the dialog ...
      return false;
    }

    final ReportProgressDialog progressDialog;
    if ( isProgressDialogEnabled( report,
        "org.pentaho.reporting.engine.classic.core.modules.gui.xls.ProgressDialogEnabled" ) ) {
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
      final ExcelExportTask task = new ExcelExportTask( report, progressDialog, getContext() );
      final Thread worker = new Thread( task );
      worker.start();
      return true;
    } catch ( Exception e ) {
      getContext().getStatusListener().setStatus( StatusType.ERROR,
          resources.getString( "ExcelExportPlugin.USER_FAILED" ), e ); //$NON-NLS-1$
      return false;
    }
  }

  /**
   * Returns a short description for the Excel dialog.
   *
   * @return The description.
   */
  public String getShortDescription() {
    return resources.getString( "action.export-to-excel.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the dialog.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.export-to-excel.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the dialog.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.export-to-excel.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the action associated with the dialog.
   *
   * @return The key stroke.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.export-to-excel.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code for the action associated with the dialog.
   *
   * @return The key code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.export-to-excel.mnemonic" ); //$NON-NLS-1$
  }

  /**
   * Returns the display name.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.export-to-excel.name" ); //$NON-NLS-1$
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
