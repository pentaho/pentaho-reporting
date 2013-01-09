package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.testsupport.FormulaTestBase;

public class MParameterTextFunctionTest extends FormulaTestBase
{
  public MParameterTextFunctionTest()
  {
  }

  public MParameterTextFunctionTest(final String s)
  {
    super(s);
  }

  protected Object[][] createDataTest()
  {
    return new Object[][]{
        // plain-value behaviour must be same as PARAMETERTEXT

        {"MPARAMETERTEXT(DATE(2009;10;10); \"test\")", "2009-10-10T00%3A00%3A00.000%2B0000"},
        {"MPARAMETERTEXT(100000; \"test\")", "100000"},
        {"MPARAMETERTEXT(1000.001; \"test\")", "1000.001"},
        {"MPARAMETERTEXT(\"AAAA\"; \"test\"; TRUE())", "AAAA"},
        {"MPARAMETERTEXT(\"&:;\"; \"test\"; FALSE())", "&:;"},
        {"MPARAMETERTEXT(\"&:;\"; \"test\"; TRUE())", "%26%3A%3B"},

        {"MPARAMETERTEXT({ DATE(2009;10;10) | \"old\"}; \"test\")", "2009-10-10T00%3A00%3A00.000%2B0000&test=old"},
        {"MPARAMETERTEXT({100000 | \"old\"}; \"test\")", "100000&test=old"},
        {"MPARAMETERTEXT({1000.001 | \"old\"}; \"test\")", "1000.001&test=old"},
        {"MPARAMETERTEXT({\"AAAA\" | \"old\"}; \"test\"; TRUE())", "AAAA&test=old"},
        {"MPARAMETERTEXT({\"&:;\" | \"old\"}; \"test\"; FALSE())", "&:;&test=old"},
        {"MPARAMETERTEXT({\"&:;\" | \"old\"}; \"test\"; TRUE())", "%26%3A%3B&test=old"},

        {"MPARAMETERTEXT({\"AAAA\" | \"o:l:d\"}; \"t:e:st\"; TRUE())", "AAAA&t%3Ae%3Ast=o%3Al%3Ad"},
    };
  }

  public void testDefault() throws Exception
  {
    runDefaultTest();
  }
}
