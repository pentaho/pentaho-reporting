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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.functions;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * This demo provides a frontend for all demos showing how to use functions.
 *
 * @author Thomas Morgner
 */
public class FunctionsDemo extends CompoundDemoFrame
{
  public FunctionsDemo(final DemoSelector demoSelector)
  {
    super(demoSelector);
    init();
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final DefaultDemoSelector demoSelector = createDemoInfo();

    final FunctionsDemo frame = new FunctionsDemo(demoSelector);
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }

  public static DefaultDemoSelector createDemoInfo()
  {
    final DefaultDemoSelector demoSelector =
        new DefaultDemoSelector("Functions demos");
    demoSelector.addDemo(new ItemHidingDemoHandler());
    demoSelector.addDemo(new PaintComponentDemoHandler());
    demoSelector.addDemo(new PercentageDemo());
    demoSelector.addDemo(new WayBillDemoHandler());
    return demoSelector;
  }
}
