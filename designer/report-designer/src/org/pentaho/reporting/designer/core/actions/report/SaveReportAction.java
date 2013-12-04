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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.report;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.status.ExceptionDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public final class SaveReportAction extends AbstractReportContextAction
{
  public SaveReportAction()
  {
    putValue(Action.NAME, ActionMessages.getString("SaveReport.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("SaveReport.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getMnemonic("SaveReport.Mnemonic"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getSaveIcon());
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getKeyStroke("SaveReport.Accelerator"));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportDocumentContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    saveReport(getReportDesignerContext(), activeContext, getReportDesignerContext().getView().getParent());
  }

  public static boolean saveReport(final ReportDesignerContext context,
                                   final ReportDocumentContext activeContext,
                                   final Component parent)
  {
    // Get the current file target
    final MasterReport report = activeContext.getContextRoot();
    final ResourceKey definitionSource = report.getDefinitionSource();
    File target = SaveReportUtilities.getCurrentFile(definitionSource);

    // If there is no target, this file has not been save before ... prompt for a filename
    if (target == null)
    {
      target = SaveReportUtilities.promptReportFilename(parent, null);
    }
    else
    {
      target = SaveReportUtilities.validateFileExtension(target, parent);
    }
    if (target == null)
    {
      return false;
    }

    // if no name has been set for the report, default to the name of the file

    try
    {
      report.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path", target.getCanonicalPath()); // NON-NLS
    }
    catch (IOException ioe)
    {
      // then let's not set the save path attribute to the *canonical path*
      report.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path", target.getAbsolutePath()); // NON-NLS
    }

    // Write the report to the filename
    if (SaveReportUtilities.saveReport(context, activeContext, target))
    {
      try
      {
        // Update the definition source to be the location from which the file is saved
        final ResourceManager resourceManager = report.getResourceManager();
        final Resource bundleResource = resourceManager.createDirectly(target, DocumentBundle.class);
        final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();
        final ResourceKey bundleKey = bundle.getBundleKey();
        report.setDefinitionSource(bundleKey);
        report.setContentBase(bundleKey);
        report.setResourceManager(bundle.getResourceManager());
        activeContext.resetChangeTracker();
      }
      catch (ResourceException e)
      {
        UncaughtExceptionsModel.getInstance().addException(e);
      }
      return true;
    }

    final ExceptionDialog exceptionDialog;
    final Window window = LibSwingUtil.getWindowAncestor(parent);
    if (window instanceof Dialog)
    {
      exceptionDialog = new ExceptionDialog((Dialog) window);
    }
    else if (window instanceof Frame)
    {
      exceptionDialog = new ExceptionDialog((Frame) window);
    }
    else
    {
      exceptionDialog = new ExceptionDialog();
    }
    exceptionDialog.showDialog();
    return false;
  }
}
