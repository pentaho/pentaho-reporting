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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.InternalDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A helper class to make this demo accessible from the DemoFrontend.
 */
public class PropertyUpdatePreviewHandler implements PreviewHandler
{
  private static final Log logger = LogFactory.getLog(PropertyUpdatePreviewHandler.class);
  private InternalDemoHandler handler;

  public PropertyUpdatePreviewHandler(final InternalDemoHandler handler)
  {
    this.handler = handler;
  }

  public void attemptPreview()
  {
    try
    {
      final MasterReport report = handler.createReport();

      final PreviewDialog frame = new PreviewDialog(report);
      frame.setToolbarFloatable(true);
      frame.setReportController(new DemoReportController());
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
