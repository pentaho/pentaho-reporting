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

package org.pentaho.reporting.engine.classic.core.designtime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryDesignTimeSupport;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironmentMapping;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.cache.IndexedTableModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.states.datarow.EmptyTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DesignTimeDataSchemaModel implements ContextAwareDataSchemaModel
{
  public static class DefaultDesignTimeDataSchemaModelChangeTracker
      implements DesignTimeDataSchemaModelChangeTracker {

    private final HashMap<InstanceID, Long> nonVisualChangeTrackers;
    private final HashMap<InstanceID, Long> dataFactoryChangeTrackers;
    private final AbstractReportDefinition parent;
    private String query;
    private int queryTimeout;

    private DefaultDesignTimeDataSchemaModelChangeTracker(final AbstractReportDefinition parent)
    {
      this.parent = parent;
      this.nonVisualChangeTrackers = new HashMap<InstanceID, Long>();
      this.dataFactoryChangeTrackers = new HashMap<InstanceID, Long>();
      this.queryTimeout = parent.getQueryTimeout();
    }

    private boolean isNonVisualsChanged()
    {
      AbstractReportDefinition parent = this.parent;
      while (parent != null)
      {
        final InstanceID id = parent.getObjectID();
        final Long dataSourceChangeTracker = parent.getDatasourceChangeTracker();
        if (dataSourceChangeTracker.equals(dataFactoryChangeTrackers.get(id)) == false)
        {
          return true;
        }

        final Long nonVisualsChangeTracker = parent.getNonVisualsChangeTracker();
        if (nonVisualsChangeTracker.equals(nonVisualChangeTrackers.get(id)) == false)
        {
          return true;
        }

        final Section parentSection = parent.getParentSection();
        if (parentSection == null)
        {
          parent = null;
        }
        else
        {
          parent = (AbstractReportDefinition) parentSection.getReportDefinition();
        }
      }
      return false;
    }

    private boolean isDataFactoryChanged()
    {
      AbstractReportDefinition parent = this.parent;
      while (parent != null)
      {
        final InstanceID id = parent.getObjectID();
        final Long dataSourceChangeTracker = parent.getDatasourceChangeTracker();
        if (dataSourceChangeTracker.equals(dataFactoryChangeTrackers.get(id)) == false)
        {
          return true;
        }

        final Section parentSection = parent.getParentSection();
        if (parentSection == null)
        {
          parent = null;
        }
        else
        {
          parent = (AbstractReportDefinition) parentSection.getReportDefinition();
        }
      }
      return false;
    }

    public void updateChangeTrackers()
    {
      this.query = parent.getQuery();
      this.queryTimeout = parent.getQueryTimeout();
      AbstractReportDefinition parent = this.parent;
      while (parent != null)
      {
        final InstanceID id = parent.getObjectID();
        dataFactoryChangeTrackers.put(id, parent.getDatasourceChangeTracker());
        nonVisualChangeTrackers.put(id, parent.getNonVisualsChangeTracker());

        final Section parentSection = parent.getParentSection();
        if (parentSection == null)
        {
          parent = null;
        }
        else
        {
          parent = (AbstractReportDefinition) parentSection.getReportDefinition();
        }
      }
    }

    public boolean isReportQueryChanged()
    {
      return ObjectUtilities.equal(this.query, parent.getQuery()) == false ||
              queryTimeout != parent.getQueryTimeout() ||
              isDataFactoryChanged();
    }

    public boolean isReportChanged() {
      return isNonVisualsChanged() || ObjectUtilities.equal(this.query, parent.getQuery()) == false;
    }
  }

  private static final Log logger = LogFactory.getLog(DesignTimeDataSchemaModel.class);
  private static final String[] EMPTY_NAMES = new String[0];

  private final AbstractReportDefinition parent;
  private final DataSchemaDefinition dataSchemaDefinition;
  private final DataAttributeContext dataAttributeContext;
  private final MasterReport masterReportElement;
  private DataSchema dataSchema;
  private String[] columnNames;
  private OfflineTableModel offlineTableModel;
  private Throwable dataFactoryException;
  private final DesignTimeDataSchemaModelChangeTracker changeTracker;

  public DesignTimeDataSchemaModel(final AbstractReportDefinition report)
  {
    this((MasterReport) report.getMasterReport(), report);
  }

  public DesignTimeDataSchemaModel(final MasterReport masterReportElement,
                                   final AbstractReportDefinition report)
  {
    ArgumentNullException.validate("masterReportElement", masterReportElement);
    ArgumentNullException.validate("report", report);

    this.columnNames = EMPTY_NAMES;
    this.masterReportElement = masterReportElement;
    this.parent = report;

    this.changeTracker = createChangeTracker();
    this.dataSchemaDefinition = createDataSchemaDefinition(masterReportElement);
    this.dataAttributeContext = new DefaultDataAttributeContext();
  }

  protected DesignTimeDataSchemaModelChangeTracker createChangeTracker() {
    return new DefaultDesignTimeDataSchemaModelChangeTracker(getParent());
  }

  protected DataSchemaDefinition createDataSchemaDefinition(final MasterReport masterReportElement)
  {
    DataSchemaDefinition dataSchemaDefinition = masterReportElement.getDataSchemaDefinition();
    if (dataSchemaDefinition == null)
    {
      return DataSchemaUtility.parseDefaults(masterReportElement.getResourceManager());
    }
    return dataSchemaDefinition;
  }

  public DataAttributeContext getDataAttributeContext()
  {
    return dataAttributeContext;
  }

  public AbstractReportDefinition getParent()
  {
    return parent;
  }

  public boolean isValid()
  {
    ensureDataSchemaValid();
    return dataFactoryException == null;
  }

  public DataSchema getDataSchema()
  {
    ensureDataSchemaValid();

    return dataSchema;
  }

  private void ensureDataSchemaValid()
  {
    if (dataSchema == null || changeTracker.isReportChanged())
    {
      try
      {
        this.dataFactoryException = null;
        this.dataSchema = buildDataSchema();
      }
      catch (final Throwable e)
      {
        handleError(e);
        this.dataFactoryException = e;
        this.dataSchema = new DefaultDataSchema();
      }
      changeTracker.updateChangeTrackers();
    }
  }

  protected void handleError(final Throwable e)
  {
    logger.debug("Failure in DataSchema", e);
  }

  public Throwable getDataFactoryException()
  {
    return dataFactoryException;
  }

  protected DataSchema buildDataSchema() throws ReportDataFactoryException
  {
    this.columnNames = EMPTY_NAMES;
    this.dataFactoryException = null;

    final ParameterDefinitionEntry[] parameterDefinitions;
    final ParameterDataRow parameterRow;
    if (parent instanceof MasterReport)
    {
      final MasterReport mr = (MasterReport) parent;
      parameterDefinitions = mr.getParameterDefinition().getParameterDefinitions();
      final LinkedMap values = computeParameterValueSet(mr);
      parameterRow = new ParameterDataRow((String[]) values.keys(new String[values.size()]), values.values());
    }
    else if (parent instanceof SubReport)
    {
      final SubReport sr = (SubReport) parent;
      final ParameterMapping[] inputMappings = sr.getInputMappings();
      final Object[] values = new Object[inputMappings.length];
      final String[] names = new String[inputMappings.length];
      parameterDefinitions = null;
      for (int i = 0; i < inputMappings.length; i++)
      {
        final ParameterMapping inputMapping = inputMappings[i];
        names[i] = inputMapping.getAlias();
      }
      parameterRow = new ParameterDataRow(names, values);
    }
    else
    {
      parameterDefinitions = null;
      parameterRow = new ParameterDataRow();
    }

    final Expression[] expressions = parent.getExpressions().getExpressions();
    final DataSchemaCompiler dataSchemaCompiler =
        new DataSchemaCompiler(dataSchemaDefinition, dataAttributeContext, masterReportElement.getResourceManager());

    try
    {
      final CachingDataFactory dataFactory = new CachingDataFactory(createDataFactory(parent), true);
      final MasterReport masterReport = masterReportElement;

      dataFactory.initialize(new DesignTimeDataFactoryContext(masterReport));

      try
      {
        final TableModel reportData = queryReportData(parent.getQuery(), parent.getQueryTimeout(), dataFactory);
        final DataSchema dataSchema = dataSchemaCompiler.compile
            (reportData, expressions, parameterRow, parameterDefinitions, masterReport.getReportEnvironment());
        this.columnNames = collectColumnNames(reportData, parameterRow, expressions);
        if (reportData instanceof CloseableTableModel)
        {
          final CloseableTableModel ctm = (CloseableTableModel) reportData;
          ctm.close();
        }
        return dataSchema;
      }
      finally
      {
        dataFactory.close();
      }
    }
    catch (final ReportProcessingException e)
    {
      final TableModel reportData = new DefaultTableModel();
      final DataSchema dataSchema = dataSchemaCompiler.compile
          (reportData, expressions, parameterRow, parameterDefinitions, masterReportElement.getReportEnvironment());
      this.columnNames = collectColumnNames(reportData, parameterRow, expressions);
      this.dataFactoryException = e;
      return dataSchema;
    }
  }

  private CompoundDataFactory createDataFactory(AbstractReportDefinition reportDefinition)
      throws ReportDataFactoryException
  {
    final CompoundDataFactory cdf = new CompoundDataFactory();
    while (reportDefinition != null)
    {
      final DataFactory dataFactory = reportDefinition.getDataFactory();
      if (dataFactory != null)
      {
        cdf.add(dataFactory);
      }
      final Section parentSection = reportDefinition.getParentSection();
      if (parentSection == null)
      {
        reportDefinition = null;
      }
      else
      {
        reportDefinition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }

    return CompoundDataFactory.normalize(cdf);
  }

  private TableModel queryReportData(final String query,
                                     final int queryTimeout,
                                     final DataFactory dataFactory)
      throws ReportDataFactoryException
  {
    if (offlineTableModel == null || changeTracker.isReportQueryChanged())
    {
      TableModel reportData = null;
      try
      {
        if (query == null)
        {
          reportData = new EmptyTableModel();
        }
        else if (dataFactory instanceof DataFactoryDesignTimeSupport)
        {
          final DataFactoryDesignTimeSupport dts = (DataFactoryDesignTimeSupport) dataFactory;
          reportData = dts.queryDesignTimeStructure(query, new QueryDataRowWrapper(new StaticDataRow(), 1, queryTimeout));
        }
        else
        {
          reportData = dataFactory.queryData(query, new QueryDataRowWrapper(new StaticDataRow(), 1, queryTimeout));
        }

        offlineTableModel = new OfflineTableModel(reportData, new DefaultDataAttributeContext());
      }
      finally
      {
        if (reportData instanceof CloseableTableModel)
        {
          final CloseableTableModel ctm = (CloseableTableModel) reportData;
          ctm.close();
        }
      }
    }
    if (offlineTableModel == null)
    {
      return new IndexedTableModel(new DefaultTableModel());
    }
    return offlineTableModel;
  }

  private String[] collectColumnNames(final TableModel reportData,
                                      final ParameterDataRow parameterRow,
                                      final Expression[] expressions)
  {

    final LinkedMap columnNamesCollector = new LinkedMap();

    final Map<String, String> envCols = DefaultReportEnvironmentMapping.INSTANCE.createEnvironmentMapping();
    final Object[] envColArray = envCols.values().toArray();
    for (int i = 0; i < envColArray.length; i++)
    {
      final String name = (String) envColArray[i];
      columnNamesCollector.put(name, Boolean.TRUE);
    }

    final String[] strings = parameterRow.getColumnNames();
    for (int i = 0; i < strings.length; i++)
    {
      final String string = strings[i];
      columnNamesCollector.put(string, Boolean.TRUE);
    }

    final int count = reportData.getColumnCount();
    for (int i = 0; i < count; i++)
    {
      columnNamesCollector.put(reportData.getColumnName(i), Boolean.TRUE);
    }
    for (int i = 0; i < expressions.length; i++)
    {
      final Expression expression = expressions[i];
      final String name = expression.getName();
      if (name != null)
      {
        columnNamesCollector.put(name, Boolean.TRUE);
      }
    }
    return (String[]) columnNamesCollector.keys(new String[columnNamesCollector.size()]);
  }

  public String[] getColumnNames()
  {
    ensureDataSchemaValid();

    return columnNames.clone();
  }

  public boolean isSelectedDataSource(final DataFactory dataFactory,
                                      final String queryName)
  {
    ensureDataSchemaValid();

    if (ObjectUtilities.equal(queryName, parent.getQuery()) == false)
    {
      // the query/datasource combination given in the parameter cannot be a selected
      // combination if the query does not match the report's active query ..
      return false;
    }

    AbstractReportDefinition reportDefinition = this.getParent();
    while (reportDefinition != null)
    {
      final DataFactory reportDataFactory = reportDefinition.getDataFactory();
      if (reportDataFactory instanceof CompoundDataFactory)
      {
        final CompoundDataFactory compoundDataFactory = (CompoundDataFactory) reportDataFactory;
        for (int i = 0; i < compoundDataFactory.size(); i++)
        {
          final DataFactory df = compoundDataFactory.getReference(i);
          for (final String query : df.getQueryNames())
          {
            if (!query.equals(queryName))
            {
              continue;
            }

            if (df == dataFactory)
            {
              return true;
            }
            else
            {
              return false;
            }
          }
        }
      }
      else
      {
        if (reportDataFactory != null)
        {
          for (final String query : reportDataFactory.getQueryNames())
          {
            if (!query.equals(queryName))
            {
              continue;
            }

            if (reportDataFactory == dataFactory)
            {
              return true;
            }
            else
            {
              return false;
            }
          }
          return true;
        }

      }
      final Section parentSection = reportDefinition.getParentSection();
      if (parentSection == null)
      {
        reportDefinition = null;
      }
      else
      {
        reportDefinition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }

    return false;
  }


  public static LinkedMap computeParameterValueSet(final MasterReport report)
  {
    final LinkedMap retval = new LinkedMap();
    retval.put(MasterReport.REPORT_DATE_PROPERTY, new Date());

    final ReportParameterValues reportParameterValues = report.getParameterValues();
    final ParameterDefinitionEntry[] columnNames = report.getParameterDefinition().getParameterDefinitions();
    for (int i = 0; i < columnNames.length; i++)
    {
      final ParameterDefinitionEntry parameter = columnNames[i];
      final String columnName = parameter.getName();
      if (columnName == null)
      {
        continue;
      }
      retval.put(columnName, reportParameterValues.get(columnName));
    }
    return retval;
  }

}
