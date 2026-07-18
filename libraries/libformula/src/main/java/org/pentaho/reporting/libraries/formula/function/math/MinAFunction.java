/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
