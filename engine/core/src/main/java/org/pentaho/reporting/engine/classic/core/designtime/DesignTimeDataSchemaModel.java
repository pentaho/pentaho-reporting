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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryDesignTimeSupport;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.cache.IndexedTableModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.sorting.SortOrderReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.sorting.SortingDataFactory;
import org.pentaho.reporting.engine.classic.core.states.NoOpPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.states.datarow.EmptyTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Date;
import java.util.List;

public class DesignTimeDataSchemaModel extends AbstractDesignTimeDataSchemaModel {

  private static final Log logger = LogFactory.getLog( DesignTimeDataSchemaModel.class );

  private DataSchema dataSchema;
  private OfflineTableModel offlineTableModel;
  private Throwable dataFactoryException;
  private final DesignTimeDataSchemaModelChangeTracker changeTracker;

  public DesignTimeDataSchemaModel( final AbstractReportDefinition report ) {
    this( (MasterReport) report.getMasterReport(), report );
  }

  public DesignTimeDataSchemaModel( final MasterReport masterReportElement, final AbstractReportDefinition report ) {
    super( masterReportElement, report );
    this.changeTracker = createChangeTracker();
  }

  protected DesignTimeDataSchemaModelChangeTracker createChangeTracker() {
    return new DefaultDesignTimeDataSchemaModelChangeTracker( getParent() );
  }

  public AbstractReportDefinition getParent() {
    return getReport();
  }

  public boolean isValid() {
    ensureDataSchemaValid();
    return dataFactoryException == null;
  }

  public DataSchema getDataSchema() {
    ensureDataSchemaValid();

    return dataSchema;
  }

  private void ensureDataSchemaValid() {
    if ( dataSchema == null || changeTracker.isReportChanged() ) {
      try {
        this.dataFactoryException = null;
        this.dataSchema = buildDataSchema();
      } catch ( final Throwable e ) {
        handleError( e );
        this.dataFactoryException = e;
        this.dataSchema = new DefaultDataSchema();
      }
      changeTracker.updateChangeTrackers();
    }
  }

  protected void handleError( final Throwable e ) {
    logger.debug( "Failure in DataSchema", e );
  }

  public Throwable getDataFactoryException() {
    return dataFactoryException;
  }

  protected DataSchema buildDataSchema() throws ReportDataFactoryException {
    this.dataFactoryException = null;

    AbstractReportDefinition parent = getReport();
    final ParameterDataRow parameterRow = computeParameterData();
    final ParameterDefinitionEntry[] parameterDefinitions = computeParameterDefinitionEntries();

    final Expression[] expressions = parent.getExpressions().getExpressions();
    final DataSchemaCompiler dataSchemaCompiler =
        new DataSchemaCompiler( getDataSchemaDefinition(), getDataAttributeContext(), getMasterReportElement()
            .getResourceManager() );

    try {
      final CachingDataFactory dataFactory =
          new CachingDataFactory( new SortingDataFactory( createDataFactory( parent ),
              new NoOpPerformanceMonitorContext() ), true );
      final MasterReport masterReport = getMasterReportElement();

      dataFactory.initialize( new DesignTimeDataFactoryContext( masterReport ) );

      try {
        List<SortConstraint> sortConstraints = new SortOrderReportPreProcessor().computeSortConstraints( parent );
        final TableModel reportData =
            queryReportData( parent.getQuery(), parent.getQueryTimeout(), dataFactory, sortConstraints );
        final DataSchema dataSchema =
            dataSchemaCompiler.compile( reportData, expressions, parameterRow, parameterDefinitions, masterReport
                .getReportEnvironment() );
        // this.columnNames = collectColumnNames(reportData, parameterRow, expressions);
        if ( reportData instanceof CloseableTableModel ) {
          final CloseableTableModel ctm = (CloseableTableModel) reportData;
          ctm.close();
        }
        return dataSchema;
      } finally {
        dataFactory.close();
      }
    } catch ( final ReportProcessingException e ) {
      final TableModel reportData = new DefaultTableModel();
      final DataSchema dataSchema =
          dataSchemaCompiler.compile( reportData, expressions, parameterRow, parameterDefinitions,
              getMasterReportElement().getReportEnvironment() );
      this.dataFactoryException = e;
      return dataSchema;
    }
  }

  private CompoundDataFactory createDataFactory( AbstractReportDefinition reportDefinition )
    throws ReportDataFactoryException {
    final CompoundDataFactory cdf = new CompoundDataFactory();
    while ( reportDefinition != null ) {
      final DataFactory dataFactory = reportDefinition.getDataFactory();
      if ( dataFactory != null ) {
        cdf.add( dataFactory );
      }
      final Section parentSection = reportDefinition.getParentSection();
      if ( parentSection == null ) {
        reportDefinition = null;
      } else {
        reportDefinition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }

    return CompoundDataFactory.normalize( cdf );
  }

  private TableModel queryReportData( final String query, final int queryTimeout, final DataFactory dataFactory,
      final List<SortConstraint> sortConstraints ) throws ReportDataFactoryException {
    if ( offlineTableModel == null || changeTracker.isReportQueryChanged() ) {
      TableModel reportData = null;
      try {
        if ( query == null ) {
          reportData = new EmptyTableModel();
        } else if ( dataFactory instanceof DataFactoryDesignTimeSupport ) {
          final DataFactoryDesignTimeSupport dts = (DataFactoryDesignTimeSupport) dataFactory;
          reportData =
              dts.queryDesignTimeStructure( query, new QueryDataRowWrapper( new StaticDataRow(), queryTimeout, 1,
                  sortConstraints ) );
        } else {
          reportData =
              dataFactory.queryData( query, new QueryDataRowWrapper( new StaticDataRow(), queryTimeout, 1,
                  sortConstraints ) );
        }

        offlineTableModel = new OfflineTableModel( reportData, new DefaultDataAttributeContext() );
      } finally {
        if ( reportData instanceof CloseableTableModel ) {
          final CloseableTableModel ctm = (CloseableTableModel) reportData;
          ctm.close();
        }
      }
    }
    if ( offlineTableModel == null ) {
      return new IndexedTableModel( new DefaultTableModel() );
    }
    return offlineTableModel;
  }

  @Deprecated
  public boolean isSelectedDataSource( final DataFactory dataFactory, final String queryName ) {
    return DesignTimeUtil.isSelectedDataSource( getReport(), dataFactory, queryName );
  }

  @Deprecated
  public static LinkedMap computeParameterValueSet( final MasterReport report ) {
    final LinkedMap retval = new LinkedMap();
    retval.put( MasterReport.REPORT_DATE_PROPERTY, new Date() );

    final ReportParameterValues reportParameterValues = report.getParameterValues();
    final ParameterDefinitionEntry[] columnNames = report.getParameterDefinition().getParameterDefinitions();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final ParameterDefinitionEntry parameter = columnNames[i];
      final String columnName = parameter.getName();
      if ( columnName == null ) {
        continue;
      }
      retval.put( columnName, reportParameterValues.get( columnName ) );
    }
    return retval;
  }

}
