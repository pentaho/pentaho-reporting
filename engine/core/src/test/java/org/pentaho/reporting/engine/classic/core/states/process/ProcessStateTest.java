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


package org.pentaho.reporting.engine.classic.core.states.process;

import junit.framework.TestCase;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ProcessStateTest extends TestCase {

  @Override
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testInitializeForSubreport() throws Exception {
    MasterReport masterReport = new MasterReport();
    ProcessingContext processingContext = new DefaultProcessingContext();
    OutputFunction outputFunction = new DefaultOutputFunction();
    SubReport subReportSpy = spy( new SubReport() );
    InstanceID instanceID = new InstanceID();
    InlineSubreportMarker[] inlineSubreportMarkers = new InlineSubreportMarker[1];
    inlineSubreportMarkers[0] = new InlineSubreportMarker( subReportSpy, instanceID, SubReportProcessType.INLINE );
    ProcessState parentProcessStateSpy = spy( new ProcessState() );
    ProcessState processState = new ProcessState();

    masterReport.getRelationalGroup( 0 ).getHeader().addSubReport( subReportSpy );
    masterReport.setQueryLimit( 3 );
    subReportSpy.reconnectParent( masterReport );

    when( subReportSpy.isQueryLimitInherited() ).thenReturn( false ).thenReturn( true );
    when( parentProcessStateSpy.getReport() ).thenReturn( masterReport );

    parentProcessStateSpy.initializeForMasterReport( masterReport, processingContext, outputFunction );

    processState.initializeForSubreport( inlineSubreportMarkers, 0, parentProcessStateSpy );
    assertEquals( Integer.valueOf( -1 ), processState.getQueryLimit() );

    processState.initializeForSubreport( inlineSubreportMarkers, 0, parentProcessStateSpy );
    assertEquals( Integer.valueOf( 3 ), processState.getQueryLimit() );
  }
}
