package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import javax.naming.spi.NamingManager;
import javax.swing.table.TableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

public class EmptyCellsetTest extends TestCase
{

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    if (NamingManager.hasInitialContextFactoryBuilder() == false)
    {
      NamingManager.setInitialContextFactoryBuilder(new DebugJndiContextFactoryBuilder());
    }
  }


  /**
   *  Validates that queries with empty results (no rows or no columns)
   *  are correctly handled by CachingDataFactory.
   *
   *   http://jira.pentaho.com/browse/PRD-4628
   */
  public void testEmptyResult() throws ReportDataFactoryException
  {
    final String query = "SELECT NON EMPTY [Product].[All Products].[Classic Cars]"
                         + ".[Highway 66 Mini Classics].[1985 Toyota Supra] "
                         + "on 0 from SteelWheelsSales where measures.Sales\n";
    DataFactory dataFactory = createDataFactory(query);
    final TableModel tableModel = ((CachingDataFactory)dataFactory)
        .queryStatic("default", new ParameterDataRow());
    assertEquals("results should be empty, rowcount should be 0.",
        0, tableModel.getRowCount());
    assertEquals("results should be empty, columncount should be 0",
        0, tableModel.getColumnCount());
  }

  protected DataFactory createDataFactory(final String query) throws ReportDataFactoryException
  {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver("org.hsqldb.jdbcDriver");
    provider.setUrl("jdbc:hsqldb:./sql/sampledata");

    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    mondrianDataFactory.setCubeFileProvider(new DefaultCubeFileProvider
        ("test/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml"));
    mondrianDataFactory.setDataSourceProvider(provider);
    mondrianDataFactory.setJdbcUser("sa");
    mondrianDataFactory.setJdbcPassword("");
    mondrianDataFactory.setQuery("default", query, null, null);
    CachingDataFactory cachingFactory = new CachingDataFactory(mondrianDataFactory, true);

    return cachingFactory;
  }
}
