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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class DefaultElementMetaData extends AbstractMetaData implements ElementMetaData
{
  private transient AttributeMetaData[] attributesAsArray;
  private AttributeMap attributes;
  private HashMap<StyleKey, StyleMetaData> styles;
  private Class elementType;
  private TypeClassification reportElementType;
  private Class contentType;
  private transient StyleMetaData[] stylesArray;

  public DefaultElementMetaData(final String name,
                                final String bundleLocation,
                                final String keyPrefix,
                                final boolean expert,
                                final boolean preferred,
                                final boolean hidden,
                                final boolean deprecated,
                                final TypeClassification reportElementType,
                                final AttributeMap attributes,
                                final HashMap<StyleKey, StyleMetaData> styles,
                                final Class elementType,
                                final Class contentType,
                                final boolean experimental,
                                final int compatibilityLevel)
  {
    super(name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    if (styles == null)
    {
      throw new NullPointerException();
    }
    if (attributes == null)
    {
      throw new NullPointerException();
    }
    if (elementType == null)
    {
      throw new NullPointerException();
    }
    if (contentType == null)
    {
      throw new NullPointerException();
    }

    this.contentType = contentType;
    this.reportElementType = reportElementType;
    this.attributes = attributes.clone();
    this.styles = (HashMap<StyleKey, StyleMetaData>) styles.clone();
    this.elementType = elementType;
  }

  public AttributeMetaData[] getAttributeDescriptions()
  {
    if (attributesAsArray == null)
    {
      final ArrayList<AttributeMetaData> buffer = new ArrayList<AttributeMetaData>();
      final String[] namespaces = attributes.getNameSpaces();
      for (int i = 0; i < namespaces.length; i++)
      {
        final String namespace = namespaces[i];
        final Map attrsNs = attributes.getAttributes(namespace);
        final Iterator it = attrsNs.entrySet().iterator();
        while (it.hasNext())
        {
          final Map.Entry entry = (Map.Entry) it.next();
          final AttributeMetaData exp = (AttributeMetaData) entry.getValue();
          buffer.add(exp);
        }
      }
      attributesAsArray = buffer.toArray(new AttributeMetaData[buffer.size()]);
    }
    return attributesAsArray.clone();
  }

  public StyleMetaData[] getStyleDescriptions()
  {
    if (stylesArray == null)
    {
      stylesArray = styles.values().toArray(new StyleMetaData[styles.size()]);
    }
    return stylesArray;
  }

  public AttributeMetaData getAttributeDescription(final String namespace, final String name)
  {
    if (namespace == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }

    return (AttributeMetaData) attributes.getAttribute(namespace, name);
  }

  public StyleMetaData getStyleDescription(final StyleKey name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }

    return styles.get(name);
  }

  public ElementType create() throws InstantiationException
  {
    try
    {
      return (ElementType) elementType.newInstance();
    }
    catch (IllegalAccessException e)
    {
      throw new InstantiationException("Unable to instantiate " + elementType + ": IllegalAccessException caught");
    }
  }

  public boolean isContainerElement()
  {
    return reportElementType != TypeClassification.DATA;
  }

  public TypeClassification getReportElementType()
  {
    return reportElementType;
  }

  public Class getContentType()
  {
    return contentType;
  }
}
