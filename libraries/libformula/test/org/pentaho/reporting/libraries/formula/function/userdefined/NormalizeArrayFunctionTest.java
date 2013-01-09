package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

public class NormalizeArrayFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public void testRecursiveSequence() throws EvaluationException
  {
    final TestFormulaContext context = new TestFormulaContext();
    final RecursiveSequence sequence = new RecursiveSequence(new Object[]{new String[0], new Integer[0], "A"}, context);
    while (sequence.hasNext())
    {
      System.out.println (sequence.next());
    }
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"NORMALIZEARRAY({11 | 21 | [.B18] | [.C19]})", new Object[]
                { new BigDecimal(11),  new BigDecimal(21),  new BigDecimal(1),
                    new BigDecimal(2),  new BigDecimal(3), new BigDecimal(42),  new BigDecimal(43)}},
            {"NORMALIZEARRAY({11 | 21 | [.B18] | [.B19]})", new Object[]
                { new BigDecimal(11),  new BigDecimal(21),  new BigDecimal(1),  new BigDecimal(2),  new BigDecimal(3)}},
            {"NORMALIZEARRAY({11 | 21 | [.B18]})", new Object[]
                { new BigDecimal(11),  new BigDecimal(21),  new BigDecimal(1),  new BigDecimal(2),  new BigDecimal(3)}},
        };
  }

}