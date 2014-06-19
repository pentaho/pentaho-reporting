/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import java.util.Arrays;
import java.util.HashMap;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DataFactoryScriptSupportTest extends TestCase
{
  private String globalScript = "var globalScopeVariable = 'global'; \n" +
      "function init (dataRow)  \n" +
      "{ \n" +
      "  if (globalScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error';  \n" +
      "} \n" +
      "\n" +
      "function shutdown ()  \n" +
      "{ \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "} \n" +
      "\n";

  private String queryScript = "var localScopeVariable = 'local'; \n" +
      "function initQuery ()  \n" +
      "{ \n" +
      "  println ('Init ');\n" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (globalScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error';  \n" +
      "} \n" +
      "\n" +
      "function computeQueryFields (query, queryName)  \n" +
      "{ \n" +
      "  println ('computeQueryFields ' + query);\n" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (globalScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "  return ['one', 'two'];\n" +
      "} \n" +
      "\n" +
      "function computeQuery (query, queryName, dataRow)  \n" +
      "{ \n" +
      "  println ('computeQuery ' + query);" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (globalScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "  return 'result'; \n" +
      "} \n" +
      "\n" +
      "function shutdown ()  \n" +
      "{ \n" +
      "  println ('shutdown ');" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (globalScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "} \n" +
      "\n";

  private String queryScript2 = "var localScopeVariable = 'local'; \n" +
      "function initQuery ()  \n" +
      "{ \n" +
      "  println ('Init ');\n" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error';  \n" +
      "} \n" +
      "\n" +
      "function computeQueryFields (query, queryName)  \n" +
      "{ \n" +
      "  println ('computeQueryFields ' + query);\n" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "  return ['one', 'two'];\n" +
      "} \n" +
      "\n" +
      "function computeQuery (query, queryName, dataRow)  \n" +
      "{ \n" +
      "  println ('computeQuery ' + query);" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "  return 'result'; \n" +
      "} \n" +
      "\n" +
      "function shutdown ()  \n" +
      "{ \n" +
      "  println ('shutdown ');" +
      "  if (localScopeVariable == null) throw 'error';  \n" +
      "  if (resourceManager == null) throw 'error';  \n" +
      "  if (dataFactory == null) throw 'error';  \n" +
      "  if (configuration == null) throw 'error'; \n" +
      "  if (contextKey == null) throw 'error';  \n" +
      "  if (resourceBundleFactory == null) throw 'error'; \n" +
      "} \n" +
      "\n";


  public DataFactoryScriptSupportTest()
  {
  }

  public DataFactoryScriptSupportTest(final String name)
  {
    super(name);
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSimpleSetup() throws ReportDataFactoryException
  {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.setGlobalScriptLanguage("JavaScript");
    support.setGlobalScript(globalScript);
    support.initialize(new TableDataFactory(), new DesignTimeDataFactoryContext
        (ClassicEngineBoot.getInstance().getGlobalConfig(), mgr,
        new ResourceKey("dummy", "dummy", new HashMap()), new DefaultResourceBundleFactory()));
    support.shutdown();
  }

  public void testQuerySetup() throws ReportDataFactoryException
  {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.setGlobalScriptLanguage("JavaScript");
    support.setGlobalScript(globalScript);
    support.initialize(new TableDataFactory(), new DesignTimeDataFactoryContext
        (ClassicEngineBoot.getInstance().getGlobalConfig(), mgr,
        new ResourceKey("dummy", "dummy", new HashMap()), new DefaultResourceBundleFactory()));
    support.setQuery("test", "test-query", null, null);
    support.setQuery("test-script", "test-query-2", "JavaScript", queryScript);

    assertEquals("test-query", support.computeQuery("test", new ParameterDataRow()));
    assertEqualsArray(null, support.computeAdditionalQueryFields("test", new ParameterDataRow()));
    assertEquals("result", support.computeQuery("test-script", new ParameterDataRow()));
    assertEqualsArray(null, support.computeAdditionalQueryFields("test-script", new ParameterDataRow()));
    support.shutdown();
  }

  public void testQueryOnly() throws ReportDataFactoryException
  {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataFactoryScriptingSupport support = new DataFactoryScriptingSupport();
    support.initialize(new TableDataFactory(), new DesignTimeDataFactoryContext
        (ClassicEngineBoot.getInstance().getGlobalConfig(), mgr,
        new ResourceKey("dummy", "dummy", new HashMap()), new DefaultResourceBundleFactory()));
    support.setQuery("test", "test-query", null, null);
    support.setQuery("test-script", "test-query-2", "JavaScript", queryScript2);

    assertEquals("test-query", support.computeQuery("test", new ParameterDataRow()));
    assertEqualsArray(null, support.computeAdditionalQueryFields("test", new ParameterDataRow()));
    assertEquals("result", support.computeQuery("test-script", new ParameterDataRow()));
    assertEqualsArray(null, support.computeAdditionalQueryFields("test-script", new ParameterDataRow()));
    support.shutdown();
  }

  private void assertEqualsArray(final String[] o, final String[] strings)
  {
    if (o == null)
    {
      System.out.println(Arrays.asList(strings));
      return;
    }
    assertNotNull(strings);
    assertEquals(Arrays.asList(o), Arrays.asList(strings));
  }
}
