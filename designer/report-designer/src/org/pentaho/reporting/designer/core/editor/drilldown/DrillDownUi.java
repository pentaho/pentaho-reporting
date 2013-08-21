package org.pentaho.reporting.designer.core.editor.drilldown;

import java.awt.Component;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;

public interface DrillDownUi
{
  public Component getEditorPanel();
  public DrillDownModel getModel();
  public void init(final Component parent,
                   final ReportDesignerContext reportDesignerContext,
                   final DrillDownModel model,
                   final String[] extraFields) throws DrillDownUiException;
  public void deactivate();
}
