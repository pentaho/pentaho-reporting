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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class SearchFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "SEARCH(\"b\";\"abcabc\")", new BigDecimal( 2 ) },
        { "SEARCH(\"b\";\"abcabcabc\"; 3)", new BigDecimal( 5 ) },
        { "SEARCH(\"d\";\"ABC\";1)", LibFormulaErrorValue.ERROR_NOT_FOUND_VALUE },
        { "SEARCH(\"b\";\"ABC\";1)", new BigDecimal( 2 ) },
        { "SEARCH(\"c?a\";\"abcabcda\")", new BigDecimal( 6 ) },
        { "SEARCH(\"e*o\";\"yes and no\")", new BigDecimal( 2 ) },
        { "SEARCH(\"b*c\";\"abcabcabc\")", new BigDecimal( 2 ) },
      };
  }
}
