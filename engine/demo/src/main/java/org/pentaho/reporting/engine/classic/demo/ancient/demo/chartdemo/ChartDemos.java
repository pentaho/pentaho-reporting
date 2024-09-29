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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.chartdemo;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 28.08.2005, 21:57:24
 *
 * @author: Thomas Morgner
 */
public class ChartDemos extends CompoundDemoFrame
{
  public ChartDemos(final DemoSelector demoSelector)
  {
    super(demoSelector);
    init();
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final DefaultDemoSelector demoSelector = ChartDemos.createDemoInfo();

    final ChartDemos frame = new ChartDemos(demoSelector);
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }

  public static DefaultDemoSelector createDemoInfo()
  {
    final DefaultDemoSelector demoSelector =
        new DefaultDemoSelector("Chart Demos");
    demoSelector.addDemo(new BasicSimpleXmlChartDemo());
    demoSelector.addDemo(new BasicExtXmlChartDemo());
    demoSelector.addDemo(new MultiSimpleXmlChartDemo());
    demoSelector.addDemo(new MultiExtXmlChartDemo());
    demoSelector.addDemo(new MultiAPIChartDemo());
    demoSelector.addDemo(new TableJFreeChartDemo());
    return demoSelector;
  }
}
