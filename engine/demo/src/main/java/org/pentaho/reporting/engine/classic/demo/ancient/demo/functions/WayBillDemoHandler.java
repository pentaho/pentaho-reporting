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
