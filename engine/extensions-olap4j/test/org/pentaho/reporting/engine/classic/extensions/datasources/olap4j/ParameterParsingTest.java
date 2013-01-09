package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import java.util.Locale;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;

public class ParameterParsingTest extends TestCase
{
  public ParameterParsingTest()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameter()
  {
    final StaticDataRow dataRow = new StaticDataRow(new String[]{"test", "testN"}, new Object[]{"tes{[t", 100});
    AbstractMDXDataFactory.MDXCompiler compiler = new AbstractMDXDataFactory.MDXCompiler(dataRow, Locale.US);
    assertEquals("SELECT \"tes{[t\" AS 100, tes{[t", compiler.translateAndLookup("SELECT ${test,string} AS ${testN,integer}, ${test}"));
  }
}
