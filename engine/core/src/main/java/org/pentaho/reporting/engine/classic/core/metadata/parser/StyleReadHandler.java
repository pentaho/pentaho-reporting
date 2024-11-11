/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultStyleKeyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.beans.PropertyEditor;

/**
 * @noinspection HardCodedStringLiteral
 */
public class StyleReadHandler extends AbstractMetaDataReadHandler {
  private String defaultBundleName;
  private StyleMetaDataBuilder builder;

  public StyleReadHandler( final String bundleName ) {
    this.defaultBundleName = bundleName;
    this.builder = new StyleMetaDataBuilder();
  }

  public StyleMetaDataBuilder getBuilder() {
    return builder;
  }

  protected boolean isDerivedName() {
    return true;
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

    getBuilder().key( StyleKey.getStyleKey( parseName( attrs ) ) );
    getBuilder().propertyEditor(
        ObjectUtilities.loadAndValidate( attrs.getValue( getUri(), "propertyEditor" ), AttributeReadHandler.class,
            PropertyEditor.class ) ); // NON-NLS

    if ( getBundle() != null ) {
      getBuilder().bundle( getBundle(), "style." );
    } else {
      getBuilder().bundle( getDefaultBundleName(), "style." );
    }
  }

  @Deprecated
  public boolean isMandatory() {
    return false;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return this;
  }

  @Deprecated
  public String getBundleName() {
    return getDefaultBundleName();
  }

  public String getDefaultBundleName() {
    return defaultBundleName;
  }

  public StyleMetaData getMetaData() {
    return new DefaultStyleKeyMetaData( getBuilder() );
  }
}
