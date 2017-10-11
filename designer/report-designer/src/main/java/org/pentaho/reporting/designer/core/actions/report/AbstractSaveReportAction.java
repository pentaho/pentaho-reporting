/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.status.ExceptionDialog;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public abstract class AbstractSaveReportAction extends AbstractReportContextAction {

  protected abstract void init();

  protected abstract File getTarget( MasterReport report, Component parent );

  public AbstractSaveReportAction() {
    init();
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    saveReport( getReportDesignerContext(), activeContext, getReportDesignerContext().getView().getParent() );
  }

  public boolean saveReport( final ReportDesignerContext context, final ReportDocumentContext activeContext,
                             final Component parent ) {
    final MasterReport report = activeContext.getContextRoot();

    // Get the current file target
    File target = getTarget( report, parent );
    if ( target == null ) {
      return false;
    }

    // if no name has been set for the report, default to the name of the file
    String attPath;
    try {
      attPath = target.getCanonicalPath();
    } catch ( IOException ioe ) {
      // then let's not set the save path attribute to the *canonical path*
      attPath = target.getAbsolutePath();
    }

    // Write the report to the filename
    if ( SaveReportUtilities.saveReport( context, activeContext, target ) ) {
      try {
        // change report save path only in case save success. see PRD-5567
        report
          .setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME, attPath );

        // Update the definition source to be the location from which the file is saved
        final ResourceManager resourceManager = report.getResourceManager();
        final Resource bundleResource = resourceManager.createDirectly( target, DocumentBundle.class );
        final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();
        final ResourceKey bundleKey = bundle.getBundleKey();
        report.setDefinitionSource( bundleKey );
        report.setContentBase( bundleKey );
        report.setResourceManager( bundle.getResourceManager() );
        activeContext.resetChangeTracker();
      } catch ( ResourceException e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
      return true;
    }

    final ExceptionDialog exceptionDialog;
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof Dialog ) {
      exceptionDialog = new ExceptionDialog( (Dialog) window );
    } else if ( window instanceof Frame ) {
      exceptionDialog = new ExceptionDialog( (Frame) window );
    } else {
      exceptionDialog = new ExceptionDialog();
    }
    exceptionDialog.showDialog();
    return false;
  }

}
