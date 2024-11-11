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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FontSizeConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new FontSizeConceptMapper();
  public FontSizeConceptMapper() {
  }

  /**
   * @param value
   * @param type
   * @return
   */
  public Object getValue( final Object value, final Class type, final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof Font == false ) {
      return null;
    }

    final Font fontSettings = (Font) value;
    final String valueAsString = String.valueOf( fontSettings.getHeight() );
    if ( type == null || Object.class.equals( type ) || Number.class.isAssignableFrom( type ) ) {
      try {
        final Object returnValue = ConverterRegistry.toPropertyValue( valueAsString, type );
        return returnValue;
      } catch ( BeanException e ) {
        // ignore ..
      }
    }
    if ( String.class.isAssignableFrom( type ) ) {
      return valueAsString;
    }
    return null;
  }
}
