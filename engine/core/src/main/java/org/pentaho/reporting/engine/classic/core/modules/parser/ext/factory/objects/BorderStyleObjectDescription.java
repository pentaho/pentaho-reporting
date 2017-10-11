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

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.AbstractObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;

/**
 * An object-description for an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object.
 *
 * @author Thomas Morgner
 */
public class BorderStyleObjectDescription extends AbstractObjectDescription {
  /**
   * Creates a new object description.
   */
  public BorderStyleObjectDescription() {
    super( BorderStyle.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    if ( o == null ) {
      return null;
    }
    if ( "dashed".equalsIgnoreCase( o ) ) {
      return BorderStyle.DASHED;
    }
    if ( "dot-dash".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOT_DASH;
    }
    if ( "dot-dot-dash".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOT_DOT_DASH;
    }
    if ( "dotted".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOTTED;
    }
    if ( "double".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOUBLE;
    }
    if ( "groove".equalsIgnoreCase( o ) ) {
      return BorderStyle.GROOVE;
    }
    if ( "hidden".equalsIgnoreCase( o ) ) {
      return BorderStyle.HIDDEN;
    }
    if ( "inset".equalsIgnoreCase( o ) ) {
      return BorderStyle.INSET;
    }
    if ( "outset".equalsIgnoreCase( o ) ) {
      return BorderStyle.OUTSET;
    }
    if ( "none".equalsIgnoreCase( o ) ) {
      return BorderStyle.NONE;
    }
    if ( "ridge".equalsIgnoreCase( o ) ) {
      return BorderStyle.RIDGE;
    }
    if ( "solid".equalsIgnoreCase( o ) ) {
      return BorderStyle.SOLID;
    }
    if ( "wave".equalsIgnoreCase( o ) ) {
      return BorderStyle.WAVE;
    }
    return null;
  }

  /**
   * Sets the parameters in the object description to match the specified object.
   *
   * @param o
   *          the object (an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} instance).
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException
   *           if the object is not recognised.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( o.equals( BorderStyle.DASHED ) ) {
      setParameter( "value", "dashed" );
    } else if ( o.equals( BorderStyle.DOT_DASH ) ) {
      setParameter( "value", "dot-dash" );
    } else if ( o.equals( BorderStyle.DOT_DOT_DASH ) ) {
      setParameter( "value", "dot-dot-dash" );
    } else if ( o.equals( BorderStyle.DOTTED ) ) {
      setParameter( "value", "dotted" );
    } else if ( o.equals( BorderStyle.DOUBLE ) ) {
      setParameter( "value", "double" );
    } else if ( o.equals( BorderStyle.GROOVE ) ) {
      setParameter( "value", "groove" );
    } else if ( o.equals( BorderStyle.HIDDEN ) ) {
      setParameter( "value", "hidden" );
    } else if ( o.equals( BorderStyle.INSET ) ) {
      setParameter( "value", "inset" );
    } else if ( o.equals( BorderStyle.NONE ) ) {
      setParameter( "value", "none" );
    } else if ( o.equals( BorderStyle.OUTSET ) ) {
      setParameter( "value", "outset" );
    } else if ( o.equals( BorderStyle.RIDGE ) ) {
      setParameter( "value", "ridge" );
    } else if ( o.equals( BorderStyle.SOLID ) ) {
      setParameter( "value", "solid" );
    } else if ( o.equals( BorderStyle.WAVE ) ) {
      setParameter( "value", "wave" );
    } else {
      throw new ObjectFactoryException( "Invalid value specified for ElementAlignment" );
    }
  }

}
