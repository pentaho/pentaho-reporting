package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class SectionRenderBox extends BlockRenderBox
{
  public SectionRenderBox(final StyleSheet styleSheet,
                          final InstanceID instanceID,
                          final BoxDefinition boxDefinition,
                          final ElementType elementType,
                          final ReportAttributeMap attributes,
                          final ReportStateKey stateKey)
  {
    super(styleSheet, instanceID, boxDefinition, elementType, attributes, stateKey);
    getStaticBoxLayoutProperties().setPlaceholderBox(StaticBoxLayoutProperties.PlaceholderType.SECTION);
    getStaticBoxLayoutProperties().setSectionContext(true);
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_BOX_SECTION;
  }

  public void clear()
  {
    super.clear();
  }
}
