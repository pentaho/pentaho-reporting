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


package org.pentaho.reporting.libraries.formula;

public class Prd5174Test extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  protected Object[][] createDataTest() {
    return new Object[][]
      {
        { "\"PreFix:\" & ({1 | 2 | 3}) & \":PostFix\"", "PreFix:1, 2, 3:PostFix" },
        { "\"PreFix:\" & [.B18] & \":PostFix\"", "PreFix:1, 2, 3:PostFix" },
        { "\"PreFix:\" & [.C18] & \":PostFix\"", "PreFix:1, 2, 3:PostFix" },
      };

  }
}
