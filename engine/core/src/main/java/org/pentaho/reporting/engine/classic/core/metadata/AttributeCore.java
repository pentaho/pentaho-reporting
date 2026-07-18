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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.Serializable;

/**
 * The attribute core enables semantic inspections of attribute values.
 */
public interface AttributeCore extends Serializable {
  public String[] getReferencedFields( AttributeMetaData metaData, ReportElement element, Object attributeValue );

  public String[] getReferencedGroups( AttributeMetaData metaData, ReportElement element, Object attributeValue );

  public ResourceReference[] getReferencedResources( AttributeMetaData metaData, ReportElement element,
      ResourceManager resourceManager, Object attributeValue );

  public String[] getExtraCalculationFields( AttributeMetaData metaData );
}
