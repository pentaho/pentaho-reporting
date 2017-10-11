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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.io.Serializable;
import java.util.Map;

/**
 * A structural layout element. ReportElements hold style and attributes and allow to establish references that survive
 * cloning via an ID object.
 *
 * @author Thomas Morgner
 */
public interface ReportElement extends Cloneable, Serializable {
  /**
   * Assigns a new attribute expression for the given attribute. Attributes are identified by the namespace and name.
   * Each attribute can have only one expression assigned. Setting the expression to <code>null</code> clears the
   * expression and will preserve the attribute's static value.
   * <p/>
   * Functions cannot be used as attribute-expressions as attribute- and style-expressions do not receive events and are
   * not guaranteed to be executed unless the element is processed.
   *
   * @param namespace
   *          the attribute's namespace.
   * @param name
   *          the attribute's name.
   * @param value
   *          the expression.
   */
  public void setAttributeExpression( String namespace, String name, Expression value );

  /**
   * Returns the attribute expression for the given attribute identified by its namespace and attribute name.
   *
   * @param namespace
   *          the attribute's namespace.
   * @param name
   *          the attribute's name.
   * @return the assigned expression or <code>null</code> if the attribute has no expression assigned.
   */
  public Expression getAttributeExpression( String namespace, String name );

  /**
   * Returns the namespaces of all attributes that have attribute-expressions assigned.
   *
   * @return the attribute-namespaces as array.
   */
  public String[] getAttributeExpressionNamespaces();

  /**
   * Returns the names of all attributes for the given namespace that have attribute-expressions assigned.
   *
   * @param namespace
   *          the namespace for which the attribute-names should be returned, never null.
   * @return the known attribute names as array.
   */
  public String[] getAttributeExpressionNames( String namespace );

  /**
   * Defines a attribute's static value. Attributes are identified by the attribute's namespace and the attribute name.
   * Setting a attribute value to <code>null</code> removes the attribute. Attribute values are not checked for type
   * safety.
   *
   * @param namespace
   *          the attribute's namespace.
   * @param name
   *          the attribute name.
   * @param value
   *          the attribute value, or null to remove the attribute.
   */
  public void setAttribute( String namespace, String name, Object value );

  public void setAttribute( final String namespace, final String name, final Object value, final boolean notifyChange );

  public Object getAttribute( String namespace, String name );

  public <TS> TS getAttributeTyped( final String namespace, final String attribute, final Class<TS> filter );

  public String[] getAttributeNamespaces();

  public String[] getAttributeNames( String name );

  public ReportAttributeMap<Object> getAttributes();

  public ElementMetaData getMetaData();

  public ElementType getElementType();

  public Section getParentSection();

  public ElementStyleSheet getStyle();

  public ElementStyleSheet getDefaultStyleSheet();

  public ReportDefinition getReportDefinition();

  public void setStyleExpression( StyleKey property, Expression function );

  public Expression getStyleExpression( StyleKey property );

  public Map<StyleKey, Expression> getStyleExpressions();

  public Object getTreeLock();

  public String getName();

  public String getId();

  public InstanceID getObjectID();

  public ReportElement[] getChildElementsByType( ElementType type );

  public ReportElement getChildElementByType( final ElementType type );

  public ReportElement[] getChildElementsByName( String name );

  public <T> T getElementContext( Class<T> contextType );

  public SimpleStyleSheet getComputedStyle();

  public void setComputedStyle( final SimpleStyleSheet computedStyle );

  public long getChangeTracker();

  public Object getFirstAttribute( String localName );
}
