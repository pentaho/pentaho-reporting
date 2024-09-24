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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * The DefaultPreviewHandler creates a PreviewDialog for the current report.
 *
 * @author Thomas Morgner
 */
public class DefaultPreviewHandler implements PreviewHandler
{
  private static final Log logger = LogFactory.getLog(DefaultPreviewHandler.class);
  private InternalDemoHandler handler;

  public DefaultPreviewHandler(final InternalDemoHandler handler)
  {
    this.handler = handler;
  }

  /**
   * Handler method called by the preview action. This method should perform all operations to preview the report.
   */
  public void attemptPreview()
  {

    try
    {
      final MasterReport report = handler.createReport();

      final PreviewDialog frame = new PreviewDialog(report);
      frame.pack();
      LibSwingUtil.positionFrameRandomly(frame);
      frame.setVisible(true);
      frame.requestFocus();
    }
    catch (ReportDefinitionException e)
    {
      logger.error("Unable to create the report; report definition contained errors.", e);
      AbstractDemoFrame.showExceptionDialog(handler.getPresentationComponent(), "report.definitionfailure", e);
    }
  }
}
