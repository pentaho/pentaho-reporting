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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.csv;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.CSVTableModelProducer;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.DemoController;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Demo that show how to use <code>CSVTableModelProducer</code> to generate <code>TableModel</code> for JFreeReport
 * input data.
 *
 * @see CSVTableModelProducer
 */
public class CSVReaderDemo extends AbstractDemoHandler
{
  private CSVUserInputPanel inputPanel;

  /**
   * Creates the demo workspace.
   */
  public CSVReaderDemo()
  {
  }

  public String getDemoName()
  {
    return "Generic Report Generation Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    return null;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("generic-demo.html", CSVReaderDemo.class);
  }

  public synchronized void setController(final DemoController controler)
  {
    super.setController(controler);
    inputPanel = null;
  }

  public synchronized JComponent getPresentationComponent()
  {
    if (inputPanel == null)
    {
      inputPanel = new CSVUserInputPanel(getController());
    }
    return inputPanel;
  }
}
