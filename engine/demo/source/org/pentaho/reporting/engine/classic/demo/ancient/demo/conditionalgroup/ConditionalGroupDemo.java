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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.conditionalgroup;

import java.math.BigDecimal;
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

public class ConditionalGroupDemo extends AbstractXmlDemoHandler
{
  private ConditionalGroupTableModel data;

  public ConditionalGroupDemo()
  {
    this.data = new ConditionalGroupTableModel();
    this.data.addRecord("Income", "Account 1", null, new BigDecimal("9999.99"));
    this.data.addRecord("Income", "Account 2", null, new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account A", "Account Z", new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account A", "Account Y", new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account B", null, new BigDecimal("9999.99"));
  }

  public String getDemoName()
  {
    return "Conditional Group Demo";
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("conditional.html", ConditionalGroupDemo.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("conditional-group-demo.xml", ConditionalGroupDemo.class);
  }


  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final ConditionalGroupDemo handler = new ConditionalGroupDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
