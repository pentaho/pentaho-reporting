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

import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class SecurityConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new SecurityConceptMapper();
  public SecurityConceptMapper() {
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

    if ( value instanceof Security == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || Security.class.equals( type ) ) {
      if ( value instanceof SecurityWrapper ) {
        return value;
      }
      return new SecurityWrapper( (Security) value );
    }

    return null;
  }

}
