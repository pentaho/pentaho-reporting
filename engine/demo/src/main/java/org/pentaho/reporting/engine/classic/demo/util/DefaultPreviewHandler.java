/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
