package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

public class DrillDownUiPlugin extends AbstractReportDesignerUiPlugin
{
  public DrillDownUiPlugin()
  {
  }

  public String[] getOverlaySources()
  {
    return new String[]{"org/pentaho/reporting/designer/extensions/pentaho/drilldown/ui-overlay.xul"};
  }

}
