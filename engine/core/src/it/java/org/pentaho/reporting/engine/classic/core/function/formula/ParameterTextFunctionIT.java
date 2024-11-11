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


package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ParameterTextFunctionIT extends FormulaTestBase {
  public ParameterTextFunctionIT() {
  }

  public ParameterTextFunctionIT( final String s ) {
    super( s );
  }

  protected Object[][] createDataTest() {
    return new Object[][] { { "PARAMETERTEXT(DATE(2009;10;10))", "2009-10-10T00%3A00%3A00.000%2B0000" },
      { "PARAMETERTEXT(100000)", "100000" }, { "PARAMETERTEXT(1000.001)", "1000.001" },
      { "PARAMETERTEXT(\"AAAA\"; TRUE())", "AAAA" }, { "PARAMETERTEXT(\"&:;\"; FALSE())", "&:;" },
      { "PARAMETERTEXT(\"&:;\"; TRUE())", "%26%3A%3B" },

    };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }
}
