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

/**
 * An object-description for a <code>Boolean</code> object.
 *
 * @author Thomas Morgner
 */
public class BooleanObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public BooleanObjectDescription() {
    super( Boolean.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates a new <code>Boolean</code> based on the settings of this description object.
   *
   * @return A <code>Boolean</code>.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    return Boolean.valueOf( o.trim() );
  }

  /**
   * Sets the description object parameters to match the supplied object (which should be an instance of
   * <code>Boolean</code>.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Boolean ) ) {
      throw new ObjectFactoryException( "The given object is no java.lang.Boolean. " );
    }
    setParameter( "value", String.valueOf( o ) );
  }

  /**
   * Tests for equality.
   *
   * @param o
   *          the object to test.
   * @return A boolean.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof AbstractObjectDescription ) ) {
      return false;
    }

    final AbstractObjectDescription abstractObjectDescription = (AbstractObjectDescription) o;

    if ( Boolean.TYPE.equals( abstractObjectDescription.getObjectClass() ) ) {
      return true;
    }
    if ( Boolean.class.equals( abstractObjectDescription.getObjectClass() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Returns a hash code.
   *
   * @return A hash code.
   */
  public int hashCode() {
    return getObjectClass().hashCode();
  }

}
