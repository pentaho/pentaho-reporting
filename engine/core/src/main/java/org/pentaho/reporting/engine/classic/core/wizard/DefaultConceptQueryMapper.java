/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
