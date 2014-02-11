package org.pentaho.reporting.engine.classic.core.layout.model;

import java.awt.font.TextLayout;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.util.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RenderableComplexText extends RenderNode
{
  private RichTextSpec richText;
  private String text;
  private TextLayout textLayout;
  private boolean forceLinebreak;

  public RenderableComplexText(final StyleSheet styleSheet,
                               final InstanceID instanceID,
                               final ElementType elementType,
                               final ReportAttributeMap attributes,
                               final RichTextSpec text)
  {
    super(new NodeLayoutProperties(styleSheet, attributes, instanceID, elementType));
    this.text = text.getText();
    this.richText = text;
    this.forceLinebreak = false;
  }

  public RenderableComplexText(final StyleSheet styleSheet,
                               final InstanceID instanceID,
                               final ElementType elementType,
                               final ReportAttributeMap attributes,
                               final String text)
  {
    super(new NodeLayoutProperties(styleSheet, attributes, instanceID, elementType));
    this.text = text;
    this.richText = null;
    this.forceLinebreak = false;
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT;
  }

  public String getRawText()
  {
    return text;
  }

  public RichTextSpec getRichText()
  {
    if (richText == null){
      // code-smell - we probably should introduce a "ProcessedComplexText" type to distinguish between
      // raw text and text that has been processed by the CanvasMinor-step.
      throw new IllegalStateException("Calling 'getRichText' is only valid after layouting is complete.");
    }
    return richText;
  }

  public TextLayout getTextLayout()
  {
    return textLayout;
  }

  public void setTextLayout(final TextLayout textLayout)
  {
    this.textLayout = textLayout;
  }

  public void setForceLinebreak(final boolean forceLinebreak)
  {
    this.forceLinebreak = forceLinebreak;
  }

  public boolean isForceLinebreak()
  {
    return forceLinebreak;
  }

}
