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
