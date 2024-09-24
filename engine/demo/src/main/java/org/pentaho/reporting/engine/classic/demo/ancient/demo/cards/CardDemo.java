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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.cards;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * The card demo shows how to use JFreeReport to print multiple cards on a single page. It also offers a way to work
 * around JFreeReport's need to print all bands over the complete page width.
 *
 * @author Thomas Morgner.
 */
public class CardDemo extends CompoundDemoFrame
{
  /**
   * Default constructor.
   */
  public CardDemo(final DemoSelector selector)
  {
    super(selector);
    init();
  }

  /**
   * The starting point for the demo application.
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final DefaultDemoSelector selector = createDemoInfo();

    final CardDemo frame = new CardDemo(selector);
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }

  public static DefaultDemoSelector createDemoInfo()
  {
    final DefaultDemoSelector selector = new DefaultDemoSelector("Card demos");
    selector.addDemo(new SimpleCardDemoHandler());
    selector.addDemo(new LeadingEmptyCardsDemoHandler());
    return selector;
  }
}
