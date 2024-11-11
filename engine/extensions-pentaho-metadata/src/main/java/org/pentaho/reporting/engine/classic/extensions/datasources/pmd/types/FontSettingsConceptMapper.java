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
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FontSettingsConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new FontSettingsConceptMapper();
  public FontSettingsConceptMapper() {
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

    if ( type == null || Object.class.equals( type ) || Font.class.equals( type ) ) {
      return value;
    }
    return null;
  }
}
