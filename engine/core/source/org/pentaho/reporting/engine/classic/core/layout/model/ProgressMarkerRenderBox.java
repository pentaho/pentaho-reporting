package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public class ProgressMarkerRenderBox extends BlockRenderBox
{
  public ProgressMarkerRenderBox()
  {
    getStaticBoxLayoutProperties().setPlaceholderBox(StaticBoxLayoutProperties.PlaceholderType.SECTION);
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER;
  }

  public void setStateKey(final ReportStateKey stateKey)
  {
    super.setStateKey(stateKey);
  }
}
