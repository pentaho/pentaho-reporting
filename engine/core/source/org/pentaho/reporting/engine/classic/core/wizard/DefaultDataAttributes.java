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

package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class DefaultDataAttributes implements DataAttributes
{
  private AttributeMap<Object> valueBackend;
  private AttributeMap<ConceptQueryMapper> mapperBackend;

  public DefaultDataAttributes()
  {
    this.valueBackend = new AttributeMap<Object>();
    this.mapperBackend = new AttributeMap<ConceptQueryMapper>();
  }

  public void setMetaAttribute(final String domain,
                               final String name,
                               final ConceptQueryMapper conceptMapper,
                               final Object value)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    if (domain == null)
    {
      throw new NullPointerException();
    }
    valueBackend.setAttribute(domain, name, value);
    mapperBackend.setAttribute(domain, name, conceptMapper);
  }

  public String[] getMetaAttributeDomains()
  {
    return valueBackend.getNameSpaces();
  }

  public String[] getMetaAttributeNames(final String domainName)
  {
    if (domainName == null)
    {
      throw new NullPointerException();
    }

    return valueBackend.getNames(domainName);
  }

  public Object getMetaAttribute(final String domain,
                                 final String name,
                                 final Class type,
                                 final DataAttributeContext context)
  {
    if (domain == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    if (context == null)
    {
      throw new NullPointerException();
    }
    return getMetaAttribute(domain, name, type, context, null);
  }

  public Object getMetaAttribute(final String domain,
                                 final String name,
                                 final Class type,
                                 final DataAttributeContext context,
                                 final Object defaultValue)
  {
    if (domain == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    if (context == null)
    {
      throw new NullPointerException();
    }

    final Object attribute = valueBackend.getAttribute(domain, name);
    if (attribute == null)
    {
      return defaultValue;
    }

    final ConceptQueryMapper mapper = getMetaAttributeMapper(domain, name);
    return mapper.getValue(attribute, type, context);
  }

  public ConceptQueryMapper getMetaAttributeMapper(final String domain, final String name)
  {
    if (domain == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    final ConceptQueryMapper attribute = mapperBackend.getAttribute(domain, name);
    if (attribute == null)
    {
      return DefaultConceptQueryMapper.INSTANCE;
    }

    return attribute;
  }

  public void merge(final DataAttributes attributes,
                    final DataAttributeContext context)
  {
    if (attributes == null)
    {
      throw new NullPointerException();
    }
    if (context == null)
    {
      throw new NullPointerException();
    }

    final String[] domains = attributes.getMetaAttributeDomains();
    for (int i = 0; i < domains.length; i++)
    {
      final String domain = domains[i];
      final String[] names = attributes.getMetaAttributeNames(domain);
      for (int j = 0; j < names.length; j++)
      {
        final String name = names[j];
        final Object value = attributes.getMetaAttribute(domain, name, null, context);
        if (value != null)
        {
          valueBackend.setAttribute(domain, name, value);
          mapperBackend.setAttribute(domain, name, attributes.getMetaAttributeMapper(domain, name));
        }
      }
    }
  }

  public void mergeReferences(final DataAttributeReferences references,
                              final DataAttributeContext context)
  {
    if (references == null)
    {
      throw new NullPointerException();
    }
    if (context == null)
    {
      throw new NullPointerException();
    }
    final String[] domains = references.getMetaAttributeDomains();
    for (int i = 0; i < domains.length; i++)
    {
      final String domain = domains[i];
      final String[] names = references.getMetaAttributeNames(domain);
      for (int j = 0; j < names.length; j++)
      {
        final String name = names[j];
        final DataAttributeReference ref = references.getReference(domain, name);
        final Object value = ref.resolve(this, context);
        if (value != null)
        {
          valueBackend.setAttribute(domain, name, value);
          mapperBackend.setAttribute(domain, name, ref.resolveMapper(this));
        }
      }
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final DefaultDataAttributes o = (DefaultDataAttributes) super.clone();
    o.valueBackend = (AttributeMap<Object>) valueBackend.clone();
    o.mapperBackend = (AttributeMap<ConceptQueryMapper>) mapperBackend.clone();
    return o;
  }


  public boolean isEmpty()
  {
    return valueBackend.getNameSpaces().length == 0;
  }

}
