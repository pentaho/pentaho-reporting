package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Olap4JDataFactoryWriteTest extends TestCase
{
  public Olap4JDataFactoryWriteTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }


  protected MasterReport postProcess(final MasterReport originalReport) throws Exception
  {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream(originalReport, bout);
    assertTrue(bout.getLength() > 0);

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource reportRes = mgr.createDirectly(bout.toByteArray(), MasterReport.class);
    return (MasterReport) reportRes.getResource();
  }

  public void testWriteBanded() throws Exception
  {
    final JndiConnectionProvider connectionProvider = new JndiConnectionProvider();
    connectionProvider.setConnectionPath("Dummy");
    final BandedMDXDataFactory df = new BandedMDXDataFactory(connectionProvider);

    df.setGlobalScript("GlobalScript");
    df.setGlobalScriptLanguage("GlobalScriptLanguage");
    df.setQuery("QueryName", "QueryText", "ScriptLanguage", "Script");
    final MasterReport report = new MasterReport();
    report.setDataFactory(df);

    final MasterReport masterReport = postProcess(report);
    final BandedMDXDataFactory dataFactory = (BandedMDXDataFactory) masterReport.getDataFactory();
    assertEquals("QueryName", dataFactory.getQueryNames()[0]);
    assertEquals("QueryText", dataFactory.getQuery("QueryName"));
    assertEquals("ScriptLanguage", dataFactory.getScriptingLanguage("QueryName"));
    assertEquals("Script", dataFactory.getScript("QueryName"));
    assertEquals("GlobalScript", dataFactory.getGlobalScript());
    assertEquals("GlobalScriptLanguage", dataFactory.getGlobalScriptLanguage());
  }


  public void testWriteDenormalized() throws Exception
  {
    final JndiConnectionProvider connectionProvider = new JndiConnectionProvider();
    connectionProvider.setConnectionPath("Dummy");
    final DenormalizedMDXDataFactory df = new DenormalizedMDXDataFactory(connectionProvider);

    df.setGlobalScript("GlobalScript");
    df.setGlobalScriptLanguage("GlobalScriptLanguage");
    df.setQuery("QueryName", "QueryText", "ScriptLanguage", "Script");
    final MasterReport report = new MasterReport();
    report.setDataFactory(df);

    final MasterReport masterReport = postProcess(report);
    final DenormalizedMDXDataFactory dataFactory = (DenormalizedMDXDataFactory) masterReport.getDataFactory();
    assertEquals("QueryName", dataFactory.getQueryNames()[0]);
    assertEquals("QueryText", dataFactory.getQuery("QueryName"));
    assertEquals("ScriptLanguage", dataFactory.getScriptingLanguage("QueryName"));
    assertEquals("Script", dataFactory.getScript("QueryName"));
    assertEquals("GlobalScript", dataFactory.getGlobalScript());
    assertEquals("GlobalScriptLanguage", dataFactory.getGlobalScriptLanguage());
  }

  public void testWriteLegacy() throws Exception
  {
    final JndiConnectionProvider connectionProvider = new JndiConnectionProvider();
    connectionProvider.setConnectionPath("Dummy");
    final LegacyBandedMDXDataFactory df = new LegacyBandedMDXDataFactory(connectionProvider);

    df.setGlobalScript("GlobalScript");
    df.setGlobalScriptLanguage("GlobalScriptLanguage");
    df.setQuery("QueryName", "QueryText", "ScriptLanguage", "Script");
    final MasterReport report = new MasterReport();
    report.setDataFactory(df);

    final MasterReport masterReport = postProcess(report);
    final LegacyBandedMDXDataFactory dataFactory = (LegacyBandedMDXDataFactory) masterReport.getDataFactory();
    assertEquals("QueryName", dataFactory.getQueryNames()[0]);
    assertEquals("QueryText", dataFactory.getQuery("QueryName"));
    assertEquals("ScriptLanguage", dataFactory.getScriptingLanguage("QueryName"));
    assertEquals("Script", dataFactory.getScript("QueryName"));
    assertEquals("GlobalScript", dataFactory.getGlobalScript());
    assertEquals("GlobalScriptLanguage", dataFactory.getGlobalScriptLanguage());
  }
}
