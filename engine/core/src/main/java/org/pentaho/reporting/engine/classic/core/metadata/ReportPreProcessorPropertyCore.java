/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.Serializable;

public interface ReportPreProcessorPropertyCore extends Serializable {
  public String[] getReferencedFields( ReportPreProcessorPropertyMetaData metaData, Expression expression,
      Object attributeValue );

  public String[] getReferencedGroups( ReportPreProcessorPropertyMetaData metaData, Expression expression,
      Object attributeValue );

  public String[] getReferencedElements( ReportPreProcessorPropertyMetaData metaData, Expression expression,
      Object attributeValue );

  public ResourceReference[] getReferencedResources( final ReportPreProcessorPropertyMetaData metaData,
      final Expression expression, final Object attributeValue, final Element reportElement,
      final ResourceManager resourceManager );

  public String[] getExtraCalculationFields( ReportPreProcessorPropertyMetaData metaData );
}
