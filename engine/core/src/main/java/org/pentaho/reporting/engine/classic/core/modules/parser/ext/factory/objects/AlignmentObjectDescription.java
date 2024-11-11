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
