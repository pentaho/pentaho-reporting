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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A very simple JFreeReport demo.  The purpose of this demo is to illustrate the basic steps required to connect a
 * report definition with some data and display a report preview on-screen.
 * <p/>
 * In this example, the report definition is created in code.  It is also possible to read a report definition from an
 * XML file...that is demonstrated elsewhere.
 *
 * @author David Gilbert
 */
public class StackedLayoutAPIDemoHandler extends AbstractDemoHandler
{
  private DemoTextInputPanel inputPanel;
  private PropertyUpdatePreviewHandler previewHandler;

  /**
   * Creates and displays a simple report.
   */
  public StackedLayoutAPIDemoHandler()
  {
    inputPanel = new DemoTextInputPanel();
    previewHandler = new PropertyUpdatePreviewHandler(this);
  }

  public JComponent getPresentationComponent()
  {
    return inputPanel;
  }

  /**
   * Creates a report definition.
   *
   * @return a report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {

    final MasterReport report = new MasterReport();
    final ReportHeader reportHeader = report.getReportHeader();
    report.setName(getDemoName());

    final TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName("T1");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(150, 12));
    factory.setColor(Color.black);
    factory.setHorizontalAlignment(ElementAlignment.RIGHT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("-");
    factory.setFieldname(DemoReportController.MESSAGE_ONE_FIELDNAME);
    factory.setDynamicHeight(Boolean.TRUE);
    reportHeader.addElement(factory.createElement());

    factory.setName("T2");
    factory.setAbsolutePosition(new Point2D.Float(200, 0));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setFieldname(DemoReportController.MESSAGE_TWO_FIELDNAME);
    reportHeader.addElement(factory.createElement());

    final DefaultParameterDefinition paramDef = new DefaultParameterDefinition();
    paramDef.addParameterDefinition(new PlainParameter("Message1", String.class));
    paramDef.addParameterDefinition(new PlainParameter("Message2", String.class));
    report.getParameterValues().put("Message1", inputPanel.getMessageOne());
    report.getParameterValues().put("Message2", inputPanel.getMessageTwo());
    return report;

  }

  public String getDemoName()
  {
    return "Stacked Layout Manager Demo (API)";
  }

  public PreviewHandler getPreviewHandler()
  {
    return previewHandler;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("stacked-layout.html", StackedLayoutXMLDemoHandler.class);
  }


  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final StackedLayoutAPIDemoHandler demoHandler = new StackedLayoutAPIDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
