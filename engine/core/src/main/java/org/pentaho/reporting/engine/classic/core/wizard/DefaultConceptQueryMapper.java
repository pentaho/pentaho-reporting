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

package org.pentaho.reporting.engine.classic.core.wizard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

public class DefaultConceptQueryMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new DefaultConceptQueryMapper();
  private static final Log logger = LogFactory.getLog( DefaultConceptQueryMapper.class );

  public DefaultConceptQueryMapper() {
  }

  /**
   * @param attribute
   * @param type
   * @return
   */
  public Object getValue( final Object attribute, final Class type, final DataAttributeContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    if ( attribute == null ) {
      return null;
    }
    if ( type == null ) {
      return attribute;
    }

    if ( type.isInstance( attribute ) ) {
      return attribute;
    }
    if ( attribute instanceof String ) {
      try {
        final Object o = ConverterRegistry.toPropertyValue( (String) attribute, type );
        if ( o != null ) {
          return o;
        }
      } catch ( BeanException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Failed to convert metadata property value " + attribute + " into " + type, e );
        }
        return null;
      }
    }
    return null;
  }
}
