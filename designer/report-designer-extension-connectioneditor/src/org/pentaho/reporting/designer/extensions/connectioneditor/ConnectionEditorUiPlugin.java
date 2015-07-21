package org.pentaho.reporting.designer.extensions.connectioneditor;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

public class ConnectionEditorUiPlugin extends AbstractReportDesignerUiPlugin {
  public ConnectionEditorUiPlugin() {
  }

  public String[] getOverlaySources() {
    return new String[] { "org/pentaho/reporting/designer/extensions/connectioneditor/ui-overlay.xul" };
  }
}
