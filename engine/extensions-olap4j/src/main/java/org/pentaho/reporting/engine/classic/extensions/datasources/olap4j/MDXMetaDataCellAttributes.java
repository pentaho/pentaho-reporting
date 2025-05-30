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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.Cell;
import org.olap4j.metadata.Property;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class MDXMetaDataCellAttributes implements DataAttributes {
  private static final String[] NAMESPACES =
    new String[] { MDXMetaAttributeNames.NAMESPACE };
  private static final String[] PROPERTIES =
    new String[]
      {

        "CELL_EVALUATION_LIST",
        "CELL_ORDINAL",
        "FORMATTED_VALUE",
        "NON_EMPTY_BEHAVIOR",
        "SOLVE_ORDER",
        "VALUE",
        "DATATYPE",
        "ACTION_TYPE",
        "UPDATEABLE",
        MDXMetaAttributeNames.BACKGROUND_COLOR,
        MDXMetaAttributeNames.FOREGROUND_COLOR,

        MDXMetaAttributeNames.FONT_FLAGS,
        MDXMetaAttributeNames.FONTSIZE,

        MDXMetaAttributeNames.FONTNAME,
        MDXMetaAttributeNames.FORMAT_STRING,
        MDXMetaAttributeNames.LANGUAGE,
      };

  private DataAttributes backend;
  private Cell cell;

  public MDXMetaDataCellAttributes( final DataAttributes backend,
                                    final Cell cell ) {
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
      try {
        final Object attribute = cell.getPropertyValue( Property.StandardCellProperty.valueOf( name ) );
        if ( attribute == null ) {
          return defaultValue;
        }
        return attribute;
      } catch ( IllegalArgumentException e ) {
        return defaultValue;
      }
    }

    return backend.getMetaAttribute( domain, name, type, context, defaultValue );
  }

  public Object clone() throws CloneNotSupportedException {
    final MDXMetaDataCellAttributes attributes = (MDXMetaDataCellAttributes) super.clone();
    attributes.backend = (DataAttributes) backend.clone();
    return attributes;
  }

  public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
    return DefaultConceptQueryMapper.INSTANCE;
  }
}
