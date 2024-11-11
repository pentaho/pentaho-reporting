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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class CsvArrayFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][] {
      { "CSVARRAY(\"1,2,3\"; TRUE(); \",\"; \"'\")", new String[] { "1", "2", "3" } },
      { "CSVARRAY(\"1,2,3\"; FALSE(); \",\"; \"'\")", new String[] { "1", "2", "3" } },
      { "CSVARRAY(\"'1,2',3\"; TRUE(); \",\"; \"'\")", new String[] { "1,2", "3" } }
    };
  }

}
