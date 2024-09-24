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

package org.pentaho.reporting.engine.classic.demo.elements.sbarcodes;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * This demo shows different usages of barcode module available in pentaho reporting extension project.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesXMLDemo extends AbstractXmlDemoHandler
{
  private DefaultTableModel data;

  public SimpleBarcodesXMLDemo()
  {
    data = createData();
  }

  /**
   * Creates data of different barcode symbologies and one associated input data.
   *
   * @return The tabular report data.
   */
  private DefaultTableModel createData()
  {
    final String[] columnNames = {"type", "value"};

    Object[][] o = new Object[][]{
        {"code39", "TEST"},
        {"code39ext", "azAZ1."},
        {"codabar", "A123-A"},
        {"ean13", "123456789012"},
        {"upca", "01234567890"},
        {"isbn", "0123456789"},
        {"code128", "12AZz%"},
        {"code128a", "TEST"},
        {"code128b", "test"},
        {"code128c", "0251"},
        {"uccean128", "4020110"},
        {"2of5", "1234"},
        {"2of5int", "1234"},
        {"postnet", "555551237"},
        {"pdf417", "kjl32452*$^^!:;;,"},
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
    return "Simple Barcodes demo (Simple-XML)";
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
    final MasterReport report = parseReport();
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
    return ObjectUtilities.getResourceRelative
        ("sbarcodes-simple.html", SimpleBarcodesXMLDemo.class);
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

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("sbarcodes-simple.xml", SimpleBarcodesXMLDemo.class);
  }

  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final SimpleBarcodesXMLDemo XMLDemoHandler = new SimpleBarcodesXMLDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(XMLDemoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
