package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ArrayLeftFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 2))", new Object[]
                { "7", new BigDecimal(2), }},
            {"NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 0))", new Object[]{}},
            {"NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 10))", new Object[]
                { "7", new BigDecimal(2), new BigDecimal(3), Boolean.TRUE, "Hello"}},
        };
  }

}