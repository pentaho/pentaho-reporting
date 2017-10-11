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
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.BeanObjectDescription;

/**
 * An abstract class for implementing the {@link TemplateDescription} interface.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractTemplateDescription extends BeanObjectDescription implements TemplateDescription {
  /**
   * The name.
   */
  private String name;

  /**
   * Creates a new description.
   *
   * @param name
   *          the name.
   * @param template
   *          the template class.
   * @param init
   *          initialise?
   */
  protected AbstractTemplateDescription( final String name, final Class template, final boolean init ) {
    super( template, init );
    this.name = name;
  }

  /**
   * Returns the name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name
   *          the name (<code>null</code> not allowed).
   */
  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  /**
   * Creates a template.
   *
   * @return The template.
   */
  public Template createTemplate() {
    return (Template) createObject();
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
    if ( !( o instanceof AbstractTemplateDescription ) ) {
      return false;
    }
    if ( !super.equals( o ) ) {
      return false;
    }

    final AbstractTemplateDescription abstractTemplateDescription = (AbstractTemplateDescription) o;

    if ( name != null ) {
      if ( !name.equals( abstractTemplateDescription.name ) ) {
        return false;
      }
    } else {
      if ( abstractTemplateDescription.name != null ) {
        return false;
      }
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
    result = 29 * result + ( name != null ? name.hashCode() : 0 );
    return result;
  }
}
