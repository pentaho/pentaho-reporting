/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;

import javax.swing.table.TableModel;

public interface MasterDataRow {
  public ResourceBundleFactory getResourceBundleFactory();

  public DataFactory getDataFactory();

  public DataSchemaDefinition getDataSchemaDefinition();

  public DataSchema getDataSchema();

  public void resetDataSchema();

  public ReportDataRow getReportDataRow();

  public TableModel getReportData();

  public int getCursor();

  public int getRawDataCursor();

  public int getRowCount();

  public ExpressionDataRow getExpressionDataRow();

  public ParameterDataRow getParameterDataRow();

  public DataRow getGlobalView();

  public void setImportedDataRow( final ImportedVariablesDataRow importedDataRow );

  public MasterDataRow getParentDataRow();

  public boolean isAdvanceable();

  public MasterDataRow advance();

  public MasterDataRow advanceRecursively( final boolean deepTraversingOnly, final MasterDataRow subReportRow );

  public void fireReportEvent( ReportEvent event );

  public MasterDataRow startCrosstabMode( CrosstabSpecification crosstabSpecification );

  public MasterDataRow endCrosstabMode();

  public MasterDataRow clearExportedParameters();

  public MasterDataRow derive();

  public void requireStructuralProcessing();

  public MasterDataRow deriveSubDataRow( final ProcessingContext reportContext, final DataFactory dataFactory,
      final ParameterDataRow parameterDataRow, final ResourceBundleFactory resourceBundleFactory );

  public MasterDataRow deriveWithQueryData( final TableModel tableData );

  public MasterDataRow deriveWithReturnFromQuery();

  public MasterDataRow resetRowCursor();

  public GlobalMasterRow rebuild();

  public MasterDataRow updateDataSchema( DataSchemaDefinition dataSchemaDefinition );

  public void refresh();

  public ImportedVariablesDataRow getImportedDataRow();

  public CrosstabSpecification getCrosstabSpecification();

  public boolean isCrosstabActive();

  public MasterDataRow recordCrosstabRowState();

  public MasterDataRow clearRecordedCrosstabRowState();

  public MasterDataRow replayStoredCrosstabRowState();

  public void validateReplayFinished() throws ReportProcessingException;
}
