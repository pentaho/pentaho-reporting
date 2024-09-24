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

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.AbstractObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

/**
 * An object-description for an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object.
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapseObjectDescription extends AbstractObjectDescription {
  /**
   * Creates a new object description.
   */
  public WhitespaceCollapseObjectDescription() {
    super( WhitespaceCollapse.class );
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
    if ( "discard".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.DISCARD;
    }
    if ( "none".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.COLLAPSE;
    }
    if ( "preserve".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.PRESERVE;
    }
    if ( "preserve-breaks".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.PRESERVE_BREAKS;
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
    if ( o.equals( WhitespaceCollapse.DISCARD ) ) {
      setParameter( "value", "discard" );
    } else if ( o.equals( WhitespaceCollapse.COLLAPSE ) ) {
      setParameter( "value", "collapse" );
    } else if ( o.equals( WhitespaceCollapse.PRESERVE ) ) {
      setParameter( "value", "preserve" );
    } else if ( o.equals( WhitespaceCollapse.PRESERVE_BREAKS ) ) {
      setParameter( "value", "preserve-breaks" );
    } else {
      throw new ObjectFactoryException( "Invalid value specified for WhitespaceCollapse" );
    }
  }

}
