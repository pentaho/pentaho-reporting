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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import java.net.URL;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.functions.PaintComponentTableModel;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A simple report where column 3 displays (column 1 / column 2) as a percentage.
 *
 * @author David Gilbert
 */
public class ComponentDrawingDemoHandler extends AbstractXmlDemoHandler
{
  private PaintComponentTableModel tableModel;

  /**
   * Constructs the demo application.
   */
  public ComponentDrawingDemoHandler()
  {
    tableModel = new PaintComponentTableModel();
//    tableModel.addComponent(new JButton ("A button"));
//    tableModel.addComponent(new JLabel ("A Label"));
//    tableModel.addComponent(new JCheckBox ("A CheckBox"));
    tableModel.addComponent(new JFileChooser());
    tableModel.addComponent(new JColorChooser());
  }

  public String getDemoName()
  {
    return "Component Drawing Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("component-drawing.html", ComponentDrawingDemoHandler.class);
  }


  public JComponent getPresentationComponent()
  {
    return createDefaultTable(tableModel);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("component-drawing.xml", ComponentDrawingDemoHandler.class);
  }


  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final ComponentDrawingDemoHandler demoHandler = new ComponentDrawingDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
