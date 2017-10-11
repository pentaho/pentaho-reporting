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
import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.beans.PropertyEditor;

public class DefaultStyleKeyMetaData extends AbstractMetaData implements StyleMetaData {
  private static final Log logger = LogFactory.getLog( DefaultStyleKeyMetaData.class );
  private StyleKey key;
  private Class<? extends PropertyEditor> propertyEditorClass;

  public DefaultStyleKeyMetaData( final StyleKey key, final String propertyEditor, final String bundleLocation,
      final String keyPrefix, final boolean expert, final boolean preferred, final boolean hidden,
      final boolean deprecated, final MaturityLevel maturityLevel, final int compatibilityLevel ) {
    super( key.getName(), bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, maturityLevel,
        compatibilityLevel );
    this.key = key;
    this.propertyEditorClass = validatePropertyEditor( propertyEditor );
  }

  public DefaultStyleKeyMetaData( final StyleMetaDataBuilder builder ) {
    super( builder );
    this.key = builder.getKey();
    this.propertyEditorClass = builder.getPropertyEditor();
  }

  private Class<? extends PropertyEditor> validatePropertyEditor( final String className ) {
    return ObjectUtilities.loadAndValidate( className, DefaultAttributeMetaData.class, PropertyEditor.class );
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

  public Class getTargetType() {
    return key.getValueType();
  }

  public StyleKey getStyleKey() {
    return key;
  }

  @Deprecated
  public String getPropertyEditor() {
    if ( propertyEditorClass == null ) {
      return null;
    }
    return propertyEditorClass.getSimpleName();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DefaultStyleKeyMetaData that = (DefaultStyleKeyMetaData) o;

    if ( !key.equals( that.key ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return key.hashCode();
  }
}
