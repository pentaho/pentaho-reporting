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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.multireport;

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
public class MultiReportDemoCollection extends CompoundDemoFrame
{
  /**
   * Default constructor.
   */
  public MultiReportDemoCollection(final DemoSelector selector)
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

    final DefaultDemoSelector selector = MultiReportDemoCollection.createDemoInfo();

    final MultiReportDemoCollection frame = new MultiReportDemoCollection(selector);
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }

  public static DefaultDemoSelector createDemoInfo()
  {
    final DefaultDemoSelector selector = new DefaultDemoSelector("Multireport demos");
    selector.addDemo(new MultiReportDemo());
    selector.addDemo(new ThreeMultiReportDemo());
    return selector;
  }
}
