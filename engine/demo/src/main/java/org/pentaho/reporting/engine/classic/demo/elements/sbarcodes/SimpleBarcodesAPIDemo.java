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


package org.pentaho.reporting.engine.classic.demo.elements.sbarcodes;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesUtility;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.elementfactory.BarcodeElementFactory;

/**
 * This demo shows different usages of barcode module available in pentaho reporting extension project.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesAPIDemo extends AbstractDemoHandler
{
  private DefaultTableModel data;

  public SimpleBarcodesAPIDemo()
  {
    data = createData();
  }

  /**
   * @return The tabular report data.
   */
  private DefaultTableModel createData()
  {
    final String[] columnNames = {"value"};

    Object[][] o = new Object[][]{
        {"TEST"},
        {"azAZ1."},
        {"A123-A"},
        {"123456789012"},
        {"01234567890"},
        {"0123456789"},
        {"12AZz%"},
        {"TEST"},
        {"test"},
        {"0251"},
        {"4020110"},
        {"1234"},
        {"1234"},
        {"555551237"},
    };

    return new DefaultTableModel(o, columnNames);
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Simple Barcodes demo (API)";
  }

  /**
   * Creates the report. For XML reports, this will most likely call the ReportGenerator, while API reports may use this
   * function to build and return a new, fully initialized report object.
   *
   * @return the fully initialized JFreeReport object.
   * @throws org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException
   *          if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = new MasterReport();

    final BarcodeElementFactory factory = new BarcodeElementFactory();
    factory.setFieldname("value");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    // remember that with barcodes we do not have much control over the element size, just make it big enough
    // to be fully printable
    factory.setMinimumSize(new Dimension(300, 50));
    factory.setType(SimpleBarcodesUtility.BARCODE_CODE128);   // code128 accepts every characters
    factory.setBarWidth(new Integer(2));
    factory.setBarHeight(new Integer(30));
    factory.setFontSize(new Integer(10));
    factory.setFontName("SansSerif");


    final ItemBand itemBand = report.getItemBand();
    itemBand.addElement(factory.createElement());

    report.setDataFactory(new TableDataFactory("default", data));
    return report;
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return null;
  }

  /**
   * Returns the presentation component for this demo. This component is shown before the real report generation is
   * started. Ususally it contains a JTable with the demo data and/or input components, which allow to configure the
   * report.
   *
   * @return the presentation component, never null.
   */
  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public static void main(final String[] args) throws Exception
  {
    ClassicEngineBoot.getInstance().start();


    final SimpleBarcodesAPIDemo demoHandler = new SimpleBarcodesAPIDemo();
    BundleWriter.writeReportToZipFile(demoHandler.createReport(), new File("/tmp/sbarcodes.prpt"));

//    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
//    frame.init();
//    frame.pack();
//    LibSwingUtil.centerFrameOnScreen(frame);
//    frame.setVisible(true);

  }
}
