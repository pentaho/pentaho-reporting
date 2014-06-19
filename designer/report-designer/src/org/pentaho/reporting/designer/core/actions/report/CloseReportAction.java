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

import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CloseReportAction extends AbstractReportContextAction
{
  private int tabIndex;

  public CloseReportAction()
  {
    putValue(Action.NAME, ActionMessages.getString("CloseReportAction.Text"));
    putValue(Action.DEFAULT, ActionMessages.getString("CloseReportAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("CloseReportAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("CloseReportAction.Accelerator"));
    this.tabIndex = -1;
  }

  public CloseReportAction(final int tabIndex)
  {
    this();
    this.tabIndex = tabIndex;
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if (tabIndex == -1)
    {
      final ReportDocumentContext activeContext = getActiveContext();
      if (activeContext == null)
      {
        return;
      }

      performCloseReport(reportDesignerContext1, activeContext);
    }
    else
    {
      if (tabIndex >= 0 && tabIndex < reportDesignerContext1.getReportRenderContextCount())
      {
        final ReportRenderContext context = reportDesignerContext1.getReportRenderContext(tabIndex);
        performCloseReport(reportDesignerContext1, context);
      }
    }
  }

  public static boolean performCloseReport(final ReportDesignerContext context,
                                           final ReportDocumentContext activeContext)
  {
    if (activeContext.isChanged() && activeContext.getReportDefinition() instanceof MasterReport)
    {
      // ask the user and maybe save the report..
      final int option = JOptionPane.showConfirmDialog(context.getView().getParent(),
          ActionMessages.getString("ReportModifiedCloseWarning.Message"),
          ActionMessages.getString("ReportModifiedCloseWarning.Title"),
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (option == JOptionPane.YES_OPTION)
      {
        if (SaveReportAction.saveReport(context, activeContext, context.getView().getParent()) == false)
        {
          return false;
        }
      }
      else if (option == JOptionPane.CANCEL_OPTION)
      {
        return false;
      }
    }

    performUnconditionalClose(context, activeContext);
    return true;
  }

  public static void performUnconditionalClose(final ReportDesignerContext context,
                                               final ReportDocumentContext activeContext)
  {
    final int contextCount = context.getReportRenderContextCount();

    final AbstractReportDefinition reportDefinition = activeContext.getReportDefinition();
    if (reportDefinition instanceof MasterReport)
    {
      for (int i = contextCount - 1; i >= 0; i--)
      {
        final ReportRenderContext reportRenderContext = context.getReportRenderContext(i);
        if (reportRenderContext.getMasterReportElement() == reportDefinition)
        {
          context.removeReportRenderContext(i);
        }
      }
      return;
    }

    for (int i = 0; i < contextCount; i++)
    {
      final ReportRenderContext reportRenderContext = context.getReportRenderContext(i);
      if (reportRenderContext == activeContext)
      {
        context.removeReportRenderContext(i);
        return;
      }
    }

  }

  public static ReportRenderContext[] filterSubreports(final ReportDesignerContext context,
                                                       final ReportRenderContext[] closeContexts)
  {
    // remove all reports that share the same master-report.
    final LinkedHashMap<InstanceID, ReportRenderContext> map = new LinkedHashMap<InstanceID, ReportRenderContext>();
    for (int i = 0; i < closeContexts.length; i++)
    {
      final ReportRenderContext closeContext = closeContexts[i];
      final InstanceID id = closeContext.getMasterReportElement().getObjectID();
      if (map.containsKey(id) == false)
      {
        map.put(id, closeContext);
      }
      else
      {
        if (closeContext.getMasterReportElement() == closeContext.getReportDefinition())
        {
          // master-reports override all subreports
          map.put(id, closeContext);
        }
      }
    }
    return map.values().toArray(new ReportRenderContext[map.size()]);
  }
}
