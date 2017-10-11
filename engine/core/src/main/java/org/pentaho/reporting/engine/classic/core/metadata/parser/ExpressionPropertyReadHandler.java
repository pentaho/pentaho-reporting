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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ExpressionPropertyMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertyWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.UserDefinedExpressionPropertyReadHandler;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ExpressionPropertyReadHandler extends AbstractMetaDataReadHandler {
  private SharedBeanInfo beanInfo;
  private String bundleLocation;
  private boolean validatePropertiesOnBoot;
  private ExpressionPropertyMetaDataBuilder builder;

  public ExpressionPropertyReadHandler( final SharedBeanInfo beanInfo, final String bundleLocation ) {
    this.validatePropertiesOnBoot =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.metadata.StrictValidation" ) );
    this.beanInfo = beanInfo;
    this.bundleLocation = bundleLocation;

    this.builder = new ExpressionPropertyMetaDataBuilder();
  }

  public ExpressionPropertyMetaDataBuilder getBuilder() {
    return builder;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    getBuilder().mandatory( "true".equals( attrs.getValue( getUri(), "mandatory" ) ) ); // NON-NLS
    getBuilder().computed( "true".equals( attrs.getValue( getUri(), "computed" ) ) ); // NON-NLS
    getBuilder().designTime( "true".equals( attrs.getValue( getUri(), "designTimeProperty" ) ) ); // NON-NLS
    getBuilder().valueRole( parseValueRole( attrs ) );
    getBuilder().editor( parsePropertyEditor( attrs ) );
    getBuilder().readHandler( parseReadHandler( attrs ) );
    getBuilder().writeHandler( parseWriteHandler( attrs ) );
    getBuilder().core( parsePropertyCore( attrs ) );
    getBuilder().bundle( getEffectiveBundle(), "property." );
    getBuilder().descriptorFromParent( beanInfo.getBeanClass() );

    if ( validatePropertiesOnBoot ) {
      if ( beanInfo.getPropertyDescriptor( getName() ) == null ) {
        throw new ParseException( "Attribute 'name' with value '" + getName()
            + "' does not reference a valid property. [" + beanInfo.getBeanClass() + "]", getLocator() );
      }
    }
  }

  private ExpressionPropertyCore parsePropertyCore( final Attributes attrs ) throws ParseException {
    final ExpressionPropertyCore expressionPropertyCore;
    final String metaDataCoreClass = attrs.getValue( getUri(), "impl" ); // NON-NLS
    if ( metaDataCoreClass != null ) {
      expressionPropertyCore =
          ObjectUtilities.loadAndInstantiate( metaDataCoreClass, ExpressionPropertyReadHandler.class,
              ExpressionPropertyCore.class );
      if ( expressionPropertyCore == null ) {
        throw new ParseException( "Attribute 'impl' references a invalid ExpressionPropertyCore implementation.",
            getLocator() );
      }
    } else {
      expressionPropertyCore = new DefaultExpressionPropertyCore();
    }
    return expressionPropertyCore;
  }

  private Class<? extends PropertyEditor> parsePropertyEditor( final Attributes attrs ) {
    String propertyEditorClass = attrs.getValue( getUri(), "propertyEditor" ); // NON-NLS
    return ObjectUtilities.loadAndValidate( propertyEditorClass, ExpressionPropertyReadHandler.class,
        PropertyEditor.class );
  }

  private Class<? extends UserDefinedExpressionPropertyReadHandler> parseReadHandler( final Attributes attrs ) {
    String propertyEditorClass = attrs.getValue( getUri(), "readHandler" ); // NON-NLS
    return ObjectUtilities.loadAndValidate( propertyEditorClass, ExpressionPropertyReadHandler.class,
        UserDefinedExpressionPropertyReadHandler.class );
  }

  private Class<? extends ExpressionPropertyWriteHandler> parseWriteHandler( final Attributes attrs ) {
    String propertyEditorClass = attrs.getValue( getUri(), "writeHandler" ); // NON-NLS
    return ObjectUtilities.loadAndValidate( propertyEditorClass, ExpressionPropertyReadHandler.class,
        ExpressionPropertyWriteHandler.class );
  }

  private String parseValueRole( final Attributes attrs ) {
    String valueRole = attrs.getValue( getUri(), "value-role" ); // NON-NLS
    if ( valueRole == null ) {
      valueRole = "Value"; // NON-NLS
    }
    return valueRole;
  }

  public boolean isMandatory() {
    return getBuilder().isMandatory();
  }

  public String getValueRole() {
    return getBuilder().getValueRole();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occurred.
   */
  public ExpressionPropertyMetaData getObject() throws SAXException {
    return new DefaultExpressionPropertyMetaData( getBuilder() );
  }

  public String getEffectiveBundle() {
    if ( getBundle() != null ) {
      return getBundle();
    }
    return bundleLocation;
  }
}
