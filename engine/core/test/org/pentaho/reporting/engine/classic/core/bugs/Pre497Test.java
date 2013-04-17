package org.pentaho.reporting.engine.classic.core.bugs;

import java.util.HashSet;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class Pre497Test extends TestCase
{
  private static class RandomValueExpression extends AbstractExpression
  {
    private static int tracker;
    
    private RandomValueExpression(final String name)
    {
      setName(name);
    }

    /**
     * Return the current expression value.
     * <p/>
     * The value depends (obviously) on the expression implementation.
     *
     * @return the value of the function.
     */
    public Object getValue()
    {
      return new Integer(tracker++);
    }
  }

  private static HashSet querytracker;

  public Pre497Test()
  {
  }

  public Pre497Test(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBug() throws ReportProcessingException
  {
    synchronized (Pre497Test.class)
    {
      querytracker = new HashSet();

      final MasterReport report = new MasterReport();
      report.setDataFactory(new StaticDataFactory());
      report.setQuery("org.pentaho.reporting.engine.classic.core.bugs.Pre497Test#createMasterReport");
      report.addExpression(new RandomValueExpression("MR"));

      final SubReport sreport = new SubReport();
      sreport.setQuery("org.pentaho.reporting.engine.classic.core.bugs.Pre497Test#createSubReport(PA)");
      sreport.addInputParameter("A", "PA");
      sreport.addExpression(new RandomValueExpression("SR"));
      report.getItemBand().addSubReport(sreport);

      DebugReportRunner.createPDF(report);

      querytracker = null;
    }
  }

  public static TableModel createMasterReport()
  {
    if (querytracker.contains("MR"))
    {
      throw new IllegalStateException();
    }
    final DefaultTableModel model = new DefaultTableModel(2, 1);
    model.setValueAt("1", 0, 0);
    model.setValueAt("2", 1, 0);

    DebugLog.log("Master-Query ");
    querytracker.add("MR");
    return model;
  }

  public static TableModel createSubReport(final String value)
  {
    if (value == null)
    {
      throw new NullPointerException();
    }
    if (querytracker.contains(value))
    {
      throw new IllegalStateException("Duplicate query");
    }

    DebugLog.log("SubQuery " + value);
    final DefaultTableModel model = new DefaultTableModel(2, 1);
    model.setValueAt(value + "1", 0, 0);
    model.setValueAt(value + "2", 1, 0);
    querytracker.add(value);
    return model;
  }

}
