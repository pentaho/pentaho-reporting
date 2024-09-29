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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.beans.PropertyEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeCore;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.builder.AttributeMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AttributeReadHandler extends AbstractMetaDataReadHandler {
  private static final Log logger = LogFactory.getLog( AttributeReadHandler.class );
  private AttributeMetaDataBuilder builder;
  private String prefix;

  public AttributeReadHandler( final String defaultBundle, final String prefix ) {
    super( defaultBundle );
    this.prefix = prefix;
    this.builder = new AttributeMetaDataBuilder();
  }

  public AttributeMetaDataBuilder getBuilder() {
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
    getBuilder().namespace( parseNamespace( attrs ) );
    getBuilder().namespacePrefix( parseNamespacePrefix( attrs ) );
    getBuilder().mandatory( "true".equals( attrs.getValue( getUri(), "mandatory" ) ) ); // NON-NLS
    getBuilder().computed( "true".equals( attrs.getValue( getUri(), "computed" ) ) ); // NON-NLS
    getBuilder().transientFlag( "true".equals( attrs.getValue( getUri(), "transient" ) ) ); // NON-NLS
    getBuilder().bulk( "true".equals( attrs.getValue( getUri(), "prefer-bulk" ) ) ); // NON-NLS
    getBuilder().designTime( "true".equals( attrs.getValue( getUri(), "design-time-value" ) ) ); // NON-NLS
    getBuilder().targetClass( parseValueType( attrs ) );
    getBuilder().valueRole( parseValueRole( attrs ) );
    getBuilder().propertyEditor(
        ObjectUtilities.loadAndValidate( attrs.getValue( getUri(), "propertyEditor" ), AttributeReadHandler.class,
            PropertyEditor.class ) ); // NON-NLS
    getBuilder().core( parseAttributeCore( attrs ) );
    getBuilder().bundle( getBundle(), computePrefix() );
  }

  private String computePrefix() {
    final String namespace = getNamespace();
    final String attrName = getName();
    final String namespacePrefix = ElementTypeRegistry.getInstance().getNamespacePrefix( namespace );
    if ( namespacePrefix == null ) {
      logger.warn( "Invalid namespace-prefix, skipping attribute " + namespace + ':' + attrName ); // NON-NLS
      return null;
    }

    return prefix + "attribute." + namespacePrefix + '.'; // NON-NLS
  }

  private AttributeCore parseAttributeCore( final Attributes attrs ) throws ParseException {
    final AttributeCore attributeCore;
    final String metaDataCoreClass = attrs.getValue( getUri(), "impl" ); // NON-NLS
    if ( metaDataCoreClass != null ) {
      attributeCore =
          ObjectUtilities.loadAndInstantiate( metaDataCoreClass, AttributeReadHandler.class, AttributeCore.class );
      if ( attributeCore == null ) {
        throw new ParseException( "Attribute 'impl' references a invalid AttributeCore implementation.", getLocator() );
      }
    } else {
      attributeCore = new DefaultAttributeCore();
    }
    return attributeCore;
  }

  private String parseValueRole( final Attributes attrs ) {
    String valueRole = attrs.getValue( getUri(), "value-role" ); // NON-NLS
    if ( valueRole == null ) {
      valueRole = "Value"; // NON-NLS
    }
    return valueRole;
  }

  private Class<?> parseValueType( final Attributes attrs ) throws ParseException {
    final String valueTypeText = attrs.getValue( getUri(), "value-type" ); // NON-NLS
    if ( valueTypeText == null ) {
      throw new ParseException( "Attribute 'value-type' is undefined", getLocator() );
    }
    try {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
      return Class.forName( valueTypeText, false, classLoader );
    } catch ( final Exception e ) {
      throw new ParseException( "Attribute 'value-type' is not valid", e, getLocator() );
    }
  }

  private String parseNamespace( final Attributes attrs ) throws ParseException {
    String namespace = attrs.getValue( getUri(), "namespace" );
    if ( namespace == null ) {
      throw new ParseException( "Attribute 'namespace' is undefined", getLocator() );
    }
    return namespace;
  }

  private String parseNamespacePrefix( final Attributes attrs ) throws ParseException {
    String namespacePrefix = attrs.getValue( getUri(), "namespace-prefix" ); // NON-NLS
    if ( namespacePrefix == null ) {
      namespacePrefix = ElementTypeRegistry.getInstance().getNamespacePrefix( parseNamespace( attrs ) );
    }
    return namespacePrefix;
  }

  public AttributeCore getAttributeCore() {
    return getBuilder().getCore();
  }

  public String getPropertyEditor() {
    return getBuilder().getPropertyEditor().getName();
  }

  public String getNamespace() {
    return getBuilder().getNamespace();
  }

  public boolean isMandatory() {
    return getBuilder().isMandatory();
  }

  public boolean isComputed() {
    return getBuilder().isComputed();
  }

  public boolean isTransient() {
    return getBuilder().isTransientFlag();
  }

  public Class<?> getValueType() {
    return getBuilder().getTargetClass();
  }

  public boolean isBulk() {
    return getBuilder().isBulk();
  }

  public String getValueRole() {
    return getBuilder().getValueRole();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public AttributeDefinition getObject() throws SAXException {
    return new AttributeDefinition( getBuilder() );
  }

  public AttributeMetaData getMetaData() {
    return new DefaultAttributeMetaData( getBuilder() );
  }
}
