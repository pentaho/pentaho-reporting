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


package org.pentaho.reporting.engine.classic.core;

import java.io.Serializable;

/**
 * A parameter mapping defines an aliasing rule for incoming and outgoing sub-report parameters.
 *
 * @author Thomas Morgner
 */
public class ParameterMapping implements Serializable {
  /**
   * A serialization helper.
   */
  private static final long serialVersionUID = -8790399939032695626L;

  /**
   * The source name of the parameter.
   */
  private String name;
  /**
   * The target name of the parameter.
   */
  private String alias;

  /**
   * Creates a new parameter mapping for the given parameter. The parameter will be made available using the given
   * 'alias' name. If the alias is null, the name will not be changed during the mapping.
   *
   * @param name
   *          the name.
   * @param alias
   *          the alias (can be null).
   */
  public ParameterMapping( final String name, final String alias ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    if ( alias == null ) {
      this.alias = name;
    } else {
      this.alias = alias;
    }
  }

  /**
   * Returns the source parameter name.
   *
   * @return the source name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the alias parameter name.
   *
   * @return the alias name.
   */
  public String getAlias() {
    return alias;
  }
}
