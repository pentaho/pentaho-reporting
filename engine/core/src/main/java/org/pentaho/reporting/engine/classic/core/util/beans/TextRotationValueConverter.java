/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.style.TextRotation;

public class TextRotationValueConverter implements ValueConverter {
  public TextRotationValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o instanceof TextRotation ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a TextRotation." );
    }
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      return null;
    }

    if ( TextRotation.D_90.toString().equalsIgnoreCase( o ) ) {
      return TextRotation.D_90;
    }

    if ( TextRotation.D_270.toString().equalsIgnoreCase( o ) ) {
      return TextRotation.D_270;
    }
    throw new BeanException( "Invalid value specified for TextRotation" );
  }
}
