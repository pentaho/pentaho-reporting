package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ArrayMidFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 3; 2))", new Object[]
                { new BigDecimal(3), Boolean.TRUE }},
            {"NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 1; 0))", new Object[]{}},
            {"NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 1; 10))", new Object[]
                { "7", new BigDecimal(2), new BigDecimal(3), Boolean.TRUE, "Hello"}},
        };
  }

}