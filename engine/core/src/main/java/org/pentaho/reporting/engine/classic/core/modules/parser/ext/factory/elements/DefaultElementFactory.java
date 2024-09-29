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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements;

import org.pentaho.reporting.engine.classic.core.Element;

/**
 * A default implementation of the {@link ElementFactory} interface.
 *
 * @author Thomas Morgner
 */
public class DefaultElementFactory implements ElementFactory {
  /**
   * Creates a new element factory.
   */
  public DefaultElementFactory() {
  }

  /**
   * Returns an element for the specified type. This implementation assumes, that all elements have a public default
   * constructor and uses Class.newInstance() to create a new instance of that element.
   *
   * @param type
   *          the type.
   * @return The element.
   */
  public Element getElementForType( final String type ) {
    return new Element();
  }

  /**
   * Compares this object with the given object for equality. The object will be considered equal if it is a element
   * factory and contains the same elements.
   *
   * @param o
   *          the object that should be compared.
   * @return true, if the given object is equal, false otherwise.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof DefaultElementFactory ) ) {
      return false;
    }
    return true;
  }

  /**
   * Computes an hashcode for this factory.
   *
   * @return the hashcode.
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return 0;
  }
}
