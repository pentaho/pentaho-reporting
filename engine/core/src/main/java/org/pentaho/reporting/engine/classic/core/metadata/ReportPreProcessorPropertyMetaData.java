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

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

public interface ReportPreProcessorPropertyMetaData extends MetaData {
  public Class getPropertyType();

  /**
   * Can be one of "Value", "Resource", "Content", "Field", "Group", "Query", "Message", "Bundle-Key", "Bundle-Name",
   * "Name", "ElementName", "DateFormat", "NumberFormat"
   *
   * @return
   */
  public String getPropertyRole();

  public boolean isMandatory();

  public PropertyDescriptor getBeanDescriptor();

  public PropertyEditor getEditor();

  public String[] getReferencedFields( Expression expression, Object attributeValue );

  public String[] getReferencedGroups( Expression expression, Object attributeValue );

  public String[] getReferencedElements( Expression expression, Object attributeValue );

  public String[] getExtraCalculationFields();

  public ResourceReference[] getReferencedResources( Expression expression, Object attributeValue,
      Element reportElement, ResourceManager resourceManager );

  public boolean isComputed();

}
