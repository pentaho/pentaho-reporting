package org.pentaho.reporting.engine.classic.core.bugs;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

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

  private static class TrackingStaticDataFactory extends StaticDataFactory
  {
    private TrackingStaticDataFactory()
    {
    }
  }

  public static class TrackingDataFactoryMetaData implements DataFactoryMetaData
  {
    private DataFactoryMetaData parent;

    public TrackingDataFactoryMetaData()
    {
      this.parent = DataFactoryRegistry.getInstance().getMetaData(StaticDataFactory.class.getName());
    }

    public Image getIcon(final Locale locale, final int iconKind)
    {
      return parent.getIcon(locale, iconKind);
    }

    public String[] getReferencedFields(final DataFactory element,
                                        final String queryName, final DataRow parameter)
    {
      if (SUBREPORT_QUERY.equals(queryName))
      {
        return new String[]{ "PA" };
      }
      return new String[0];
    }

    public ResourceReference[] getReferencedResources(final DataFactory element,
                                                      final ResourceManager resourceManager,
                                                      final String queryName,
                                                      final DataRow parameter)
    {
      return parent.getReferencedResources(element, resourceManager, queryName, parameter);
    }

    public boolean isEditable()
    {
      return parent.isEditable();
    }

    public boolean isEditorAvailable()
    {
      return parent.isEditorAvailable();
    }

    public boolean isFreeFormQuery()
    {
      return parent.isFreeFormQuery();
    }

    public boolean isFormattingMetaDataSource()
    {
      return parent.isFormattingMetaDataSource();
    }

    public DataSourcePlugin createEditor()
    {
      return parent.createEditor();
    }

    public String getDisplayConnectionName(final DataFactory dataFactory)
    {
      return parent.getDisplayConnectionName(dataFactory);
    }

    public Object getQueryHash(final DataFactory element,
                               final String queryName,
                               final DataRow parameter)
    {
      final ArrayList<Object> retval = new ArrayList<Object>();
      retval.add(queryName);
      retval.add(parameter.get("PA"));
      return retval;
    }

    public String getName()
    {
      return TrackingStaticDataFactory.class.getName();
    }

    public String getDisplayName(final Locale locale)
    {
      return parent.getDisplayName(locale);
    }

    public String getMetaAttribute(final String attributeName, final Locale locale)
    {
      return parent.getMetaAttribute(attributeName, locale);
    }

    public String getGrouping(final Locale locale)
    {
      return parent.getGrouping(locale);
    }

    public int getGroupingOrdinal(final Locale locale)
    {
      return parent.getGroupingOrdinal(locale);
    }

    public int getItemOrdinal(final Locale locale)
    {
      return parent.getItemOrdinal(locale);
    }

    public String getDeprecationMessage(final Locale locale)
    {
      return parent.getDeprecationMessage(locale);
    }

    public String getDescription(final Locale locale)
    {
      return parent.getDescription(locale);
    }

    public boolean isDeprecated()
    {
      return parent.isDeprecated();
    }

    public boolean isExpert()
    {
      return parent.isExpert();
    }

    public boolean isPreferred()
    {
      return parent.isPreferred();
    }

    public boolean isHidden()
    {
      return parent.isHidden();
    }

    public boolean isExperimental()
    {
      return parent.isExperimental();
    }

    public int getCompatibilityLevel()
    {
      return parent.getCompatibilityLevel();
    }
  }

  public static final String SUBREPORT_QUERY = "org.pentaho.reporting.engine.classic.core.bugs.Pre497Test#createSubReport(PA)";

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

    DataFactoryRegistry.getInstance().register(new TrackingDataFactoryMetaData());
  }

  public void testBug() throws ReportProcessingException
  {
    synchronized (Pre497Test.class)
    {
      querytracker = new HashSet();

      final MasterReport report = new MasterReport();
      report.setDataFactory(new TrackingStaticDataFactory());
      report.setQuery("org.pentaho.reporting.engine.classic.core.bugs.Pre497Test#createMasterReport");
      report.addExpression(new RandomValueExpression("MR"));

      final SubReport sreport = new SubReport();
      sreport.setQuery(SUBREPORT_QUERY);
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
      throw new IllegalStateException("MasterReport query has been called twice! Caching does not work.");
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
      throw new IllegalStateException("SubReport query for '" + value + "' has been called twice! Caching does not work.");
    }

    DebugLog.log("SubQuery " + value);
    final DefaultTableModel model = new DefaultTableModel(2, 1);
    model.setValueAt(value + "1", 0, 0);
    model.setValueAt(value + "2", 1, 0);
    querytracker.add(value);
    return model;
  }

}
