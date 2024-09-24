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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class factory collector.
 *
 * @author Thomas Morgner
 */
public class ClassFactoryCollector extends ClassFactoryImpl {

  /**
   * Storage for the class factories.
   */
  private ArrayList<ClassFactory> factories;

  /**
   * Creates a new class factory collector.
   */
  public ClassFactoryCollector() {
    this.factories = new ArrayList<ClassFactory>();
  }

  /**
   * Adds a class factory to the collection.
   *
   * @param factory
   *          the factory.
   */
  public void addFactory( final ClassFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    this.factories.add( factory );
    if ( getConfig() != null ) {
      factory.configure( getConfig() );
    }
  }

  /**
   * Returns an iterator the provides access to all the factories in the collection.
   *
   * @return The iterator.
   */
  public Iterator getFactories() {
    return this.factories.iterator();
  }

  /**
   * Returns an object description for a class.
   *
   * @param c
   *          the class.
   * @return The object description.
   */
  public ObjectDescription getDescriptionForClass( final Class c ) {
    for ( int i = 0; i < this.factories.size(); i++ ) {
      final ClassFactory f = this.factories.get( i );
      final ObjectDescription od = f.getDescriptionForClass( c );
      if ( od != null ) {
        return od;
      }
    }
    return super.getDescriptionForClass( c );
  }

  /**
   * Returns an object-description for the super class of a class.
   *
   * @param d
   *          the class.
   * @param knownSuperClass
   *          the last known super class or null.
   * @return The object description.
   */
  public ObjectDescription getSuperClassObjectDescription( final Class d, ObjectDescription knownSuperClass ) {
    for ( int i = 0; i < this.factories.size(); i++ ) {
      final ClassFactory f = this.factories.get( i );
      final ObjectDescription od = f.getSuperClassObjectDescription( d, knownSuperClass );
      if ( od != null ) {
        if ( knownSuperClass == null ) {
          knownSuperClass = od;
        } else {
          if ( knownSuperClass.getObjectClass().isAssignableFrom( od.getObjectClass() ) ) {
            knownSuperClass = od;
          }
        }
      }
    }
    return super.getSuperClassObjectDescription( d, knownSuperClass );
  }

  /**
   * Returns an iterator that provices access to the registered classes.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredClasses() {
    final ArrayList list = new ArrayList();
    for ( int i = 0; i < this.factories.size(); i++ ) {
      final ClassFactory f = this.factories.get( i );
      final Iterator iterator = f.getRegisteredClasses();
      while ( iterator.hasNext() ) {
        list.add( iterator.next() );
      }
    }
    return list.iterator();
  }

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( final Configuration config ) {
    if ( getConfig() != null ) {
      // already configured ...
      return;
    }
    super.configure( config );

    final Iterator it = this.factories.iterator();
    while ( it.hasNext() ) {
      final ClassFactory od = (ClassFactory) it.next();
      od.configure( config );
    }
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
    if ( !( o instanceof ClassFactoryCollector ) ) {
      return false;
    }
    if ( !super.equals( o ) ) {
      return false;
    }

    final ClassFactoryCollector classFactoryCollector = (ClassFactoryCollector) o;

    if ( !this.factories.equals( classFactoryCollector.factories ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code for the object.
   *
   * @return The hash code.
   */
  public int hashCode() {
    int result = super.hashCode();
    result = 29 * result + this.factories.hashCode();
    return result;
  }
}
