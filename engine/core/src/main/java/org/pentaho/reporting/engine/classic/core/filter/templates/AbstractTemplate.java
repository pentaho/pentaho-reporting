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


package org.pentaho.reporting.engine.classic.core.filter.templates;

/**
 * An abstract base class that implements the {@link Template} interface.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractTemplate implements Template {
  /**
   * The template name.
   */
  private String name;

  /**
   * Creates a new template.
   */
  protected AbstractTemplate() {
  }

  /**
   * Sets the template name.
   *
   * @param name
   *          the name (<code>null</code> not permitted).
   */
  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  /**
   * Returns the template name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public AbstractTemplate clone() throws CloneNotSupportedException {
    return (AbstractTemplate) super.clone();
  }

  /**
   * Returns an instance of the template by cloning.
   *
   * @return A clone.
   */
  public Template getInstance() {
    try {
      return clone();
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( "Clone not supported" );
    }
  }

}
