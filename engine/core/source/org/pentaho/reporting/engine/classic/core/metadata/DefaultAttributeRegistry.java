package org.pentaho.reporting.engine.classic.core.metadata;

public class DefaultAttributeRegistry implements AttributeRegistry
{
  private DefaultElementMetaData elementMetaData;

  public DefaultAttributeRegistry(final DefaultElementMetaData elementMetaData)
  {
    if (elementMetaData == null)
    {
      throw new NullPointerException();
    }
    this.elementMetaData = elementMetaData;
  }

  public void setAttributeDescription(final AttributeMetaData metaData)
  {
    this.elementMetaData.setAttributeDescription(metaData.getNameSpace(), metaData.getName(), metaData);
  }

  public AttributeMetaData getAttributeDescription(final String namespace, final String name)
  {
    return elementMetaData.getAttributeDescription(namespace, name);
  }
}
