package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

public interface ParameterComponent
{
  public JComponent getUIComponent();
  public void initialize() throws ReportDataFactoryException;
}
