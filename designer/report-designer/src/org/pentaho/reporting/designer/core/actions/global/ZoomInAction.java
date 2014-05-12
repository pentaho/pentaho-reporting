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

package org.pentaho.reporting.designer.core.actions.global;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PreviewPaneUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ZoomInAction extends AbstractReportContextAction
{
  private static double[] ZOOM_FACTORS = {0.50f, 0.75f, 1.00f, 1.25f, 1.50f, 2.00f, 4.00f};

  public ZoomInAction()
  {
    putValue(Action.NAME, ActionMessages.getString("ZoomInAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("ZoomInAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("ZoomInAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ZoomInAction.Accelerator"));
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportDocumentContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }
    final ZoomModel zoomModel = activeContext.getZoomModel();

    final double nextZoomIn = PreviewPaneUtilities.getNextZoomIn(zoomModel.getZoomAsPercentage(), ZOOM_FACTORS);
    if (nextZoomIn == 0)
    {
      return;
    }
    zoomModel.setZoomAsPercentage((float) nextZoomIn);
  }
}
