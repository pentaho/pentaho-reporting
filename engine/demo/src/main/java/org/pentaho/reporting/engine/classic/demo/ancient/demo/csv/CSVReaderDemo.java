/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
