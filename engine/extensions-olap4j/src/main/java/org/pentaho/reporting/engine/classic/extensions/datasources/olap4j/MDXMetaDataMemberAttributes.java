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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.OlapException;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Property;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types.LocalizedString;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types.LocalizedStringConceptMapper;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class MDXMetaDataMemberAttributes implements DataAttributes {
  private static final String[] NAMESPACES = new String[] { MDXMetaAttributeNames.NAMESPACE };
  private static final String[] PROPERTIES = new String[]
    {

      "CATALOG_NAME",
      "SCHEMA_NAME",
      "CUBE_NAME",
      "DIMENSION_UNIQUE_NAME",
      "HIERARCHY_UNIQUE_NAME",
      "LEVEL_UNIQUE_NAME",
      "LEVEL_NUMBER",
      "MEMBER_ORDINAL",
      "MEMBER_NAME",
      "MEMBER_UNIQUE_NAME",
      "MEMBER_TYPE",
      "MEMBER_GUID",
      "MEMBER_CAPTION",
      "CHILDREN_CARDINALITY",
      "PARENT_LEVEL",
      "PARENT_UNIQUE_NAME",
      "PARENT_COUNT",
      "DESCRIPTION",
      "$visible",
      "MEMBER_KEY",
      "IS_PLACEHOLDERMEMBER",
      "IS_DATAMEMBER",
      "DEPTH",
      "DISPLAY_INFO",
      "VALUE",

      MDXMetaAttributeNames.BACKGROUND_COLOR,
      MDXMetaAttributeNames.FOREGROUND_COLOR,

      MDXMetaAttributeNames.FONT_FLAGS,
      MDXMetaAttributeNames.FONTSIZE,

      MDXMetaAttributeNames.FONTNAME,
      MDXMetaAttributeNames.FORMAT_STRING,
      MDXMetaAttributeNames.LANGUAGE,

      MDXMetaAttributeNames.MDX_ALL_MEMBER,
      MDXMetaAttributeNames.MDX_CALCULATED,
      MDXMetaAttributeNames.MDX_HIDDEN,
      MDXMetaAttributeNames.MDX_CAPTION,
      MDXMetaAttributeNames.MDX_DESCRIPTION,
    };

  private DataAttributes backend;
  private Member cell;

  public MDXMetaDataMemberAttributes( final DataAttributes backend,
                                      final Member cell ) {
    if ( cell == null ) {
      throw new NullPointerException();
    }
    if ( backend == null ) {
      throw new NullPointerException();
    }
    this.cell = cell;
    this.backend = backend;
  }

  public String[] getMetaAttributeDomains() {
    final String[] backendDomains = backend.getMetaAttributeDomains();
    return StringUtils.merge( NAMESPACES, backendDomains );
  }

  public String[] getMetaAttributeNames( final String domainName ) {
    if ( MDXMetaAttributeNames.NAMESPACE.equals( domainName ) ) {
      return PROPERTIES.clone();
    }

    return backend.getMetaAttributeNames( domainName );
  }

  public Object getMetaAttribute( final String domain,
                                  final String name,
                                  final Class type,
                                  final DataAttributeContext context ) {
    return getMetaAttribute( domain, name, type, context, null );
  }

  public Object getMetaAttribute( final String domain,
                                  final String name,
                                  final Class type,
                                  final DataAttributeContext context,
                                  final Object defaultValue ) {

    if ( MDXMetaAttributeNames.NAMESPACE.equals( domain ) ) {
      if ( name.equals( MDXMetaAttributeNames.MDX_ALL_MEMBER ) ) {
        return Boolean.valueOf( cell.isAll() );
      }
      if ( name.equals( MDXMetaAttributeNames.MDX_CALCULATED ) ) {
        return Boolean.valueOf( cell.isCalculated() );
      }
      if ( name.equals( MDXMetaAttributeNames.MDX_HIDDEN ) ) {
        return Boolean.valueOf( cell.isHidden() );
      }
      if ( name.equals( MDXMetaAttributeNames.MDX_CAPTION ) ) {
        return new LocalizedString( cell, false );
      }
      if ( name.equals( MDXMetaAttributeNames.MDX_DESCRIPTION ) ) {
        return new LocalizedString( cell, false );
      }

      try {
        final Property.StandardMemberProperty property = Property.StandardMemberProperty.valueOf( name );
        final Object attribute = cell.getPropertyValue( property );
        if ( attribute == null ) {
          return defaultValue;
        }
        return attribute;
      } catch ( IllegalArgumentException ie ) {
        return defaultValue;
      } catch ( OlapException ex ) {
        throw new IllegalStateException( "Failed to retrieve property from OLAP member", ex ); //$NON-NLS-1$
      }
    }

    return backend.getMetaAttribute( domain, name, type, context, defaultValue );
  }

  public Object clone() throws CloneNotSupportedException {
    final MDXMetaDataMemberAttributes attributes = (MDXMetaDataMemberAttributes) super.clone();
    attributes.backend = (DataAttributes) backend.clone();
    return attributes;
  }

  public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
    if ( MDXMetaAttributeNames.NAMESPACE.equals( domain ) ) {
      if ( MDXMetaAttributeNames.MDX_CAPTION.equals( name ) ) {
        return new LocalizedStringConceptMapper();
      }
      if ( MDXMetaAttributeNames.MDX_DESCRIPTION.equals( name ) ) {
        return new LocalizedStringConceptMapper();
      }
    }
    return DefaultConceptQueryMapper.INSTANCE;
  }
}
