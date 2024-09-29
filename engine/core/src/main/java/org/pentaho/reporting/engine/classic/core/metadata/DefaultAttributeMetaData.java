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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.builder.AttributeMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.beans.PropertyEditor;

public class DefaultAttributeMetaData extends AbstractMetaData implements AttributeMetaData {
  private static final Log logger = LogFactory.getLog( DefaultAttributeMetaData.class );

  private String valueRole;
  private boolean bulk;
  private String namespace;
  private Class<?> targetClass;
  private boolean mandatory;

  /**
   * Indicates if the value is computed. A computed value does not have to be retained since it can be re-computed.
   * NOTE: an Element that is computed may or may not be transient.
   */
  private boolean computed;

  /**
   * Indicates if the value is transient. These values will not be written when the Element is serialized. NOTE: an
   * Element that is computed may or may not be transient.
   */
  private boolean transientFlag;

  private Class<? extends PropertyEditor> propertyEditorClass;
  private boolean designTimeValue;
  private AttributeCore attributeCore;

  /**
   * Creates an expert, non-preferred, hidden, non-mandatory, non-computed and non-transient attribute.
   * <p/>
   * This is a suitable constructor to declare internal attributes that should not be edited by an end-user in the
   * report-designer. This constructor is only used for testing.
   *
   * @param namespace
   * @param name
   * @param bundleLocation
   * @param keyPrefix
   * @param targetClass
   * @param designTimeValue
   * @param compatibilityLevel
   */
  public DefaultAttributeMetaData( final String namespace, final String name, final String bundleLocation,
      final String keyPrefix, final Class<?> targetClass, final boolean designTimeValue, final int compatibilityLevel ) {
    this( namespace, name, bundleLocation, keyPrefix, null, targetClass, true, false, true, false, false, false, false,
        VALUEROLE_VALUE, false, designTimeValue, new DefaultAttributeCore(), MaturityLevel.Production,
        compatibilityLevel );
  }

  public DefaultAttributeMetaData( final AttributeMetaDataBuilder builder ) {
    super( builder );

    this.attributeCore = builder.getCore();
    this.propertyEditorClass = builder.getPropertyEditor();
    this.namespace = builder.getNamespace();
    this.targetClass = builder.getTargetClass();
    this.mandatory = builder.isMandatory();
    this.computed = builder.isComputed();
    this.transientFlag = builder.isTransientFlag();
    this.valueRole = builder.getValueRole();
    this.bulk = builder.isBulk();
    this.designTimeValue = builder.isDesignTime();

    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( attributeCore == null ) {
      throw new NullPointerException();
    }
    if ( targetClass == null ) {
      throw new NullPointerException();
    }
  }

  public DefaultAttributeMetaData( final String namespace, final String name, final String bundleLocation,
      final String keyPrefix, final String propertyEditorClass, final Class<?> targetClass, final boolean expert,
      final boolean preferred, final boolean hidden, final boolean deprecated, final boolean mandatory,
      final boolean computed, final boolean transientFlag, final String valueRole, final boolean bulk,
      final boolean designTimeValue, final AttributeCore attributeCore, final MaturityLevel maturityLevel,
      final int compatibilityLevel ) {
    super( name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, maturityLevel, compatibilityLevel );
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( attributeCore == null ) {
      throw new NullPointerException();
    }
    if ( targetClass == null ) {
      throw new NullPointerException();
    }

    this.attributeCore = attributeCore;
    this.propertyEditorClass = validatePropertyEditor( propertyEditorClass );
    this.namespace = namespace;
    this.targetClass = targetClass;
    this.mandatory = mandatory;
    this.computed = computed;
    this.transientFlag = transientFlag;
    this.valueRole = valueRole;
    this.bulk = bulk;
    this.designTimeValue = designTimeValue;
  }

  private Class<? extends PropertyEditor> validatePropertyEditor( final String className ) {
    return ObjectUtilities.loadAndValidate( className, DefaultAttributeMetaData.class, PropertyEditor.class );
  }

  /**
   * Can be one of "Value", "Resource", "Content", "Field", "Group", "Query", "Message", "Bundle-Key", "Bundle-Name",
   * "Name", "ElementName",
   *
   * @return
   */
  public String getValueRole() {
    return valueRole;
  }

  public boolean isDesignTimeValue() {
    return designTimeValue;
  }

  public boolean isBulk() {
    return bulk;
  }

  public boolean isComputed() {
    return computed;
  }

  public boolean isTransient() {
    return transientFlag;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public String getNameSpace() {
    return namespace;
  }

  public Class<?> getTargetType() {
    return targetClass;
  }

  public PropertyEditor getEditor() {
    if ( propertyEditorClass == null ) {
      return null;
    }
    try {
      return propertyEditorClass.newInstance();
    } catch ( Exception e ) {
      logger.warn( "Property editor threw error on instantiation", e );
      return null;
    }
  }

  public String[] getReferencedFields( final ReportElement element, final Object attributeValue ) {
    return attributeCore.getReferencedFields( this, element, attributeValue );
  }

  public String[] getReferencedGroups( final ReportElement element, final Object attributeValue ) {
    return attributeCore.getReferencedGroups( this, element, attributeValue );
  }

  public ResourceReference[] getReferencedResources( final ReportElement element,
      final ResourceManager resourceManager, final Object attributeValue ) {
    return attributeCore.getReferencedResources( this, element, resourceManager, attributeValue );
  }

  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData{" + "valueRole='" + valueRole
        + '\'' + ", namespace='" + namespace + '\'' + ", name='" + getName() + '\'' + ", targetType=" + targetClass
        + ", mandatory=" + mandatory + ", computed=" + computed + ", transient=" + transientFlag + ", editor="
        + getEditor() + '}';
  }

  public String[] getExtraCalculationFields() {
    return attributeCore.getExtraCalculationFields( this );
  }
}
