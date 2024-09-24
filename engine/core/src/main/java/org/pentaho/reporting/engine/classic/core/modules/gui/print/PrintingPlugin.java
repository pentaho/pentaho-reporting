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

package org.pentaho.reporting.engine.classic.core.modules.gui.print;

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
 * An export plugin for the <code>java.awt.print</code> API.
 * <p/>
 *
 * @author Thomas Morgner
 */
public class PrintingPlugin extends AbstractExportActionPlugin {
  /**
   * Localized resources.
   */
  private final ResourceBundleSupport resources;

  /**
   * The base resource class.
   */
  public static final String BASE_RESOURCE_CLASS =
      "org.pentaho.reporting.engine.classic.core.modules.gui.print.messages.messages"; //$NON-NLS-1$
  public static final String PROGRESS_DIALOG_ENABLE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.print.ProgressDialogEnabled"; //$NON-NLS-1$

  /**
   * DefaultConstructor.
   */
  public PrintingPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), PrintingPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( PrintingPlugin.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( ClassicEngineBoot.getInstance().isModuleAvailable( AWTPrintingGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  /**
   * Returns the resourcebundle used to translate strings.
   *
   * @return the resourcebundle.
   */
  protected ResourceBundleSupport getResources() {
    return resources;
  }

  /**
   * Creates the progress dialog that monitors the export process.
   *
   * @return the progress monitor dialog.
   */
  protected ReportProgressDialog createProgressDialog() {
    final ReportProgressDialog progressDialog = super.createProgressDialog();
    progressDialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    progressDialog.setMessage( resources.getString( "printing-export.progressdialog.message" ) ); //$NON-NLS-1$
    progressDialog.pack();
    LibSwingUtil.positionFrameRandomly( progressDialog );
    return progressDialog;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.print.print."; //$NON-NLS-1$
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
    if ( "true".equals( report.getReportConfiguration().getConfigProperty( PrintingPlugin.PROGRESS_DIALOG_ENABLE_KEY,
        "false" ) ) ) { //$NON-NLS-1$ //$NON-NLS-2$
      progressDialog = createProgressDialog();
      if ( report.getTitle() == null ) {
        progressDialog.setTitle( getResources().getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( getResources().formatMessage( "ProgressDialog.TITLE", report.getTitle() ) );
      }
    } else {
      progressDialog = null;
    }

    final PrintExportTask task = new PrintExportTask( report, progressDialog, getContext() );
    final Thread worker = new Thread( task );
    worker.start();
    return true;
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return ( resources.getString( "action.print.name" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return ( resources.getString( "action.print.description" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.print.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.print.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return ( resources.getOptionalKeyStroke( "action.print.accelerator" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return ( resources.getOptionalMnemonic( "action.print.mnemonic" ) ); //$NON-NLS-1$
  }

}
