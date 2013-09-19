package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;

public class DesignTimeDataSchemaModelTest extends TestCase
{
  public DesignTimeDataSchemaModelTest()
  {
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunWithInvalidQuery() throws ReportDataFactoryException
  {

    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add(new TableDataFactory("query", new DefaultTableModel()));
    cdf.add(new TableDataFactory("query", new DefaultTableModel()));

    final DataFactory tableDataFactory1 = cdf.getReference(0);
    final DataFactory tableDataFactory2 = cdf.getReference(1);

    final MasterReport report = new MasterReport();
    report.setDataFactory(cdf);
    report.setQuery("default");

    final DesignTimeDataSchemaModel model = new DesignTimeDataSchemaModel(report);
    assertFalse(model.isSelectedDataSource(tableDataFactory2, "query"));
    assertFalse(model.isSelectedDataSource(tableDataFactory1, "query"));
  }

  public void testRunWithValidQuery() throws ReportDataFactoryException
  {

    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add(new TableDataFactory("query", new DefaultTableModel()));
    cdf.add(new TableDataFactory("query", new DefaultTableModel()));

    final DataFactory tableDataFactory1 = cdf.getReference(0);
    final DataFactory tableDataFactory2 = cdf.getReference(1);

    final MasterReport report = new MasterReport();
    report.setDataFactory(cdf);
    report.setQuery("query");

    final DesignTimeDataSchemaModel model = new DesignTimeDataSchemaModel(report);
    assertFalse(model.isSelectedDataSource(tableDataFactory2, "query"));
    assertTrue(model.isSelectedDataSource(tableDataFactory1, "query"));
  }
}
