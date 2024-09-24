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

package org.pentaho.reporting.engine.classic.core.modules.gui.plaintext;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Encapsulates the PlainTextExportDialog into a separate plugin.
 *
 * @author Thomas Morgner
 */
public class PlainTextExportPlugin extends AbstractExportActionPlugin {
  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * DefaultConstructor.
   */
  public PlainTextExportPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), PlainTextExportGUIModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( PlainTextExportGUIModule.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( PlainTextExportGUIModule.class.getName() ) == false ) {
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
    progressDialog.setMessage( resources.getString( "plaintext-export.progressdialog.message" ) ); //$NON-NLS-1$
    progressDialog.pack();
    LibSwingUtil.positionFrameRandomly( progressDialog );
    return progressDialog;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.export."; //$NON-NLS-1$
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
        performShowExportDialog( report, "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.Dialog" ); //$NON-NLS-1$
    if ( result == false ) {
      // user canceled the dialog ...
      return false;
    }

    final ReportProgressDialog progressDialog;
    if ( isProgressDialogEnabled( report,
        "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.ProgressDialogEnabled" ) ) {
      progressDialog = createProgressDialog();
      if ( report.getTitle() == null ) {
        progressDialog.setTitle( getResources().getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( getResources().formatMessage( "ProgressDialog.TITLE", report.getTitle() ) );
      }
    } else {
      progressDialog = null;
    }

    final PlainTextExportTask task = new PlainTextExportTask( report, progressDialog, getContext() );
    final Thread thread = new Thread( task );
    thread.start();
    return true;
  }

  /**
   * Returns the display name for the action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.export-to-plaintext.name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( "action.export-to-plaintext.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.export-to-plaintext.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for an action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.export-to-plaintext.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.export-to-plaintext.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key.
   *
   * @return The key code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.export-to-plaintext.mnemonic" ); //$NON-NLS-1$
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
