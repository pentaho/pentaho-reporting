package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface ReportPreProcessorPropertyCore extends Serializable
{
  public String[] getReferencedFields(ReportPreProcessorPropertyMetaData metaData, Expression expression, Object attributeValue);

  public String[] getReferencedGroups(ReportPreProcessorPropertyMetaData metaData, Expression expression, Object attributeValue);

  public String[] getReferencedElements(ReportPreProcessorPropertyMetaData metaData, Expression expression, Object attributeValue);

  public ResourceReference[] getReferencedResources(final ReportPreProcessorPropertyMetaData metaData,
                                                    final Expression expression,
                                                    final Object attributeValue,
                                                    final Element reportElement,
                                                    final ResourceManager resourceManager);
  
  public String[] getExtraCalculationFields(ReportPreProcessorPropertyMetaData metaData);
}
