package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class ComplexTextFactory implements RenderableTextFactory
{
  public ComplexTextFactory()
  {
  }

  public RenderNode[] createText(final int[] text,
                                 final int offset,
                                 final int length,
                                 final StyleSheet layoutContext,
                                 final ElementType elementType,
                                 final InstanceID instanceId,
                                 final ReportAttributeMap attributeMap)
  {
    return new RenderNode[] { new RenderableComplexText
            (layoutContext, instanceId, elementType, attributeMap, new String(text, offset, length))};
  }

  public RenderNode[] finishText()
  {
    return new RenderNode[0];
  }

  public void startText()
  {

  }
}
