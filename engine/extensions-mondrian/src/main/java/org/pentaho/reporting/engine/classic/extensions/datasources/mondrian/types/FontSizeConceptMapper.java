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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.types;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FontSizeConceptMapper implements ConceptQueryMapper {
  public FontSizeConceptMapper() {
  }

  public Object getValue( final Object value,
                          final Class type,
                          final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    final String valueAsString = String.valueOf( value );
    if ( String.class.isAssignableFrom( type ) ) {
      return valueAsString;
    }
    if ( type == null || Object.class.equals( type ) || Number.class.isAssignableFrom( type ) ) {
      try {
        return ConverterRegistry.toPropertyValue( valueAsString, type );
      } catch ( BeanException e ) {
        // ignore ..
      }
    }
    return null;
  }
}
