package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface AttributeCore extends Serializable
{
  public String[] getReferencedFields(AttributeMetaData metaData, ReportElement element, Object attributeValue);

  public String[] getReferencedGroups(AttributeMetaData metaData, ReportElement element, Object attributeValue);

  public ResourceReference[] getReferencedResources(AttributeMetaData metaData,
                                                    ReportElement element,
                                                    ResourceManager resourceManager,
                                                    Object attributeValue);

  public String[] getExtraCalculationFields(AttributeMetaData metaData);
}
