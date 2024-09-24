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
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;

/**
 * An object-description for an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object.
 *
 * @author Thomas Morgner
 */
public class BoxSizingObjectDescription extends AbstractObjectDescription {
  /**
   * Creates a new object description.
   */
  public BoxSizingObjectDescription() {
    super( BoxSizing.class );
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
    if ( BoxSizing.BORDER_BOX.toString().equals( o ) ) {
      return BoxSizing.BORDER_BOX;
    }
    if ( BoxSizing.CONTENT_BOX.toString().equals( o ) ) {
      return BoxSizing.CONTENT_BOX;
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
    if ( o.equals( BoxSizing.BORDER_BOX ) ) {
      setParameter( "value", BoxSizing.BORDER_BOX.toString() );
    } else if ( o.equals( BoxSizing.CONTENT_BOX ) ) {
      setParameter( "value", BoxSizing.CONTENT_BOX.toString() );
    } else {
      throw new ObjectFactoryException( "Invalid value specified for ElementAlignment" );
    }
  }

}
