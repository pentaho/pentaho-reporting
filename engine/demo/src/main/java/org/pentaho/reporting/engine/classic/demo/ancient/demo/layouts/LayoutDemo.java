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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe.InternalFrameDrawingDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * The LayoutDemo is the frontend class for all layouting related demos.
 *
 * @author Thomas Morgner
 */
public class LayoutDemo extends CompoundDemoFrame
{
  public LayoutDemo(final DemoSelector demoSelector)
  {
    super(demoSelector);
    init();
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final DefaultDemoSelector demoSelector = createDemoInfo();

    final LayoutDemo frame = new LayoutDemo(demoSelector);
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }

  public static DefaultDemoSelector createDemoInfo()
  {
    final DefaultDemoSelector demoSelector =
        new DefaultDemoSelector("Layout demos");
    demoSelector.addDemo(new BandInBandStackingDemoHandler());
    demoSelector.addDemo(new StackedLayoutXMLDemoHandler());
    demoSelector.addDemo(new StackedLayoutAPIDemoHandler());
    demoSelector.addDemo(new ComponentDrawingDemoHandler());
    demoSelector.addDemo(new InternalFrameDrawingDemoHandler());
    return demoSelector;
  }
}
