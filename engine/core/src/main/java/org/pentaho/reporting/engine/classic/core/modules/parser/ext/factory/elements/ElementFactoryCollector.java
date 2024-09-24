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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An element factory that encapsulates multiple element factories.
 *
 * @author Thomas Morgner
 */
public class ElementFactoryCollector implements ElementFactory {
  /**
   * Storage for the element factories.
   */
  private final ArrayList factories;

  /**
   * Creates a new element factory.
   */
  public ElementFactoryCollector() {
    factories = new ArrayList();
  }

  /**
   * Adds an element factory.
   *
   * @param factory
   *          the factory.
   */
  public void addFactory( final ElementFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    factories.add( factory );
  }

  /**
   * Returns an iterator that provides access to the factories.
   *
   * @return The iterator.
   */
  public Iterator getFactories() {
    return factories.iterator();
  }

  /**
   * Returns an element for the given type.
   *
   * @param type
   *          the content type.
   * @return The element.
   */
  public Element getElementForType( final String type ) {
    for ( int i = 0; i < factories.size(); i++ ) {
      final ElementFactory fact = (ElementFactory) factories.get( i );
      final Element element = fact.getElementForType( type );
      if ( element != null ) {
        return element;
      }
    }
    return null;
  }

  /**
   * Indicated whether an other object is equal to this one.
   *
   * @param o
   *          the other object.
   * @return true, if the object is equal, false otherwise.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ElementFactoryCollector ) ) {
      return false;
    }

    final ElementFactoryCollector elementFactoryCollector = (ElementFactoryCollector) o;

    if ( !factories.equals( elementFactoryCollector.factories ) ) {
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
    return factories.hashCode();
  }
}
