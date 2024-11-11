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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.awt.*;

public class ColorConceptMapper implements ConceptQueryMapper {
  private ColorValueConverter valueConverter;

  public ColorConceptMapper() {
    valueConverter = new ColorValueConverter();
  }

  public Object getValue( final Object value,
                          final Class type,
                          final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof String ) {
      try {
        final int nvalue = Integer.parseInt( String.valueOf( value ) );
        final Color c = new Color( nvalue );
        if ( type == null || Object.class.equals( type ) || Color.class.isAssignableFrom( type ) ) {
          return c;
        }
        if ( String.class.isAssignableFrom( type ) ) {
          return valueConverter.toAttributeValue( c );
        }
      } catch ( NumberFormatException nfe ) {
        return null;
      } catch ( BeanException e ) {
        return null;
      }
    }

    if ( value instanceof Number ) {
      final Number nvalue = (Number) value;
      final Color c = new Color( nvalue.intValue() );
      if ( type == null || Object.class.equals( type ) || Color.class.isAssignableFrom( type ) ) {
        return c;
      }
      if ( String.class.isAssignableFrom( type ) ) {
        try {
          return valueConverter.toAttributeValue( c );
        } catch ( BeanException e ) {
          return null;
        }
      }
    }

    return null;
  }
}
