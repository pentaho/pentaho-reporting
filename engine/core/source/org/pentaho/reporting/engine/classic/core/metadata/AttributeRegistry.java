package org.pentaho.reporting.engine.classic.core.metadata;

public interface AttributeRegistry
{
  public void setAttributeDescription (AttributeMetaData metaData);
  public AttributeMetaData getAttributeDescription (String namespace, String name);
}
