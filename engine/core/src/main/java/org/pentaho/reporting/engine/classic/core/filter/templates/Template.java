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

import org.pentaho.reporting.engine.classic.core.filter.DataSource;

/**
 * A template defines a common use case for a DataSource and one or more predefined Filters.
 *
 * @author Thomas Morgner
 */
public interface Template extends DataSource {
  /**
   * Sets the name of this template.
   *
   * @param name
   *          the name.
   */
  public void setName( String name );

  /**
   * Returns the template name.
   *
   * @return The name.
   */
  public String getName();

  /**
   * Returns an instance of the template.
   *
   * @return A template instance.
   */
  public Template getInstance();

}
