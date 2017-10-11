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
