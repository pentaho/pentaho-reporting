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


package org.pentaho.reporting.engine.classic.core.testsupport.base;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.function.ProcessingDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.states.NoOpPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.StateUtilities;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;

import java.util.Collections;

public abstract class PreProcessorTestBase {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testMetaData() throws Exception {
    ReportPreProcessor prc = create();
    PreProcessorTestHelper.validateElementMetaData( prc.getClass() );
  }

  protected abstract ReportPreProcessor create();

  /**
   * Helper method to invoke a pre-processor outside of the report processing. This is strictly for unit-testing only.
   *
   * @param report
   * @param processor
   * @return
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   */
  protected MasterReport materialize( final MasterReport report, final ReportPreProcessor processor )
    throws ReportProcessingException {
    final PerformanceMonitorContext pmc = new NoOpPerformanceMonitorContext();
    final DefaultProcessingContext processingContext = new DefaultProcessingContext( report );
    final DataSchemaDefinition definition = report.getDataSchemaDefinition();
    final DefaultFlowController flowController =
        new DefaultFlowController( processingContext, definition, StateUtilities.computeParameterValueSet( report ),
            pmc );
    final CachingDataFactory dataFactory = new CachingDataFactory( report.getDataFactory(), false );
    dataFactory.initialize( new ProcessingDataFactoryContext( processingContext, dataFactory ) );

    try {
      final DefaultFlowController postQueryFlowController =
          flowController.performQuery( dataFactory, report.getQuery(), report.getQueryLimit(),
              report.getQueryTimeout(), flowController.getMasterRow().getResourceBundleFactory(), Collections.<SortConstraint>emptyList() );

      return processor.performPreProcessing( report, postQueryFlowController );
    } finally {
      dataFactory.close();
    }
  }

  protected MasterReport materializePreData( MasterReport report, ReportPreProcessor reportPreProcessor )
    throws ReportProcessingException {
    final PerformanceMonitorContext pmc = new NoOpPerformanceMonitorContext();
    final DefaultProcessingContext processingContext = new DefaultProcessingContext( report );
    final DataSchemaDefinition definition = report.getDataSchemaDefinition();
    final DefaultFlowController flowController =
        new DefaultFlowController( processingContext, definition, StateUtilities.computeParameterValueSet( report ),
            pmc );
    return reportPreProcessor.performPreDataProcessing( report, flowController );
  }
}
