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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A attribute map holding &lt;namspace;name&gt;-value pairs.
 *
 * @author Thomas Morgner
 */
public class AttributeMap<T> implements Serializable, Cloneable
{
  private static final String[] EMPTY_NAMESPACES = new String[0];

  private static final long serialVersionUID = -7442871030874215436L;
  private LinkedHashMap<String, LinkedHashMap<String, T>> namespaces;
  private String singletonNamespace;
  private LinkedHashMap<String, T> singletonContent;

  /**
   * Default constructor.
   */
  public AttributeMap()
  {
  }

  /**
   * Creates a new attibute map using the given parameter as source for the initial values.
   *
   * @param copy the attribute map that should be copied.
   * @noinspection unchecked
   */
  public AttributeMap(final AttributeMap copy)
  {
    if (copy == null)
    {
      return;
    }

    if (copy.singletonNamespace != null)
    {
      singletonNamespace = copy.singletonNamespace;
      singletonContent = (LinkedHashMap<String, T>) copy.singletonContent.clone();
    }

    if (copy.namespaces == null)
    {
      return;
    }

    namespaces = (LinkedHashMap<String, LinkedHashMap<String, T>>) copy.namespaces.clone();
    for (final Map.Entry<String, LinkedHashMap<String, T>> entry : namespaces.entrySet())
    {
      final LinkedHashMap<String, T> value = entry.getValue();
      entry.setValue((LinkedHashMap<String, T>) value.clone());
    }
  }

  /**
   * Creates a copy of this map.
   *
   * @return the clone.
   * @noinspection CloneDoesntDeclareCloneNotSupportedException, unchecked
   */
  public AttributeMap<T> clone()
  {
    try
    {
      final AttributeMap<T> map = (AttributeMap<T>) super.clone();
      if (singletonNamespace != null)
      {
        map.singletonContent = (LinkedHashMap<String, T>) singletonContent.clone();
      }
      if (namespaces != null)
      {
        map.namespaces = (LinkedHashMap<String, LinkedHashMap<String, T>>) namespaces.clone();
        for (final Map.Entry<String, LinkedHashMap<String, T>> entry : map.namespaces.entrySet())
        {
          final LinkedHashMap value = entry.getValue();
          entry.setValue((LinkedHashMap<String, T>) value.clone());
        }
      }
      return map;
    }
    catch (final CloneNotSupportedException cne)
    {
      // ignored
      throw new IllegalStateException("Cannot happen: Clone not supported exception");
    }
  }

  /**
   * Defines the attribute for the given namespace and attribute name.
   *
   * @param namespace the namespace under which the value should be stored.
   * @param attribute the attribute name under which the value should be stored within the namespace.
   * @param value     the value.
   * @return the previously stored value at that position.
   */
  public T setAttribute(final String namespace,
                        final String attribute,
                        final T value)
  {
    if (namespace == null)
    {
      throw new NullPointerException("Attribute namespace must not be null");
    }
    if (attribute == null)
    {
      throw new NullPointerException("Attribute name must not be null");
    }

    if (singletonNamespace == null)
    {
      if (value != null)
      {
        singletonNamespace = namespace;
        singletonContent = new LinkedHashMap<String, T>();
        singletonContent.put(attribute, value);
      }
      return null;
    }
    if (namespace.equals(singletonNamespace))
    {
      if (value == null)
      {
        return singletonContent.remove(attribute);
      }
      else
      {
        return singletonContent.put(attribute, value);
      }
    }

    if (namespaces == null)
    {
      if (value == null)
      {
        return null;
      }

      namespaces = new LinkedHashMap<String, LinkedHashMap<String, T>>();
    }

    final LinkedHashMap<String, T> attrs = namespaces.get(namespace);
    if (attrs == null)
    {
      if (value == null)
      {
        return null;
      }

      final LinkedHashMap<String, T> newAtts = new LinkedHashMap<String, T>();
      newAtts.put(attribute, value);
      namespaces.put(namespace, newAtts);
      return null;
    }
    else
    {
      if (value == null)
      {
        final T retval = attrs.remove(attribute);
        if (attrs.isEmpty())
        {
          namespaces.remove(namespace);
        }
        return retval;
      }
      else
      {
        return attrs.put(attribute, value);
      }
    }
  }

  /**
   * Returns the attribute value for the given namespace and attribute-name.
   *
   * @param namespace the namespace.
   * @param attribute the attribute name.
   * @return the value or null, if there is no such namespace/attribute name combination.
   */
  public T getAttribute(final String namespace,
                        final String attribute)
  {
    if (namespace == null)
    {
      throw new NullPointerException("Attribute namespace must not be null");
    }
    if (attribute == null)
    {
      throw new NullPointerException("Attribute name must not be null");
    }
    if (singletonNamespace == null)
    {
      return null;
    }
    if (namespace.equals(singletonNamespace))
    {
      return singletonContent.get(attribute);
    }

    if (namespaces == null)
    {
      return null;
    }

    final LinkedHashMap<String, T> attrs = namespaces.get(namespace);
    if (attrs == null)
    {
      return null;
    }
    else
    {
      return attrs.get(attribute);
    }
  }

