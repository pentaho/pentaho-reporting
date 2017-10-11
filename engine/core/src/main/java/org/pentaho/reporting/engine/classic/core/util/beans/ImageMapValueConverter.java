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

package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapParser;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapWriter;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class ImageMapValueConverter implements ValueConverter {
  public ImageMapValueConverter() {
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o
   *          the object.
   * @return the attribute value.
   * @throws org.pentaho.reporting.engine.classic.core.util.beans.BeanException
   *           if there was an error during the conversion.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( ( o instanceof ImageMap ) == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a ImageMap." );
    }
    return ImageMapWriter.writeImageMapAsString( (ImageMap) o );
  }

  /**
   * Converts a string to a property value.
   *
   * @param s
   *          the string.
   * @return a property value.
   * @throws org.pentaho.reporting.engine.classic.core.util.beans.BeanException
   *           if there was an error during the conversion.
   */
  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    try {
      final ImageMapParser parser = new ImageMapParser();
      return parser.parseFromString( s );
    } catch ( ResourceException ioe ) {
      throw new BeanException( "Failed to parse image map.", ioe );
    }
  }
}
