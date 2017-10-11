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
