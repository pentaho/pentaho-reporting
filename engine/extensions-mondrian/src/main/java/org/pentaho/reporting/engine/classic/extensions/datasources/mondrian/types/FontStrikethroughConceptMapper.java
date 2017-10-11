/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.types;

import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class FontStrikethroughConceptMapper implements ConceptQueryMapper {
  public FontStrikethroughConceptMapper() {
  }

  public Object getValue( final Object value,
                          final Class type,
                          final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    final int nvalue;
    if ( value instanceof String ) {
      try {
        nvalue = Integer.parseInt( String.valueOf( value ) );
      } catch ( NumberFormatException nfe ) {
        return null;
      }
    } else if ( value instanceof Number ) {
      final Number numvalue = (Number) value;
      nvalue = numvalue.intValue();
    } else {
      return null;
    }
    if ( Boolean.class.isAssignableFrom( type ) ) {
      if ( ( nvalue & 8 ) == 8 ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    if ( String.class.isAssignableFrom( type ) ) {
      if ( ( nvalue & 8 ) == 8 ) {
        return "true";
      }
      return "false";
    }

    return null;
  }
}
