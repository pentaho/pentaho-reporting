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
 * This function returns the average of the number sequence.
 *
 * @author Cedric Pronzato
 */
public class AverageAFunction extends AverageFunction {
  public AverageAFunction() {
    super( new SumAFunction() );
  }

  @Override
  public String getCanonicalName() {
    return "AVERAGEA";
  }
}
