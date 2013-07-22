package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class MondrianDataFactoryWriteTest extends TestCase
{
  public MondrianDataFactoryWriteTest()
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


  public void testWriteBandedNullCubeName() throws Exception
  {
    final BandedMDXDataFactory df = new BandedMDXDataFactory();
    df.setCubeFileProvider(new DefaultCubeFileProvider("path/to/cube.xml", null));
    df.setDataSourceProvider(new JndiDataSourceProvider("dummy"));

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
    assertEquals(null, dataFactory.getCubeFileProvider().getCubeConnectionName());
    assertEquals("path/to/cube.xml", dataFactory.getCubeFileProvider().getDesignTimeFile());
  }

  public void testWriteBanded() throws Exception
  {
    final BandedMDXDataFactory df = new BandedMDXDataFactory();
    df.setCubeFileProvider(new DefaultCubeFileProvider("path/to/cube.xml", "test-connection"));
    df.setDataSourceProvider(new JndiDataSourceProvider("dummy"));

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
    assertEquals("test-connection", dataFactory.getCubeFileProvider().getCubeConnectionName());
    assertEquals("path/to/cube.xml", dataFactory.getCubeFileProvider().getDesignTimeFile());
  }


  public void testWriteDenormalized() throws Exception
  {
    final DenormalizedMDXDataFactory df = new DenormalizedMDXDataFactory();
    df.setCubeFileProvider(new DefaultCubeFileProvider("path/to/cube.xml", "test-connection"));
    df.setDataSourceProvider(new JndiDataSourceProvider("dummy"));

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
    assertEquals("test-connection", dataFactory.getCubeFileProvider().getCubeConnectionName());
    assertEquals("path/to/cube.xml", dataFactory.getCubeFileProvider().getDesignTimeFile());
  }

  public void testWriteLegacy() throws Exception
  {
    final LegacyBandedMDXDataFactory df = new LegacyBandedMDXDataFactory();
    df.setCubeFileProvider(new DefaultCubeFileProvider("path/to/cube.xml", "test-connection"));
    df.setDataSourceProvider(new JndiDataSourceProvider("dummy"));

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
    assertEquals("test-connection", dataFactory.getCubeFileProvider().getCubeConnectionName());
    assertEquals("path/to/cube.xml", dataFactory.getCubeFileProvider().getDesignTimeFile());
  }
}
