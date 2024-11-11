/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.types;

import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FontItalicConceptMapper implements ConceptQueryMapper {
  public FontItalicConceptMapper() {
  }

  public Object getValue( final Object value,
                          final Class type,
                          final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    final int nvalue;
    if ( value instanceof String ) {
      try {
        nvalue = Integer.parseInt( String.valueOf( value ) );
      } catch ( NumberFormatException nfe ) {
        return null;
      }
    } else if ( value instanceof Number ) {
      final Number numvalue = (Number) value;
      nvalue = numvalue.intValue();
    } else {
      return null;
    }
    if ( type == null || Object.class.equals( type ) || Boolean.class.isAssignableFrom( type ) ) {
      if ( ( nvalue & 2 ) == 2 ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    if ( String.class.isAssignableFrom( type ) ) {
      if ( ( nvalue & 2 ) == 2 ) {
        return "true";
      }
      return "false";
    }

    return null;
  }
}
