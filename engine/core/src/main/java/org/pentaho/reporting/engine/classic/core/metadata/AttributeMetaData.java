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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.beans.PropertyEditor;

/**
 * Describes the known attributes an element can take.
 *
 * @author Thomas Morgner
 */
public interface AttributeMetaData extends MetaData {
  public String getNameSpace();

  public Class getTargetType();

  /**
   * Can be one of "Value", "Resource", "Content", "Field", "Group", "Query", "Message", "Bundle-Key", "Bundle-Name",
   * "Name", "ElementName", "DateFormat", "NumberFormat"
   *
   * @return
   */
  public String getValueRole();

  public boolean isMandatory();

  /**
   * Indicates if this attribute is computed. This indicates that values of the attribute will be computed at runtime
   * and any value set in the element itself is merely there for caching purposes and can be removed before writing the
   * PRPT file, before serializing and before deriving an element.
   */
  public boolean isComputed();

  /**
   * Indicates whether this attribute should be omitted when element is serialized to disk or written into a PRPT file.
   * Only mark attributes as transient which contain values that have been handled elsewhere.
   */
  public boolean isTransient();

  public boolean isBulk();

  /**
   * Indicates whether this attribute is a design-time value. Such values will not be computed at runtime and any
   * expression given for that attribute will be ignored.
   *
   * @return
   */
  public boolean isDesignTimeValue();

  /**
   * This method can return null if there is no property editor registered for this type.
   *
   * @return
   */
  public PropertyEditor getEditor();

  public String[] getReferencedFields( ReportElement element, Object attributeValue );

  public String[] getReferencedGroups( ReportElement element, Object attributeValue );

  public ResourceReference[] getReferencedResources( ReportElement element, ResourceManager resourceManager,
      Object attributeValue );

  public String[] getExtraCalculationFields();
}
