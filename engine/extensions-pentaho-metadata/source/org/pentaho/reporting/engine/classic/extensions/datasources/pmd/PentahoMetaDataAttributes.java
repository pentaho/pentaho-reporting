/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.AggregationConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.AlignmentConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.BooleanConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.ColorConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.ColumnWidthConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.DataTypeConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.DateConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.FieldTypeConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.FontSettingsConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.LocalizedStringConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.NumberConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.SecurityConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.StringConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.TableTypeConceptMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types.URLConceptMapper;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class PentahoMetaDataAttributes implements DataAttributes
{
  private static final String[] NAMESPACES = new String[]
      {PmdDataFactoryModule.META_DOMAIN};

  private DataAttributes backend;
  private IConcept concept;
  private ArrayList<ConceptQueryMapper> conceptMappers;
  private LinkedHashMap<String, Object> properties;

  public PentahoMetaDataAttributes(final DataAttributes backend,
                                   final IConcept concept)
  {
    if (concept == null)
    {
      throw new NullPointerException();
    }
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.concept = concept;
    this.properties = new LinkedHashMap<String, Object>(concept.getProperties());

    this.backend = backend;
    this.conceptMappers = new ArrayList<ConceptQueryMapper>();
    this.conceptMappers.add(new AggregationConceptMapper());
    this.conceptMappers.add(new AlignmentConceptMapper());
    this.conceptMappers.add(new BooleanConceptMapper());
    this.conceptMappers.add(new ColorConceptMapper());
    this.conceptMappers.add(new ColumnWidthConceptMapper());
    this.conceptMappers.add(new DataTypeConceptMapper());
    this.conceptMappers.add(new DateConceptMapper());
    this.conceptMappers.add(new FieldTypeConceptMapper());
    this.conceptMappers.add(new FontSettingsConceptMapper());
    this.conceptMappers.add(new NumberConceptMapper());
    this.conceptMappers.add(new LocalizedStringConceptMapper());
    this.conceptMappers.add(new TableTypeConceptMapper());
    this.conceptMappers.add(new URLConceptMapper());
    this.conceptMappers.add(new StringConceptMapper());
    this.conceptMappers.add(new SecurityConceptMapper());
  }

  public String[] getMetaAttributeDomains()
  {
    final String[] backendDomains = backend.getMetaAttributeDomains();
    return StringUtils.merge(NAMESPACES, backendDomains);
  }

  public String[] getMetaAttributeNames(final String domainName)
  {
    if (PmdDataFactoryModule.META_DOMAIN.equals(domainName))
    {
      return properties.keySet().toArray(new String[properties.size()]);
    }

    return backend.getMetaAttributeNames(domainName);
  }

  public Object getMetaAttribute(final String domain, final String name,
                                 final Class type, final DataAttributeContext context)
  {
    return getMetaAttribute(domain, name, type, context, null);
  }

  public Object getMetaAttribute(final String domain, final String name,
                                 final Class type, final DataAttributeContext context,
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

    if (PmdDataFactoryModule.META_DOMAIN.equals(domain))
    {
      final Object value = concept.getProperty(name);
      if (value == null)
      {
        return defaultValue;
      }

      // this metadata layer is not cloneable, so malicious client code may
      // modify metadata objects.
      // We cant solve the problem here, so all we can do is hope and pray.
      return convertFromPmd(value, type, defaultValue, context);
    }

    return backend.getMetaAttribute(domain, name, type, context, defaultValue);
  }

  private Object convertFromPmd(final Object attribute, final Class type,
                                final Object defaultValue, final DataAttributeContext context)
  {
    if (attribute == null)
    {
      return defaultValue;
    }

    for (int i = 0; i < conceptMappers.size(); i++)
    {
      final ConceptQueryMapper conceptMapper = conceptMappers.get(i);
      final Object value = conceptMapper.getValue(attribute, type, context);
      if (value != null)
      {
        return value;
      }
    }

    if (type == null)
    {
      return attribute;
    }

    if (type.isInstance(attribute))
    {
      return attribute;
    }
    return defaultValue;
  }

  public ConceptQueryMapper getMetaAttributeMapper(final String domain, final String name)
  {

    if (PmdDataFactoryModule.META_DOMAIN.equals(domain))
    {
      final Object value = properties.get(name);
      if (value == null)
      {
        return DefaultConceptQueryMapper.INSTANCE;
      }

      // this metadata layer is not cloneable, so malicious client code may
      // modify metadata objects.
      // We cant solve the problem here, so all we can do is hope and pray.

      final DataAttributeContext context = new DefaultDataAttributeContext();
      for (int i = 0; i < conceptMappers.size(); i++)
      {
        final ConceptQueryMapper conceptMapper = conceptMappers.get(i);
        final Object ivalue = conceptMapper.getValue(value, null, context);
        if (ivalue != null)
        {
          return conceptMapper;
        }
      }
    }

    return DefaultConceptQueryMapper.INSTANCE;

  }

  public Object clone() throws CloneNotSupportedException
  {
    final PentahoMetaDataAttributes attributes = (PentahoMetaDataAttributes) super.clone();
    attributes.backend = (DataAttributes) backend.clone();
    attributes.concept = (IConcept) concept.clone();
    attributes.properties = (LinkedHashMap<String, Object>) properties.clone();
    attributes.conceptMappers = (ArrayList<ConceptQueryMapper>) conceptMappers.clone();
    return attributes;
  }
}
