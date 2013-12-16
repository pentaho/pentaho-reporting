package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RenderableComplexText extends RenderNode
{
  private String text;

  public RenderableComplexText(final StyleSheet styleSheet,
                               final InstanceID instanceID,
                               final ElementType elementType,
                               final ReportAttributeMap attributes,
                               final String text)
  {
    super(new NodeLayoutProperties(styleSheet, attributes, instanceID, elementType));
    this.text = text;
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT;
  }

  public String getRawText()
  {
    return text;
  }
}
