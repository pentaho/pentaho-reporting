/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.net.URL;

public class URLConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new URLConceptMapper();

  public URLConceptMapper() {
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

    if ( value instanceof URL == false ) {
      return null;
    }
    if ( type == null || Object.class.equals( type ) || URL.class.equals( type ) ) {
      return value;
    }
    if ( String.class.equals( type ) ) {
      final URL url = (URL) value;
      return url.toExternalForm();
    }
    return null;
  }
}
