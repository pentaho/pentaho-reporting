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

package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.beans.PropertyEditor;

public class StyleMetaDataBuilder extends MetaDataBuilder<StyleMetaDataBuilder> {
  private StyleKey key;
  private Class<? extends PropertyEditor> propertyEditor;

  public StyleMetaDataBuilder() {
  }

  public StyleMetaDataBuilder propertyEditor( Class<? extends PropertyEditor> propertyEditor ) {
    this.propertyEditor = propertyEditor;
    return self();
  }

  public StyleMetaDataBuilder key( StyleKey key ) {
    this.key = key;
    return self();
  }

  public String getName() {
    if ( key == null ) {
      return null;
    }
    return key.getName();
  }

  protected StyleMetaDataBuilder self() {
    return this;
  }

  public StyleKey getKey() {
    return key;
  }

  public Class<? extends PropertyEditor> getPropertyEditor() {
    return propertyEditor;
  }
}
