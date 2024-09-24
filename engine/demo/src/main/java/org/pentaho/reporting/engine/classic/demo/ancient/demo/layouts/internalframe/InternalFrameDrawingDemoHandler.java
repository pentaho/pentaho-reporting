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
