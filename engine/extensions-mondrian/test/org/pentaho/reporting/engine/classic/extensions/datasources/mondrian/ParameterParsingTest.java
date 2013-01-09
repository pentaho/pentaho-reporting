package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.util.Locale;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.01.11
 * Time: 15:59
 *
 * @author Thomas Morgner.
 */
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
    final String tQuery = compiler.translateAndLookup("SELECT ${test,string} AS ${testN,integer}, ${test}");
    assertEquals("SELECT \"tes{[t\" AS 100, tes{[t", tQuery);
  }
}
