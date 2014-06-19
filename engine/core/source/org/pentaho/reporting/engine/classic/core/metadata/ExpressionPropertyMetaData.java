/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Describes the known attributes an element can take.
 *
 * @author Thomas Morgner
 */
public interface ExpressionPropertyMetaData extends MetaData
{
  public Class getPropertyType();

  /**
   * Can be one of "Value", "Resource", "Content", "Field", "Group", "Query", "Message", "Bundle-Key", "Bundle-Name",
   * "Name", "ElementName",  "DateFormat", "NumberFormat"
   *
   * @return
   */
  public String getPropertyRole();

  public boolean isMandatory();

  public PropertyDescriptor getBeanDescriptor();

  public PropertyEditor getEditor();

  public String[] getReferencedFields(Expression expression, Object attributeValue);

  public String[] getReferencedGroups(Expression expression, Object attributeValue);

  public String[] getReferencedElements(Expression expression, Object attributeValue);

  public ResourceReference[] getReferencedResources(Expression expression,
                                                    Object attributeValue,
                                                    Element reportElement,
                                                    ResourceManager resourceManager);

  public boolean isComputed();

  public String[] getExtraCalculationFields();
}
