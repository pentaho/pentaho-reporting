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

import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class AggregationConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new AggregationConceptMapper();

  public AggregationConceptMapper() {
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

    if ( value instanceof AggregationType == false ) {
      return null;
    }

    if ( AggregationType.class.equals( type ) ) {
      return value;
    }

    if ( type == null || Object.class.equals( type ) ) {
      return value;
    }

    if ( AggregationType.NONE.equals( value ) ) {
      return "none";
    } else if ( AggregationType.SUM.equals( value ) ) {
      return "sum";
    } else if ( AggregationType.AVERAGE.equals( value ) ) {
      return "average";
    } else if ( AggregationType.COUNT.equals( value ) ) {
      return "count";
    } else if ( AggregationType.COUNT_DISTINCT.equals( value ) ) {
      return "count_distinct";
    } else if ( AggregationType.MINIMUM.equals( value ) ) {
      return "minimum";
    } else if ( AggregationType.MAXIMUM.equals( value ) ) {
      return "maximum";
    } else {
      return null;
    }

  }
}
