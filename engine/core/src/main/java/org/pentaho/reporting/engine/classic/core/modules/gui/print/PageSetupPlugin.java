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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ControlActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * An export control plugin that handles the setup of page format objects for the report.
 *
 * @author Thomas Morgner
 */
public class PageSetupPlugin extends AbstractActionPlugin implements ControlActionPlugin {
  private class ReportJobListener implements PropertyChangeListener {
    protected ReportJobListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( eventSource.getReportJob() != null );
    }
  }

  /**
   * Localised resources.
   */
  private ResourceBundleSupport resources;
  private ReportEventSource eventSource;
  private PageSetupPlugin.ReportJobListener reportJobListener;

  /**
   * Default Constructor.
   */
  public PageSetupPlugin() {
    resources =
        new ResourceBundleSupport( Locale.getDefault(), PrintingPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
            .getClassLoader( PrintingPlugin.class ) );
    reportJobListener = new ReportJobListener();
  }

  public boolean initialize( final SwingGuiContext context ) {
    if ( super.initialize( context ) == false ) {
      return false;
    }
    eventSource = context.getEventSource();
    eventSource.addPropertyChangeListener( "reportJob", reportJobListener ); //$NON-NLS-1$
    setEnabled( eventSource.getReportJob() != null );

    if ( ClassicEngineBoot.getInstance().isModuleAvailable( AWTPrintingGUIModule.class.getName() ) == false ) {
      return false;
    }
    return true;
  }

  public void deinitialize( final SwingGuiContext swingGuiContext ) {
    super.deinitialize( swingGuiContext );
    swingGuiContext.getEventSource().removePropertyChangeListener( ReportEventSource.REPORT_JOB_PROPERTY,
        reportJobListener );
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return ( resources.getString( "action.page-setup.name" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return ( resources.getString( "action.page-setup.description" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.page-setup.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.page-setup.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return null;
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.page-setup.mnemonic" ); //$NON-NLS-1$
  }

  /**
   * Returns the resourcebundle to be used to translate strings into localized content.
   *
   * @return the resourcebundle for the localisation.
   */
  protected ResourceBundleSupport getResources() {
    return resources;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.print.page-setup."; //$NON-NLS-1$
  }

  public boolean configure( final PreviewPane pane ) {
    final MasterReport report = pane.getReportJob();

    if ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.gui.print.UseAlternatePageSetupDialog" ) ) ) {
      final PageSetupDialog dialog;
      final Window proxy = getContext().getWindow();
      if ( proxy instanceof Frame ) {
        dialog = new PageSetupDialog( getContext(), (Frame) proxy );
      } else if ( proxy instanceof Dialog ) {
        dialog = new PageSetupDialog( getContext(), (Dialog) proxy );
      } else {
        dialog = new PageSetupDialog( getContext() );
      }
      dialog.pack();
      LibSwingUtil.centerDialogInParent( dialog );
      final PageDefinition definition = dialog.performSetup( report.getPageDefinition() );
      if ( dialog.isConfirmed() == false ) {
        return false;
      }
      report.setPageDefinition( definition );
      pane.setReportJob( report );
      return true;
    } else {

      final PrinterJob pj = PrinterJob.getPrinterJob();
      final PageFormat original = report.getPageDefinition().getPageFormat( 0 );
      final PageFormat pf = pj.validatePage( pj.pageDialog( original ) );
      if ( PageFormatFactory.isEqual( pf, original ) ) {
        return false;
      }

      final PageDefinition pageDefinition = report.getPageDefinition();
      if ( pageDefinition instanceof SimplePageDefinition ) {
        final SimplePageDefinition spd = (SimplePageDefinition) pageDefinition;
        report.setPageDefinition( new SimplePageDefinition( pf, spd.getPageCountHorizontal(), spd
            .getPageCountVertical() ) );
      } else {
        report.setPageDefinition( new SimplePageDefinition( pf ) );
      }
      pane.setReportJob( report );
      return true;
    }
  }
}
