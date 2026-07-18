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

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionCategory;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;

/**
 * Creation-Date: 05.11.2006, 14:30:36
 *
 * @author Thomas Morgner
 */
public final class MathFunctionCategory extends AbstractFunctionCategory {
  public static final FunctionCategory CATEGORY = new MathFunctionCategory();

  private MathFunctionCategory() {
    super( "org.pentaho.reporting.libraries.formula.function.math.category" );
  }
}
