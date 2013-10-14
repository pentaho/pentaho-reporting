package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

public class JndiDataSourceServiceTest extends TestCase
{
  public JndiDataSourceServiceTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    if (NamingManager.hasInitialContextFactoryBuilder() == false)
    {
      NamingManager.setInitialContextFactoryBuilder(new DebugJndiContextFactoryBuilder());
    }
  }

  public void testLookupSampleData()
  {
    JndiDataSourceService service = new JndiDataSourceService();
    final DataSource sampleData = service.getDataSource("SampleData");
    assertNotNull(sampleData);
    final DataSource sampleData2 = service.getDataSource("SampleData");
    assertNotNull(sampleData2);
    assertTrue(sampleData == sampleData2);
  }
}
