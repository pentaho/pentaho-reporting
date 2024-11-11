/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.function.math;

/**
 * This function returns the maximum from a set of numbers.
 *
 * @author Cedric Pronzato
 */
public class MaxAFunction extends MaxFunction {

  public MaxAFunction() {
  }

  @Override
  protected boolean isStrictSequenceNeeded() {
    return false;
  }

  @Override
  public String getCanonicalName() {
    return "MAXA";
  }
}
