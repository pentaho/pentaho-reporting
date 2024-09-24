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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;

/**
 * An object-description for a <code>Color</code> object.
 *
 * @author Thomas Morgner
 */
public class ColorObjectDescription extends AbstractObjectDescription {
  private ColorValueConverter valueConverter;

  /**
   * Creates a new object description.
   */
  public ColorObjectDescription() {
    super( Color.class );
    valueConverter = new ColorValueConverter();
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String value = (String) getParameter( "value" );
    if ( value == null ) {
      return null;
    }
    try {
      return valueConverter.toPropertyValue( value.trim() );
    } catch ( BeanException e ) {
      return null;
    }
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>Color</code>).
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Color ) ) {
      throw new ObjectFactoryException( "Is no instance of java.awt.Color" );
    }
    final Color c = (Color) o;
    try {
      setParameter( "value", valueConverter.toAttributeValue( c ) );
    } catch ( BeanException e ) {
      throw new ObjectFactoryException( "Failed to convert color to string", e );
    }
  }
}
