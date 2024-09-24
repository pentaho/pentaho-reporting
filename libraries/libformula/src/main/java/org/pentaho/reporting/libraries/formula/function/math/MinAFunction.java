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

package org.pentaho.reporting.libraries.formula.function.math;

/**
 * This function returns the minimum from a set of numbers.
 *
 * @author Cedric Pronzato
 */
public class MinAFunction extends MinFunction {

  public MinAFunction() {
  }

  @Override
  protected boolean isStrictSequenceNeeded() {
    return false;
  }

  @Override
  public String getCanonicalName() {
    return "MINA";
  }

}
