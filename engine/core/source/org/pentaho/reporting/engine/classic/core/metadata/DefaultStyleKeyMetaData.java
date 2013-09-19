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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DefaultStyleKeyMetaData extends AbstractMetaData implements StyleMetaData
{
  private StyleKey key;
  private String propertyEditor;

  public DefaultStyleKeyMetaData(final StyleKey key,
                                 final String propertyEditor,
                                 final String bundleLocation,
                                 final String keyPrefix,
                                 final boolean expert,
                                 final boolean preferred,
                                 final boolean hidden,
                                 final boolean deprecated,
                                 final boolean experimental,
                                 final int compatibilityLevel)
  {
    super(key.getName(), bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    this.key = key;
    this.propertyEditor = propertyEditor;
  }

  public PropertyEditor getEditor()
  {
    return ObjectUtilities.loadAndInstantiate(propertyEditor, DefaultStyleKeyMetaData.class, PropertyEditor.class);
  }

  public Class getTargetType()
  {
    return key.getValueType();
  }

  public StyleKey getStyleKey()
  {
    return key;
  }

  public String getPropertyEditor()
  {
    return propertyEditor;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final DefaultStyleKeyMetaData that = (DefaultStyleKeyMetaData) o;

    if (!key.equals(that.key))
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    return key.hashCode();
  }
}
