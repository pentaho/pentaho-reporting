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
import javax.swing.Action;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CloseUnmodifiedReportsAction extends AbstractReportContextAction
{
  public CloseUnmodifiedReportsAction()
  {
    putValue(Action.NAME, ActionMessages.getString("CloseUnmodifiedReportsAction.Text"));
    putValue(Action.DEFAULT, ActionMessages.getString("CloseUnmodifiedReportsAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("CloseUnmodifiedReportsAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("CloseUnmodifiedReportsAction.Accelerator"));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext context = getReportDesignerContext();
    final int contextCount = context.getReportRenderContextCount();
    final ReportRenderContext[] contextArray = new ReportRenderContext[contextCount];
    for (int i = 0; i < contextCount; i++)
    {
      contextArray[i] = context.getReportRenderContext(i);
    }

    final ReportRenderContext[] filteredArray =
        CloseReportAction.filterSubreports(getReportDesignerContext(), contextArray);

    for (int i = 0; i < filteredArray.length; i++)
    {
      final ReportRenderContext reportRenderContext = filteredArray[i];
      if (reportRenderContext.isChanged() == false)
      {
        CloseReportAction.performUnconditionalClose(getReportDesignerContext(), reportRenderContext);
      }
    }
  }
}
