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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ExpressionPropertyMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.SharedPropertyDescriptorProxy;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertyWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.UserDefinedExpressionPropertyReadHandler;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

public class DefaultExpressionPropertyMetaData extends AbstractMetaData implements ExpressionPropertyMetaData {
  private static final Log logger = LogFactory.getLog( DefaultExpressionPropertyMetaData.class );

  private boolean mandatory;
  private String propertyRole;
  private Class<? extends PropertyEditor> propertyEditorClass;
  private boolean computed;
  private ExpressionPropertyCore expressionPropertyCore;
  private SharedPropertyDescriptorProxy propertyDescriptor;
  private Class<? extends UserDefinedExpressionPropertyReadHandler> propertyReadHandler;
  private Class<? extends ExpressionPropertyWriteHandler> propertyWriteHandler;
  private boolean designTimeProperty;

  @Deprecated
  public DefaultExpressionPropertyMetaData( final String name, final String bundleLocation, final boolean expert,
      final boolean preferred, final boolean hidden, final boolean deprecated, final boolean mandatory,
      final boolean computed, final String propertyRole, final SharedBeanInfo beanInfo,
      final String propertyEditorClass, final ExpressionPropertyCore expressionPropertyCore,
      final MaturityLevel maturityLevel, final int compatibilityLevel, boolean  designTimeProperty ) {
    super( name, bundleLocation, "property.", expert, preferred, hidden, deprecated, maturityLevel, compatibilityLevel );
    ArgumentNullException.validate( "propertyRole", propertyRole );
    ArgumentNullException.validate( "beanInfo", beanInfo );
    ArgumentNullException.validate( "expressionPropertyCore", expressionPropertyCore );

    this.propertyDescriptor = new SharedPropertyDescriptorProxy( beanInfo, name );
    this.computed = computed;
    this.expressionPropertyCore = expressionPropertyCore;
    this.propertyEditorClass =
        ObjectUtilities.loadAndValidate( propertyEditorClass, DefaultExpressionPropertyMetaData.class,
            PropertyEditor.class );
    this.mandatory = mandatory;
    this.propertyRole = propertyRole;
    this.designTimeProperty = designTimeProperty;
  }

  public DefaultExpressionPropertyMetaData( final ExpressionPropertyMetaDataBuilder builder ) {
    super( builder );
    this.propertyDescriptor = builder.getDescriptor();
    this.computed = builder.isComputed();
    this.expressionPropertyCore = builder.getCore();
    this.propertyEditorClass = builder.getEditor();
    this.mandatory = builder.isMandatory();
    this.propertyRole = builder.getValueRole();
    this.propertyReadHandler = builder.getPropertyReadHandler();
    this.propertyWriteHandler = builder.getPropertyWriteHandler();
    this.designTimeProperty = builder.isDesignTime();

    ArgumentNullException.validate( "propertyRole", propertyRole );
    ArgumentNullException.validate( "propertyDescriptor", propertyDescriptor );
    ArgumentNullException.validate( "expressionPropertyCore", expressionPropertyCore );
  }

  public boolean isComputed() {
    return computed;
  }

  public Class<?> getPropertyType() {
    return getBeanDescriptor().getPropertyType();
  }

  public String getPropertyRole() {
    return propertyRole;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public String[] getReferencedFields( final Expression element, final Object attributeValue ) {
    return expressionPropertyCore.getReferencedFields( this, element, attributeValue );
  }

  public String[] getReferencedGroups( final Expression element, final Object attributeValue ) {
    return expressionPropertyCore.getReferencedGroups( this, element, attributeValue );
  }

  public String[] getReferencedElements( final Expression expression, final Object attributeValue ) {
    return expressionPropertyCore.getReferencedElements( this, expression, attributeValue );
  }

  public ResourceReference[] getReferencedResources( final Expression expression, final Object attributeValue,
      final Element reportElement, final ResourceManager resourceManager ) {
    return expressionPropertyCore.getReferencedResources( this, expression, attributeValue, reportElement,
        resourceManager );
  }

  public PropertyDescriptor getBeanDescriptor() throws IllegalStateException {
    return propertyDescriptor.get();
  }

  public PropertyEditor getEditor() {
    if ( propertyEditorClass == null ) {
      return null;
    }
    try {
      return propertyEditorClass.newInstance();
    } catch ( Exception e ) {
      logger.warn( "Property editor for expression property '" + getName() + "' threw an Exception on instantiate", e );
      return null;
    }
  }

  public String[] getExtraCalculationFields() {
    return expressionPropertyCore.getExtraCalculationFields( this );
  }

  @Override
  public Class<? extends UserDefinedExpressionPropertyReadHandler> getPropertyReadHandler() {
    return propertyReadHandler;
  }

  @Override
  public Class<? extends ExpressionPropertyWriteHandler> getPropertyWriteHandler() {
    return propertyWriteHandler;
  }

  public boolean isDesignTimeProperty() {
    return designTimeProperty;
  }
}
