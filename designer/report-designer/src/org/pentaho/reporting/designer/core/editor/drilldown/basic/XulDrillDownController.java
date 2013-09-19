package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.ui.xul.impl.XulEventHandler;

public interface XulDrillDownController extends XulEventHandler
{
  public DrillDownModel getModel();
  public void init(final ReportDesignerContext reportDesignerContext,
                   final DrillDownModel model,
                   final String[] extraFields);
  public void deactivate();
}
