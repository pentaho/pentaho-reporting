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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ReportPreProcessorPropertyMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.beans.PropertyEditor;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ReportPreProcessorPropertyReadHandler extends AbstractMetaDataReadHandler {
  private boolean validatePropertiesOnBoot;
  private SharedBeanInfo beanInfo;
  private String bundleLocation;

  private ReportPreProcessorPropertyMetaDataBuilder builder;

  public ReportPreProcessorPropertyReadHandler( final SharedBeanInfo beanInfo, final String bundleLocation ) {
    this.validatePropertiesOnBoot =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.metadata.StrictValidation" ) );
    this.beanInfo = beanInfo;
    this.bundleLocation = bundleLocation;

    this.builder = new ReportPreProcessorPropertyMetaDataBuilder();
  }

  public ReportPreProcessorPropertyMetaDataBuilder getBuilder() {
    return builder;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    getBuilder().mandatory( "true".equals( attrs.getValue( getUri(), "mandatory" ) ) ); // NON-NLS
    getBuilder().computed( "true".equals( attrs.getValue( getUri(), "computed" ) ) ); // NON-NLS
    getBuilder().valueRole( parseValueRole( attrs ) );
    getBuilder().editor( parsePropertyEditor( attrs ) );
    getBuilder().core( parsePropertyCore( attrs ) );
    getBuilder().bundle( getEffectiveBundle(), "property." );
    getBuilder().descriptorFromParent( beanInfo.getBeanClass() );

    if ( validatePropertiesOnBoot ) {
      if ( beanInfo.getPropertyDescriptor( getName() ) == null ) {
        throw new ParseException( "Attribute 'name' with value '" + getName()
            + "' does not reference a valid property. [" + beanInfo + "]", getLocator() );
      }
    }
  }

  public String getEffectiveBundle() {
    if ( getBundle() != null ) {
      return getBundle();
    }
    return bundleLocation;
  }

  private ReportPreProcessorPropertyCore parsePropertyCore( final Attributes attrs ) throws ParseException {
    final ReportPreProcessorPropertyCore core;
    final String metaDataCoreClass = attrs.getValue( getUri(), "impl" ); // NON-NLS
    if ( metaDataCoreClass != null ) {
      core =
          ObjectUtilities.loadAndInstantiate( metaDataCoreClass, ReportPreProcessorPropertyReadHandler.class,
              ReportPreProcessorPropertyCore.class );
      if ( core == null ) {
        throw new ParseException(
            "Attribute 'impl' references a invalid ReportPreProcessorPropertyCore implementation.", getLocator() );
      }
      return core;
    } else {
      return new DefaultReportPreProcessorPropertyCore();
    }
  }

  private Class<? extends PropertyEditor> parsePropertyEditor( final Attributes attrs ) {
    String propertyEditorClass = attrs.getValue( getUri(), "propertyEditor" ); // NON-NLS
    return ObjectUtilities.loadAndValidate( propertyEditorClass, ExpressionPropertyReadHandler.class,
        PropertyEditor.class );
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
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public ReportPreProcessorPropertyMetaData getObject() throws SAXException {
    return new DefaultReportPreProcessorPropertyMetaData( getBuilder() );
  }
}
