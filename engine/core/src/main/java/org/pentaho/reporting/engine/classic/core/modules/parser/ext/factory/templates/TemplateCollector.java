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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.pentaho.reporting.engine.classic.core.filter.templates.Template;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A template collection.
 *
 * @author Thomas Morgner
 */
public class TemplateCollector extends TemplateCollection {
  /**
   * Storage for the factories.
   */
  private final ArrayList factories;

  /**
   * Creates a new template collector.
   */
  public TemplateCollector() {
    factories = new ArrayList();
  }

  /**
   * Adds a template collection.
   *
   * @param tc
   *          the template collection.
   */
  public void addTemplateCollection( final TemplateCollection tc ) {
    if ( tc == null ) {
      throw new NullPointerException();
    }
    factories.add( tc );
    if ( getConfig() != null ) {
      tc.configure( getConfig() );
    }
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
   * Returns a template description.
   *
   * @param name
   *          the name.
   * @return The template description.
   */
  public TemplateDescription getTemplate( final String name ) {
    for ( int i = 0; i < factories.size(); i++ ) {
      final TemplateCollection fact = (TemplateCollection) factories.get( i );
      final TemplateDescription o = fact.getTemplate( name );
      if ( o != null ) {
        return o;
      }
    }
    return super.getTemplate( name );
  }

  /**
   * Returns a template description.
   *
   * @param template
   *          the template.
   * @return The description.
   */
  public TemplateDescription getDescription( final Template template ) {
    for ( int i = 0; i < factories.size(); i++ ) {
      final TemplateCollection fact = (TemplateCollection) factories.get( i );
      final TemplateDescription o = fact.getDescription( template );
      if ( o != null ) {
        return o;
      }
    }
    return super.getDescription( template );
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

    final Iterator it = factories.iterator();
    while ( it.hasNext() ) {
      final TemplateCollection od = (TemplateCollection) it.next();
      od.configure( config );
    }

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
    if ( !( o instanceof TemplateCollector ) ) {
      return false;
    }
    if ( !super.equals( o ) ) {
      return false;
    }

    final TemplateCollector templateCollector = (TemplateCollector) o;

    if ( !factories.equals( templateCollector.factories ) ) {
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
    int result = super.hashCode();
    result = 29 * result + factories.hashCode();
    return result;
  }
}
