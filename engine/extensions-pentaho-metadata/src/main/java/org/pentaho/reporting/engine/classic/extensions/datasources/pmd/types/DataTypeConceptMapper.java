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

import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.util.Date;

public class DataTypeConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new DataTypeConceptMapper();

  public DataTypeConceptMapper() {
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

    if ( value instanceof DataType == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || DataType.class.equals( type ) ) {
      return value;
    }

    final DataType dataTypeSettings = (DataType) value;
    //    public static final int DATA_TYPE_STRING = 1;
    //    public static final int DATA_TYPE_DATE = 2;
    //    public static final int DATA_TYPE_BOOLEAN = 3;
    //    public static final int DATA_TYPE_NUMERIC = 4;
    //    public static final int DATA_TYPE_BINARY = 5;
    //    public static final int DATA_TYPE_IMAGE = 6;
    //    public static final int DATA_TYPE_URL = 7;
    final Class convertedValue;
    if ( DataType.STRING.equals( dataTypeSettings ) ) {
      convertedValue = String.class;
    } else if ( DataType.DATE.equals( dataTypeSettings ) ) {
      convertedValue = Date.class;
    } else if ( DataType.BOOLEAN.equals( dataTypeSettings ) ) {
      convertedValue = Boolean.class;
    } else if ( DataType.NUMERIC.equals( dataTypeSettings ) ) {
      convertedValue = Number.class;
    } else if ( DataType.URL.equals( dataTypeSettings ) ) {
      convertedValue = String.class;
    } else {
      // we cannot safely handle binary, image or URL types. For them, we fall back to the
      // database metadata instead ..
      return null;
    }

    if ( Class.class.equals( type ) ) {
      return convertedValue;
    }
    if ( String.class.equals( type ) ) {
      return convertedValue.getName();
    }
    return null;
  }
}
