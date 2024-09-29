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


import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dstepanov on 05/06/17.
 */
public class DefaultFlowControllerTest {

  private DefaultFlowController controller;
  private ProcessingContext reportContext;
  private DataSchemaDefinition schemaDefinition;
  private PerformanceMonitorContext performanceMonitorContext;
  private ReportParameterValues parameters;

  @Before
  public void setUp() throws Exception {
    reportContext = Mockito.mock(ProcessingContext.class);
    schemaDefinition = Mockito.mock(DataSchemaDefinition.class);
    performanceMonitorContext = Mockito.mock(PerformanceMonitorContext.class);
    parameters = Mockito.mock(ReportParameterValues.class);
    controller = new DefaultFlowController(reportContext, schemaDefinition, parameters, performanceMonitorContext) {
      @Override
      protected MasterDataRow createDataRow(ProcessingContext reportContext, DataSchemaDefinition schemaDefinition,
                                            ReportParameterValues parameters) {
        return null;
      }
    };
  }

  @Test
  public void performQueryDataTest() throws ReportDataFactoryException {
    PerformanceLoggingStopWatch stopWatch = Mockito.mock(PerformanceLoggingStopWatch.class);
    Mockito.doNothing().when(stopWatch).stop();
    Mockito.doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((PerformanceLoggingStopWatch) invocation.getMock()).stop();
        return null;
      }
    }).when(stopWatch).close();
    Mockito.doNothing().when(stopWatch).start();
    Mockito.when(performanceMonitorContext.createStopWatch(Mockito.eq(PerformanceTags.REPORT_QUERY), Mockito.any())).thenReturn(stopWatch);
    DataFactory dataFactory = Mockito.mock(DataFactory.class);
    String query = "query";
    Mockito.when(dataFactory.queryData(Mockito.eq(query), Mockito.any())).thenReturn(new EmptyTableModel());

    int queryLimit = 10;
    int queryTimeout = 10000;
    boolean designTime = true;
    List<SortConstraint> sortConstraints = new ArrayList<>();
    try {
      controller.performQueryData(dataFactory, query, queryLimit, queryTimeout, parameters, designTime, sortConstraints);
    } catch (ReportDataFactoryException e) {
      e.printStackTrace();
    }
    InOrder order = Mockito.inOrder(stopWatch);
    order.verify(stopWatch).start();
    order.verify(stopWatch).stop();
  }
}