  /**
   * Looks up all namespaces and returns the value from the first namespace that has this attribute defined. As the
   * order of the namespaces is not defined, this returns a random value and the namespace used is undefined if more
   * than one namespace contains the same attribute.
   *
   * @param attribute the the attribute name.
   * @return the object from the first namespace that carries this attribute or null, if none of the namespaces has such
   *         an attribute defined.
   */
  public T getFirstAttribute(final String attribute)
  {
    if (attribute == null)
    {
      throw new NullPointerException("Attribute name must not be null");
    }

    if (singletonContent != null)
    {
      final T val = singletonContent.get(attribute);
      if (val != null)
      {
        return val;
      }
    }

    if (namespaces == null)
    {
      return null;
    }

    for (final Map.Entry<String, LinkedHashMap<String, T>> entry : namespaces.entrySet())
    {
      final LinkedHashMap<String, T> value = entry.getValue();
      final T val = value.get(attribute);
      if (val != null)
      {
        return val;
      }
    }
    return null;
  }

  /**
   * Returns all attributes of the given namespace as unmodifable map.
   *
   * @param namespace the namespace for which the attributes should be returned.
   * @return the map, never null.
   */
  public Map<String, T> getAttributes(final String namespace)
  {
    if (namespace == null)
    {
      throw new NullPointerException("Attribute namespace must not be null");
    }

    if (namespace.equals(singletonNamespace))
    {
      return Collections.unmodifiableMap(singletonContent);
    }

    if (namespaces == null)
    {
      return Collections.emptyMap();
    }

    final LinkedHashMap<String, T> attrs = namespaces.get(namespace);
    if (attrs == null)
    {
      return Collections.emptyMap();
    }
    else
    {
      return Collections.unmodifiableMap(attrs);
    }
  }

  /**
   * Returns all names for the given namespace that have values in this map.
   *
   * @param namespace the namespace for which known attribute names should be looked up.
   * @return the names stored for the given namespace.
   */
  public String[] getNames(final String namespace)
  {
    if (namespace == null)
    {
      throw new NullPointerException("Attribute namespace must not be null");
    }

    if (namespace.equals(singletonNamespace))
    {
      return singletonContent.keySet().toArray(new String[singletonContent.size()]);
    }

    if (namespaces == null)
    {
      return AttributeMap.EMPTY_NAMESPACES;
    }

    final LinkedHashMap<String, T> attrs = namespaces.get(namespace);
    if (attrs == null)
    {
      return AttributeMap.EMPTY_NAMESPACES;
    }
    else
    {
      return attrs.keySet().toArray(new String[attrs.size()]);
    }
  }

  /**
   * Returns all namespaces that have values in this map.
   *
   * @return the namespaces stored in this map.
   */
  public String[] getNameSpaces()
  {
    if (namespaces == null)
    {
      if (singletonContent != null)
      {
        return new String[]{singletonNamespace};
      }
      return AttributeMap.EMPTY_NAMESPACES;
    }
    final String[] strings = namespaces.keySet().toArray(new String[namespaces.size() + 1]);
    System.arraycopy(strings, 0, strings, 1, namespaces.size());
    strings[0] = singletonNamespace;
    return strings;
  }

  public void putAll(final AttributeMap<T> attributeMap)
  {
    final String[] namespaces = attributeMap.getNameSpaces();
    if (namespaces.length == 0)
    {
      return;
    }

    final boolean dontCopySingleton;
    if (this.singletonNamespace == null)
    {
      dontCopySingleton = true;
      this.singletonNamespace = attributeMap.singletonNamespace;
      //noinspection unchecked
      this.singletonContent = (LinkedHashMap<String, T>) attributeMap.singletonContent.clone();
    }
    else
    {
      if (this.singletonNamespace.equals(attributeMap.singletonNamespace))
      {
        dontCopySingleton = true;
        this.singletonContent.putAll(attributeMap.singletonContent);
      }
      else
      {
        dontCopySingleton = false;
      }
    }

    for (int i = 0; i < namespaces.length; i++)
    {
      final String namespace = namespaces[i];
      final Map<String, T> sourceMap = attributeMap.getAttributes(namespace);
      if (dontCopySingleton && singletonNamespace.equals(namespace))
      {
        continue;
      }

      if (this.namespaces == null)
      {
        this.namespaces = new LinkedHashMap<String, LinkedHashMap<String, T>>();
      }
      final LinkedHashMap<String, T> targetMap = this.namespaces.get(namespace);
      if (targetMap == null)
      {
        this.namespaces.put(namespace, new LinkedHashMap<String, T>(sourceMap));
      }
      else
      {
        targetMap.putAll(sourceMap);
      }
    }
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

    final AttributeMap that = (AttributeMap) o;
    if (singletonNamespace != null ? !singletonNamespace.equals(that.singletonNamespace) : that.singletonNamespace != null)
    {
      return false;
    }
    if (singletonContent != null ? !singletonContent.equals(that.singletonContent) : that.singletonContent != null)
    {
      return false;
    }
    if (namespaces != null ? !namespaces.equals(that.namespaces) : that.namespaces != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result = namespaces != null ? namespaces.hashCode() : 0;
    result = 31 * result + (singletonNamespace != null ? singletonNamespace.hashCode() : 0);
    result = 31 * result + (singletonContent != null ? singletonContent.hashCode() : 0);
    return result;
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("AttributeMap");
    sb.append("{namespaces=").append(namespaces);
    sb.append(", singletonNamespace='").append(singletonNamespace).append('\'');
    sb.append(", singletonContent=").append(singletonContent);
    sb.append('}');
    return sb.toString();
  }
}
