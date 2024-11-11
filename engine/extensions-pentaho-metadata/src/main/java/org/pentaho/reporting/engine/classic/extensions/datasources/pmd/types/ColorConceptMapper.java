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

import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class ColorConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new ColorConceptMapper();

  private ColorValueConverter colorValueConverter;

  public ColorConceptMapper() {
    colorValueConverter = new ColorValueConverter();
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

    if ( value instanceof Color == false ) {
      return null;
    }
    if ( type == null || Object.class.equals( type ) || Color.class.equals( type ) ) {
      return value;
    }

    final Color settings = (Color) value;
    final java.awt.Color color = new java.awt.Color( settings.getRed(), settings.getGreen(), settings.getBlue() );
    if ( java.awt.Color.class.equals( type ) ) {
      return color;
    }
    if ( String.class.equals( type ) ) {
      try {
        return colorValueConverter.toAttributeValue( color );
      } catch ( BeanException e ) {
        return null;
      }
    }

    return null;
  }
}
