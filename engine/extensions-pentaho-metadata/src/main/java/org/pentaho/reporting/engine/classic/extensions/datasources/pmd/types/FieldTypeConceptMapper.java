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

import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FieldTypeConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new FieldTypeConceptMapper();

  public FieldTypeConceptMapper() {
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

    if ( value instanceof FieldType == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || FieldType.class.equals( type ) ) {
      return value;
    }

    if ( String.class.equals( type ) == false ) {
      return null;
    }

    final FieldType fieldTypeSettings = (FieldType) value;
    if ( FieldType.ATTRIBUTE.equals( fieldTypeSettings ) ) {
      return "attribute";
    }
    if ( FieldType.FACT.equals( fieldTypeSettings ) ) {
      return "fact";
    }
    if ( FieldType.KEY.equals( fieldTypeSettings ) ) {
      return "key";
    }
    if ( FieldType.DIMENSION.equals( fieldTypeSettings ) ) {
      return "dimension";
    }
    return null;
  }
}
