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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.pentaho.reporting.engine.classic.core.filter.templates.Template;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A template collection.
 *
 * @author Thomas Morgner
 */
public class TemplateCollection implements Serializable {
  /**
   * Storage for the templates.
   */
  private final HashMap templates;

  /**
   * The parser/report configuration.
   */
  private Configuration config;

  /**
   * Creates a new collection.
   */
  public TemplateCollection() {
    templates = new HashMap();
  }

  /**
   * Adds a template.
   *
   * @param template
   *          the template.
   */
  public void addTemplate( final TemplateDescription template ) {
    if ( template == null ) {
      throw new NullPointerException();
    }
    templates.put( template.getName(), template );
    if ( getConfig() != null ) {
      template.configure( getConfig() );
    }
  }

  /**
   * Returns a template.
   *
   * @param name
   *          the template name.
   * @return The template description.
   */
  public TemplateDescription getTemplate( final String name ) {
    final TemplateDescription td = (TemplateDescription) templates.get( name );
    if ( td != null ) {
      return (TemplateDescription) td.getInstance();
    }
    return null;
  }

  /**
   * Returns a template description.
   *
   * @param template
   *          the template.
   * @return The description.
   */
  public TemplateDescription getDescription( final Template template ) {
    if ( template == null ) {
      throw new NullPointerException( "Template given must not be null." );
    }
    final Iterator values = templates.values().iterator();
    while ( values.hasNext() ) {
      final TemplateDescription td = (TemplateDescription) values.next();
      if ( td.getObjectClass().equals( template.getClass() ) ) {
        return (TemplateDescription) td.getInstance();
      }
    }
    return null;
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
    if ( config == null ) {
      throw new NullPointerException( "The given configuration is null" );
    }
    if ( this.config != null ) {
      // already configured ... ignored
      return;
    }

    this.config = config;
    final Iterator it = templates.values().iterator();
    while ( it.hasNext() ) {
      final TemplateDescription od = (TemplateDescription) it.next();
      od.configure( config );
    }

  }

  /**
   * Returns the currently set configuration or null, if none was set.
   *
   * @return the configuration.
   */
  public Configuration getConfig() {
    return config;
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
    if ( !( o instanceof TemplateCollection ) ) {
      return false;
    }

    final TemplateCollection templateCollection = (TemplateCollection) o;

    if ( !templates.equals( templateCollection.templates ) ) {
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
    return templates.hashCode();
  }

  /**
   * Returns the names of all defined templates.
   *
   * @return the template names.
   */
  public String[] getNames() {
    return (String[]) templates.keySet().toArray( new String[templates.size()] );
  }
}
