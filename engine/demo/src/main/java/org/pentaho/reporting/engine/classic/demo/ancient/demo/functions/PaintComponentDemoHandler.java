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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
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
public class PaintComponentDemoHandler extends AbstractXmlDemoHandler
{
  private class CreateTask implements Runnable
  {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      tableModel.addComponent(new JButton("A button"));
      tableModel.addComponent(new JLabel("A Label"));
      tableModel.addComponent(new JCheckBox("A CheckBox"));
      tableModel.addComponent(new JFileChooser());
      tableModel.addComponent(new JColorChooser());
    }
  }

  private PaintComponentTableModel tableModel;

  /**
   * Constructs the demo application.
   */
  public PaintComponentDemoHandler()
  {
    tableModel = new PaintComponentTableModel();
    try
    {
      SwingUtilities.invokeAndWait(new CreateTask());
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public String getDemoName()
  {
    return "Paint Component Demo";
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
        ("paint-component.html", PaintComponentDemoHandler.class);
  }


  public JComponent getPresentationComponent()
  {
    return createDefaultTable(tableModel);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("paint-component.xml", PaintComponentDemoHandler.class);
  }


  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final PaintComponentDemoHandler demoHandler = new PaintComponentDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
