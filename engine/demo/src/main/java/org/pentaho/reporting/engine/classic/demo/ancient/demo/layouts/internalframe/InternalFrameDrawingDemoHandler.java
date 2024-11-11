/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.DemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;

/**
 * Creation-Date: 11.12.2005, 12:33:51
 *
 * @author Thomas Morgner
 */
public class InternalFrameDrawingDemoHandler implements DemoHandler
{
  public InternalFrameDrawingDemoHandler()
  {
  }

  /**
   * A helper class to make this demo accessible from the DemoFrontend.
   */
  private class InternalFrameDrawingPreviewHandler implements PreviewHandler
  {
    public InternalFrameDrawingPreviewHandler()
    {
    }

    public void attemptPreview()
    {
      final InternalFrameDemoFrame internalFrameDemoFrame = new InternalFrameDemoFrame();
      internalFrameDemoFrame.updateFrameSize(20);
      internalFrameDemoFrame.setVisible(true);
    }
  }

  public String getDemoName()
  {
    return "InternalFrame drawing";
  }

  public PreviewHandler getPreviewHandler()
  {
    return new InternalFrameDrawingPreviewHandler();
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    new InternalFrameDrawingDemoHandler().getPreviewHandler().attemptPreview();
  }
}
