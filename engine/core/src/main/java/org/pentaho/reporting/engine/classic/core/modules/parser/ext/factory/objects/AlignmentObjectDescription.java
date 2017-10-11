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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.AbstractObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;

/**
 * An object-description for an {@link ElementAlignment} object.
 *
 * @author Thomas Morgner
 */
public class AlignmentObjectDescription extends AbstractObjectDescription {
  /**
   * Creates a new object description.
   */
  public AlignmentObjectDescription() {
    super( ElementAlignment.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an {@link ElementAlignment} object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    if ( o == null ) {
      return null;
    }
    if ( "left".equalsIgnoreCase( o ) ) {
      return ElementAlignment.LEFT;
    }
    if ( "right".equalsIgnoreCase( o ) ) {
      return ElementAlignment.RIGHT;
    }
    if ( "justify".equalsIgnoreCase( o ) ) {
      return ElementAlignment.JUSTIFY;
    }
    if ( "center".equalsIgnoreCase( o ) ) {
      return ElementAlignment.CENTER;
    }
    if ( "top".equalsIgnoreCase( o ) ) {
      return ElementAlignment.TOP;
    }
    if ( "middle".equalsIgnoreCase( o ) ) {
      return ElementAlignment.MIDDLE;
    }
    if ( "bottom".equalsIgnoreCase( o ) ) {
      return ElementAlignment.BOTTOM;
    }
    return null;
  }

  /**
   * Sets the parameters in the object description to match the specified object.
   *
   * @param o
   *          the object (an {@link ElementAlignment} instance).
   * @throws ObjectFactoryException
   *           if the object is not recognised.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( o.equals( ElementAlignment.BOTTOM ) ) {
      setParameter( "value", "bottom" );
    } else if ( o.equals( ElementAlignment.MIDDLE ) ) {
      setParameter( "value", "middle" );
    } else if ( o.equals( ElementAlignment.TOP ) ) {
      setParameter( "value", "top" );
    } else if ( o.equals( ElementAlignment.CENTER ) ) {
      setParameter( "value", "center" );
    } else if ( o.equals( ElementAlignment.RIGHT ) ) {
      setParameter( "value", "right" );
    } else if ( o.equals( ElementAlignment.JUSTIFY ) ) {
      setParameter( "value", "justify" );
    } else if ( o.equals( ElementAlignment.LEFT ) ) {
      setParameter( "value", "left" );
    } else {
      throw new ObjectFactoryException( "Invalid value specified for ElementAlignment" );
    }
  }

}
