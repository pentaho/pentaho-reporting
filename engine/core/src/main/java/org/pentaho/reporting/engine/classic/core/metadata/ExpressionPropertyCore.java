/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface ExpressionPropertyCore extends Serializable {
  public String[]
    getReferencedFields( ExpressionPropertyMetaData metaData, Expression expression, Object attributeValue );

  public String[]
    getReferencedGroups( ExpressionPropertyMetaData metaData, Expression expression, Object attributeValue );

  public String[] getReferencedElements( ExpressionPropertyMetaData metaData, Expression expression,
      Object attributeValue );

  public ResourceReference[] getReferencedResources( ExpressionPropertyMetaData metaData, Expression expression,
      Object attributeValue, Element reportElement, ResourceManager resourceManager );

  public String[] getExtraCalculationFields( ExpressionPropertyMetaData metaData );

}
