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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * Tests, whether a certain export type is currently used. This matches the given export type with the export type that
 * is specified by the output-target. The given export type can be a partial pattern, in which case this expression
 * tests, whether the given export type is a sub-type of the output-target's type.
 * <p/>
 * To test whether a table-export is used, specifiy the export type as "table" and it will match all table exports.
 *
 * @author Thomas Morgner
 */
public class IsExportTypeExpression extends AbstractExpression {
  /**
   * The export type for which to test for.
   */
  private String exportType;

  /**
   * Default constructor.
   */
  public IsExportTypeExpression() {
  }

  /**
   * Returns the export type string.
   *
   * @return the export type string.
   */
  public String getExportType() {
    return exportType;
  }

  /**
   * Defines the export type.
   *
   * @param exportType
   *          the export type.
   */
  public void setExportType( final String exportType ) {
    this.exportType = exportType;
  }

  /**
   * Return Boolean.TRUE, if the specified export type matches the used export type, Boolean.FALSE otherwise.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    if ( exportType == null ) {
      return Boolean.FALSE;
    }
    if ( getRuntime().getExportDescriptor().startsWith( exportType ) ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
