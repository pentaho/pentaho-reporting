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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.awt.*;

public class ColorConceptMapper implements ConceptQueryMapper {
  private ColorValueConverter valueConverter;

  public ColorConceptMapper() {
    valueConverter = new ColorValueConverter();
  }

  public Object getValue( final Object value,
                          final Class type,
                          final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof String ) {
      try {
        final int nvalue = Integer.parseInt( String.valueOf( value ) );
        final Color c = new Color( nvalue );
        if ( type == null || Object.class.equals( type ) || Color.class.isAssignableFrom( type ) ) {
          return c;
        }
        if ( String.class.isAssignableFrom( type ) ) {
          return valueConverter.toAttributeValue( c );
        }
      } catch ( NumberFormatException nfe ) {
        return null;
      } catch ( BeanException e ) {
        return null;
      }
    }

    if ( value instanceof Number ) {
      final Number nvalue = (Number) value;
      final Color c = new Color( nvalue.intValue() );
      if ( type == null || Object.class.equals( type ) || Color.class.isAssignableFrom( type ) ) {
        return c;
      }
      if ( String.class.isAssignableFrom( type ) ) {
        try {
          return valueConverter.toAttributeValue( c );
        } catch ( BeanException e ) {
          return null;
        }
      }
    }

    return null;
  }
}
