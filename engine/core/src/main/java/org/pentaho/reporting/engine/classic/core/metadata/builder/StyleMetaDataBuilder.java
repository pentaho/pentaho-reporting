/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
