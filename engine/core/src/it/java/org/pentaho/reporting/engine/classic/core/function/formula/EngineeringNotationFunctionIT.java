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

public class EngineeringNotationFunctionIT extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][] { { "ENGINEERINGNOTATION(0)", "0 " }, { "ENGINEERINGNOTATION(1)", "1 " },
      { "ENGINEERINGNOTATION(100)", "100 " }, { "ENGINEERINGNOTATION(1000)", "1k" },
      { "ENGINEERINGNOTATION(10000)", "10k" }, { "ENGINEERINGNOTATION(-11)", "-11 " },
      { "ENGINEERINGNOTATION(-1000)", "-1k" }, { "ENGINEERINGNOTATION(-100000000)", "-100M" },
      { "ENGINEERINGNOTATION(0.0000101000)", "10\u00b5" },

      { "ENGINEERINGNOTATION(1; 5)", "1.000 " }, { "ENGINEERINGNOTATION(100; 5)", "100.00 " },
      { "ENGINEERINGNOTATION(1000; 6)", "1.0000k" }, { "ENGINEERINGNOTATION(10000; 0)", "10k" },
      { "ENGINEERINGNOTATION(-11; 1)", "-11 " }, { "ENGINEERINGNOTATION(-1000; 5)", "-1.000k" },
      { "ENGINEERINGNOTATION(-100000000; 0)", "-100M" }, { "ENGINEERINGNOTATION(0.0000101000; 5)", "10.100\u00b5" },

      { "ENGINEERINGNOTATION(1; 5; FALSE())", "1.00000 " }, { "ENGINEERINGNOTATION(100; 5; FALSE())", "100.00000 " },
      { "ENGINEERINGNOTATION(1000; 6; FALSE())", "1.000000k" }, { "ENGINEERINGNOTATION(10000; 0; FALSE())", "10k" },
      { "ENGINEERINGNOTATION(-11; 1; FALSE())", "-11.0 " }, { "ENGINEERINGNOTATION(-1000; 5; FALSE())", "-1.00000k" },
      { "ENGINEERINGNOTATION(-100000000; 0; FALSE())", "-100M" },
      { "ENGINEERINGNOTATION(0.0000101000; 5; FALSE())", "10.10000\u00b5" }, };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
