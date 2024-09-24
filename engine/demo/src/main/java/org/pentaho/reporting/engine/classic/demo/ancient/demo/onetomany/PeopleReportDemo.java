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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class PeopleReportDemo extends CompoundDemoFrame
{
  public PeopleReportDemo(final DemoSelector demoSelector)
  {
    super(demoSelector);
    init();
  }

  public static DemoSelector createDemoInfo()
  {
    final DefaultDemoSelector demoSelector =
        new DefaultDemoSelector("One-To-Many-Elements Reports");
    demoSelector.addDemo(new PeopleReportXmlDemoHandler());
    demoSelector.addDemo(new PeopleReportAPIDemoHandler());
    return demoSelector;
  }

  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();


    final PeopleReportDemo frame = new PeopleReportDemo(createDemoInfo());
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
