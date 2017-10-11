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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.functions;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 01.10.2005, 11:47:14
 *
 * @author Thomas Morgner
 */
public class WayBillDemoHandler extends AbstractXmlDemoHandler
{
  private WayBillTableModel tableModel;

  public WayBillDemoHandler()
  {
    tableModel = new WayBillTableModel();
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container A", "Glass Pearls", "Fragile", 5000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container A", "Chinese Silk", "Keep Dry", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container A", "Incense", "", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container B", "Palladium", "", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container B", "Tungsten", "", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container B", "Grain", "Keep Dry", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Container B", "Scottish Whiskey", "Stay Dry!", 1000));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Notes", "Note", "This freight is dutyable.", 0));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Notes", "Note", "Customs paid on 2005-08-12 12:00.", 0));
    tableModel.addItem(new WayBillTableModel.CategoryItem
        ("Notes", "Note", "Customs bill id: NY-A32ZY48473", 0));
  }

  public String getDemoName()
  {
    return "Way-Bill Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("waybill.html", WayBillDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(tableModel);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("waybill.xml", WayBillDemoHandler.class);
  }


  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final WayBillDemoHandler demoHandler = new WayBillDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
