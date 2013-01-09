package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.12.2009
 * Time: 18:09:53
 *
 * @author Thomas Morgner.
 */
public class ParameterTest extends TestCase
{
  public ParameterTest()
  {
  }

  public ParameterTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBoo() throws ReportDataFactoryException
  {
/*    final String query =
        "select NON EMPTY {[Measures].[Sales],[Measures].[Quantity] } ON COLUMNS,\n" +
        "  { [TopSelection], [Customers].[All Customers].[Other Customers]} ON ROWS\n" +
        "from [SteelWheelsSales]\n" +
        "where \n" +
        "(\n" +
        "Parameter(\"sLine\", [Product], \n" +
        "   [Product].[All Products].[Classic Cars]), \n" +
        "[Markets].[All Markets].[Japan],\n" +
        "[Time].[All Years].[2003]\n" +
        ")";
*/
    String query = "SELECT STRTOMEMBER(\"[Product].[All Products].[Classic Cars]\") ON 0 FROM [SteelWheelsSales]";
    final BandedMDXDataFactory mondrianDataFactory = new BandedMDXDataFactory();
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    provider.setDriver("org.hsqldb.jdbcDriver");
    provider.setUrl("jdbc:hsqldb:./sql/sampledata");
    mondrianDataFactory.setCubeFileProvider(new DefaultCubeFileProvider
        ("test/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml"));
    mondrianDataFactory.setDataSourceProvider(provider);
    mondrianDataFactory.setJdbcUser("sa");
    mondrianDataFactory.setJdbcPassword("");
    try
    {
      mondrianDataFactory.setQuery("default", query);
      mondrianDataFactory.initialize(new DesignTimeDataFactoryContext());

      final ParameterDataRow parameters = new ParameterDataRow(new String[]{"sLine"},
          new String[]{"[Product].[All Products].[Classic Cars]"});
      final CloseableTableModel tableModel = (CloseableTableModel) mondrianDataFactory.queryData("default",
          parameters);
      tableModel.close();
    }
    finally
    {

      mondrianDataFactory.close();
    }

  }
}
