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


package org.pentaho.reporting.engine.classic.extensions.legacy.charts;

import org.pentaho.plugin.jfreereport.reportcharts.ChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.MultiPlotChartExpression;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;

public class LegacyChartPreProcessor extends AbstractReportPreProcessor {
  public LegacyChartPreProcessor() {
  }

  public MasterReport performPreProcessing( final MasterReport definition,
                                            final DefaultFlowController flowController )
    throws ReportProcessingException {
    processSection( definition, flowController.getDataSchema(), definition );
    return definition;
  }

  public SubReport performPreProcessing( final SubReport definition,
                                         final DefaultFlowController flowController ) throws ReportProcessingException {
    processSection( definition, flowController.getDataSchema(), definition );
    return definition;
  }


  private void processSection( final Section section,
                               final DataSchema dataSchema,
                               final AbstractReportDefinition reportDefinition ) throws ReportProcessingException {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof SubReport ) {
        continue;
      }

      if ( element instanceof Section ) {
        processSection( (Section) element, dataSchema, reportDefinition );
        continue;
      }

      final Object attribute =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
      if ( attribute instanceof LegacyChartType == false ) {
        continue;
      }

      final Object maybeChartExpression =
        element.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
      if ( maybeChartExpression instanceof ChartExpression == false ) {
        continue;
      }

      final ChartExpression chartExpression = (ChartExpression) maybeChartExpression;
      final Object primaryChartExpression = element.getAttribute
        ( LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE );
      if ( primaryChartExpression instanceof Expression ) {
        final Expression datasetExpression = (Expression) primaryChartExpression;
        final Expression datasetExpressionInstance = datasetExpression.getInstance();
        final String name = AutoGeneratorUtility.generateUniqueExpressionName
          ( dataSchema, "::legacy-charts::primary-dataset::{0}", reportDefinition );
        datasetExpressionInstance.setName( name );
        chartExpression.setDataSource( name );
        reportDefinition.addExpression( datasetExpressionInstance );
      }

      if ( chartExpression instanceof MultiPlotChartExpression == false ) {
        continue;
      }
      final MultiPlotChartExpression multiPlotChartExpression = (MultiPlotChartExpression) chartExpression;

      final Object secondaryDataSourceExpression = element.getAttribute
        ( LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.SECONDARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE );
      if ( secondaryDataSourceExpression instanceof Expression ) {
        final Expression datasetExpression = (Expression) secondaryDataSourceExpression;
        final Expression datasetExpressionInstance = datasetExpression.getInstance();
        final String name = AutoGeneratorUtility.generateUniqueExpressionName
          ( dataSchema, "::legacy-charts::secondary-dataset::{0}", reportDefinition );
        datasetExpressionInstance.setName( name );
        multiPlotChartExpression.setSecondaryDataSet( name );
        reportDefinition.addExpression( datasetExpressionInstance );
      }
    }
  }

}
