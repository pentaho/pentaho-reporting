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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 02.12.2006, 14:21:07
 *
 * @author Thomas Morgner
 */
public abstract class AbstractExportActionPlugin extends AbstractActionPlugin implements ExportActionPlugin {
  private static final Log logger = LogFactory.getLog( AbstractExportActionPlugin.class );

  private class ReportJobListener implements PropertyChangeListener {
    protected ReportJobListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( eventSource.getReportJob() != null );
    }
  }

  private ReportEventSource eventSource;

  private Messages messages;
  private ReportJobListener reportJobUpdateHandler;

  protected AbstractExportActionPlugin() {
    reportJobUpdateHandler = new ReportJobListener();
    messages =
        new Messages( Locale.getDefault(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingCommonModule.class ) );
  }

  public boolean initialize( final SwingGuiContext context ) {
    final SwingGuiContext oldContext = getContext();

    if ( super.initialize( context ) == false ) {
      return false;
    }
    if ( oldContext != null && eventSource != null ) {
      eventSource.removePropertyChangeListener( ReportEventSource.REPORT_JOB_PROPERTY, reportJobUpdateHandler ); // NON-NLS
    }

    if ( oldContext != context ) {
      messages =
          new Messages( context.getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
              .getClassLoader( SwingCommonModule.class ) );
      eventSource = context.getEventSource();
      eventSource.addPropertyChangeListener( ReportEventSource.REPORT_JOB_PROPERTY, reportJobUpdateHandler ); //$NON-NLS-1$
      setEnabled( eventSource.getReportJob() != null );
    }
    return true;
  }

  public void deinitialize( final SwingGuiContext swingGuiContext ) {
    super.deinitialize( swingGuiContext );
    if ( swingGuiContext != null && eventSource != null ) {
      eventSource.removePropertyChangeListener( ReportEventSource.REPORT_JOB_PROPERTY, reportJobUpdateHandler ); // NON-NLS
    }
  }

  /**
   * Creates a progress dialog, and tries to assign a parent based on the given preview proxy.
   *
   * @return the progress dialog.
   */
  protected ExportDialog createExportDialog( final String className ) throws InstantiationException {
    if ( className == null ) {
      throw new NullPointerException( "No classname given" ); //$NON-NLS-1$
    }

    final Window proxy = getContext().getWindow();
    if ( proxy instanceof Frame ) {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( AbstractActionPlugin.class );
      try {
        final Class aClass = Class.forName( className, true, classLoader );
        final Constructor constructor = aClass.getConstructor( new Class[] { Frame.class } );
        return (ExportDialog) constructor.newInstance( new Object[] { proxy } );
      } catch ( Exception e ) {
        AbstractExportActionPlugin.logger.error( messages.getErrorString(
            "AbstractExportActionPlugin.ERROR_0001_FAILED_EXPORT_DIALOG_CREATION", className ) ); //$NON-NLS-1$
      }
    } else if ( proxy instanceof Dialog ) {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( AbstractActionPlugin.class );
      try {
        final Class aClass = Class.forName( className, true, classLoader );
        final Constructor constructor = aClass.getConstructor( new Class[] { Dialog.class } );
        return (ExportDialog) constructor.newInstance( new Object[] { proxy } );
      } catch ( Exception e ) {
        AbstractExportActionPlugin.logger.error( messages.getErrorString(
            "AbstractExportActionPlugin.ERROR_0002_FAILED_EXPORT_DIALOG_CREATION", className ), e ); //$NON-NLS-1$
      }
    }

    final Object fallBack =
        ObjectUtilities.loadAndInstantiate( className, AbstractActionPlugin.class, ExportDialog.class );
    if ( fallBack != null ) {
      return (ExportDialog) fallBack;
    }

    AbstractExportActionPlugin.logger.error( messages.getErrorString(
        "AbstractExportActionPlugin.ERROR_0003_FAILED_EXPORT_DIALOG_CREATION", className ) ); //$NON-NLS-1$
    throw new InstantiationException( messages
        .getErrorString( "AbstractExportActionPlugin.ERROR_0004_FAILED_EXPORT_DIALOG_CREATION" ) ); //$NON-NLS-1$
  }

  /**
   * Exports a report.
   *
   * @param job
   *          the report.
   * @return A boolean.
   */
  public boolean performShowExportDialog( final MasterReport job, final String configKey ) {
    try {
      final Configuration configuration = job.getConfiguration();
      final String dialogClassName = configuration.getConfigProperty( configKey );
      final ExportDialog dialog = createExportDialog( dialogClassName );

      return dialog.performQueryForExport( job, getContext() );
    } catch ( InstantiationException e ) {
      AbstractExportActionPlugin.logger.error( messages
          .getErrorString( "AbstractExportActionPlugin.ERROR_0005_UNABLE_TO_CONFIGURE" ) ); //$NON-NLS-1$
      getContext().getStatusListener().setStatus( StatusType.ERROR,
          messages.getString( "AbstractExportActionPlugin.ERROR_0005_UNABLE_TO_CONFIGURE" ), e ); //$NON-NLS-1$
      return false;
    }
  }

  protected boolean isProgressDialogEnabled( final MasterReport report, final String configKey ) {
    return getConfig().getBoolProperty( configKey );
  }

}
