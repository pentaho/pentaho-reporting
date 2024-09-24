/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class DefaultReportPreProcessorTest {

  private DefaultReportPreProcessor preProcessor;

  @Before
  public void setUp() {
    preProcessor = new DefaultReportPreProcessor();
  }

  @Test
  public void testIsDesignTime() {
    DefaultFlowController flowController = mock( DefaultFlowController.class );
    ProcessingContext ctx = mock( ProcessingContext.class );
    OutputProcessorMetaData metaData = mock( OutputProcessorMetaData.class );

    doReturn( ctx ).when( flowController ).getReportContext();
    doReturn( metaData ).when( ctx ).getOutputProcessorMetaData();
    doReturn( false ).when( metaData ).isFeatureSupported( OutputProcessorFeature.DESIGNTIME );

    boolean result = preProcessor.isDesignTime( flowController );
    assertThat( result, is( equalTo( true ) ) );

    doReturn( true ).when( metaData ).isFeatureSupported( OutputProcessorFeature.DESIGNTIME );
    result = preProcessor.isDesignTime( flowController );
    assertThat( result, is( equalTo( false ) ) );
  }

  @Test
  public void testClone() {
    AbstractReportPreProcessor result = preProcessor.clone();
    assertThat( result, is( instanceOf( DefaultReportPreProcessor.class ) ) );
    assertThat( (DefaultReportPreProcessor) result, is( not( sameInstance( preProcessor ) ) ) );
  }

  @Test
  public void testPerformPreDataProcessing() throws ReportProcessingException {
    MasterReport definition = mock( MasterReport.class );
    DefaultFlowController flowController = mock( DefaultFlowController.class );
    MasterReport result = preProcessor.performPreDataProcessing( definition, flowController );
    assertThat( result, is( equalTo( definition ) ) );
  }

  @Test
  public void testPerformPreProcessing() throws ReportProcessingException {
    MasterReport definition = mock( MasterReport.class );
    DefaultFlowController flowController = mock( DefaultFlowController.class );
    MasterReport result = preProcessor.performPreProcessing( definition, flowController );
    assertThat( result, is( equalTo( definition ) ) );
  }

  @Test
  public void testPerformPreDataProcessingSubReport() throws ReportProcessingException {
    SubReport definition = mock( SubReport.class );
    DefaultFlowController flowController = mock( DefaultFlowController.class );
    SubReport result = preProcessor.performPreDataProcessing( definition, flowController );
    assertThat( result, is( equalTo( definition ) ) );
  }

  @Test
  public void testPerformPreProcessingSubReport() throws ReportProcessingException {
    SubReport definition = mock( SubReport.class );
    DefaultFlowController flowController = mock( DefaultFlowController.class );
    SubReport result = preProcessor.performPreProcessing( definition, flowController );
    assertThat( result, is( equalTo( definition ) ) );
  }
}
