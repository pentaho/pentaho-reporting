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

public class FontBoldConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new FontBoldConceptMapper();

  public FontBoldConceptMapper() {
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
    if ( type == null || Object.class.equals( type ) || Boolean.class.isAssignableFrom( type ) ) {
      return fontSettings.isBold();
    }
    if ( String.class.isAssignableFrom( type ) ) {
      return String.valueOf( fontSettings.isBold() );
    }
    return null;
  }
}
