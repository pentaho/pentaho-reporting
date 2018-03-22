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
 * Copyright (c) 2018 Hitachi Vantara.  All rights reserved.
 */

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
