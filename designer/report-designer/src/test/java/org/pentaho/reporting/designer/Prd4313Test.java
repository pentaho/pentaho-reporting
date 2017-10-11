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

package org.pentaho.reporting.designer;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.SharedElementRenderer;
import org.pentaho.reporting.designer.testsupport.TableTestUtil;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.StopWatch;

import java.io.PrintStream;

import javax.naming.spi.NamingManager;

public class Prd4313Test extends TestCase {
  public void setUp() throws Exception {
    final PrintStream out = System.out;
    final PrintStream err = System.err;
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
    System.setOut( out );
    System.setErr( err );
  }

  public void testEventNotification() {
    final MasterReport report = new MasterReport();
    final Element mrLabel = TableTestUtil.createDataItem( "Label" );
    report.getPageHeader().addElement( mrLabel );

    final Element mrLabel2 = TableTestUtil.createDataItem( "Label2" );
    report.getPageHeader().addElement( mrLabel2 );

    final TestReportDesignerContext designerContext = new TestReportDesignerContext();
    final int idx = designerContext.addMasterReport( report );
    final ReportRenderContext masterContext = designerContext.getReportRenderContext( idx );

    final SharedElementRenderer sharedRenderer = masterContext.getSharedRenderer();
    assertTrue( sharedRenderer.performLayouting() );

    // we should have conflicts ..
    assertFalse( sharedRenderer.getConflicts().isEmpty() );
  }

  public void testPerformance() {
    runPerformanceTestInternal();
  }

  private void runPerformanceTestInternal() {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty
      ( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.ReportCellConflicts", "false" );
    final Element mrLabel = TableTestUtil.createDataItem( "Label" );
    report.getPageHeader().addElement( mrLabel );

    final Element mrLabel2 = TableTestUtil.createDataItem( "Label2" );
    report.getPageHeader().addElement( mrLabel2 );

    final TestReportDesignerContext designerContext = new TestReportDesignerContext();
    final int idx = designerContext.addMasterReport( report );
    final ReportRenderContext masterContext = designerContext.getReportRenderContext( idx );

    final SharedElementRenderer sharedRenderer = masterContext.getSharedRenderer();
    final StopWatch w = StopWatch.startNew();
    run( mrLabel2, sharedRenderer );
    DebugLog.log( w );
  }

  private void run( final Element mrLabel2, final SharedElementRenderer sharedRenderer ) {
    for ( int i = 0; i < 1; i += 1 ) {
      mrLabel2.getStyle().setStyleProperty( ElementStyleKeys.ANCHOR_NAME, String.valueOf( Math.random() ) );
      //      mrLabel2.getStyle().setStyleProperty(ElementStyleKeys.POS_Y, new Float(100f * Math.random()));
      //   mrLabel2.getStyle().setStyleProperty(ElementStyleKeys.POS_X, new Float(100f * Math.random()));
      assertTrue( sharedRenderer.performLayouting() );
    }
  }
}
