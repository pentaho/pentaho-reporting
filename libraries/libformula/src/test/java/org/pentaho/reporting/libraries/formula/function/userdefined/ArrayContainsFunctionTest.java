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



package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ArrayContainsFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ARRAYCONTAINS([.B3:.B8]; \"7\")", Boolean.TRUE, },
        { "ARRAYCONTAINS([.B3:.B8]; \"A\")", Boolean.FALSE, },
        { "ARRAYCONTAINS([.B3:.B8]; \"7\"; 2; 3)", Boolean.TRUE },
        { "ARRAYCONTAINS([.B3:.B8]; \"7\"; 2; 4)", Boolean.FALSE },
        { "ARRAYCONTAINS([.B3:.B8]; \"A\"; 2; 3)", Boolean.FALSE },
        { "ARRAYCONTAINS([.B3:.B8]; \"7\"; 4; 3)", Boolean.FALSE },
      };
  }

}
