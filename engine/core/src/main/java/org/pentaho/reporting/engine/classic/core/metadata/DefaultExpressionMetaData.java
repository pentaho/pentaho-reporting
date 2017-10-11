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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ExpressionMetaDataBuilder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultExpressionMetaData extends AbstractMetaData implements ExpressionMetaData {
  public static final int NO_LAYOUT_PROCESSOR = 0;
  public static final int ELEMENT_LAYOUT_PROCESSOR = 1;
  public static final int GLOBAL_LAYOUT_PROCESSOR = 2;

  private Class<? extends Expression> expressionType;
  private Class<?> resultType;
  private HashMap<String, ExpressionPropertyMetaData> properties;
  private int layoutProcessorMode;

  private transient SharedBeanInfo beanInfo;
  private transient String[] propertyKeys;
  private transient ExpressionPropertyMetaData[] propertyMetaData;

  public DefaultExpressionMetaData( final String bundleLocation, final boolean expert, final boolean preferred,
      final boolean hidden, final boolean deprecated, final Class<? extends Expression> expressionType,
      final Class<?> resultType, final Map<String, ExpressionPropertyMetaData> attributes,
      final SharedBeanInfo beanInfo, final int layoutProcessorMode, final MaturityLevel maturityLevel,
      final int compatibilityLevel ) {
    super( expressionType.getName(), bundleLocation, "", expert, preferred, hidden, deprecated, maturityLevel,
        compatibilityLevel );
    if ( resultType == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( beanInfo == null ) {
      throw new NullPointerException();
    }

    this.expressionType = expressionType;
    this.layoutProcessorMode = layoutProcessorMode;
    this.resultType = resultType;
    this.properties = new HashMap<String, ExpressionPropertyMetaData>( attributes );
    this.beanInfo = beanInfo;
  }

  public DefaultExpressionMetaData( final ExpressionMetaDataBuilder builder ) {
    super( builder );
    this.expressionType = builder.getImpl();
    this.layoutProcessorMode = builder.getLayoutComputation();
    this.resultType = builder.getResultType();
    this.properties = builder.getProperties();

    this.beanInfo = new SharedBeanInfo( expressionType );
  }

  protected String computePrefix( final String keyPrefix, final String name ) {
    return "";
  }

  public boolean isStatefull() {
    return Function.class.isAssignableFrom( getExpressionType() );
  }

  public Class getResultType() {
    return resultType;
  }

  public Class getExpressionType() {
    return expressionType;
  }

  public ExpressionPropertyMetaData getPropertyDescription( final String name ) {
    return properties.get( name );
  }

  public Expression create() {
    try {
      return expressionType.newInstance();
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public String[] getPropertyNames() {
    if ( propertyKeys == null ) {
      propertyKeys = properties.keySet().toArray( new String[properties.size()] );
    }
    return propertyKeys;
  }

  public ExpressionPropertyMetaData[] getPropertyDescriptions() {
    if ( propertyMetaData == null ) {
      propertyMetaData = properties.values().toArray( new ExpressionPropertyMetaData[properties.size()] );
    }
    return propertyMetaData;
  }

  public BeanInfo getBeanDescriptor() throws IntrospectionException {
    return beanInfo.getBeanInfo();
  }

  /**
   * Checks whether the main purpose of the expression is to modify the layout of the report. This method returns true,
   * if the expression modifies one or more named elements.
   *
   * @return true, if this is a layout-processor that modifies named elements.
   */
  public boolean isElementLayoutProcessor() {
    return layoutProcessorMode == DefaultExpressionMetaData.ELEMENT_LAYOUT_PROCESSOR;
  }

  /**
   * Checks whether the main purpose of the expression is to modify the layout of the report. This method returns true,
   * if the expression modifies the global layout only.
   *
   * @return true, if this is a layout-processor that modifies the global layout.
   */
  public boolean isGlobalLayoutProcessor() {
    return layoutProcessorMode == DefaultExpressionMetaData.GLOBAL_LAYOUT_PROCESSOR;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DefaultExpressionMetaData that = (DefaultExpressionMetaData) o;

    if ( !expressionType.equals( that.expressionType ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return expressionType.hashCode();
  }

  private void writeObject( ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
    out.writeObject( beanInfo.getBeanClass() );
  }

  private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    final Class c = (Class) in.readObject();
    beanInfo = new SharedBeanInfo( c );
  }
}
