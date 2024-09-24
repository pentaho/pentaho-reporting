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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ElementAlignmentValueConverter;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class AlignmentConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new AlignmentConceptMapper();

  private ElementAlignmentValueConverter alignmentValueConverter;

  public AlignmentConceptMapper() {
    alignmentValueConverter = new ElementAlignmentValueConverter();
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

    if ( value instanceof Alignment == false ) {
      return null;
    }

    if ( Alignment.class.equals( type ) ) {
      return value;
    }

    if ( type == null || Object.class.equals( type ) ) {
      return value;
    }

    final String textAlignment;
    if ( Alignment.LEFT.equals( value ) ) {
      textAlignment = "left";
    } else if ( Alignment.CENTERED.equals( value ) ) {
      textAlignment = "center";
    } else if ( Alignment.RIGHT.equals( value ) ) {
      textAlignment = "right";
    } else if ( Alignment.JUSTIFIED.equals( value ) ) {
      textAlignment = "justified";
    } else {
      return null;
    }

    if ( Alignment.class.equals( type ) ) {
      try {
        return alignmentValueConverter.toPropertyValue( textAlignment );
      } catch ( BeanException e ) {
        // ignore ...
        return null;
      }
    }
    if ( String.class.equals( type ) ) {
      return textAlignment;
    }

    return null;
  }
}
