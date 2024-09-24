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

package org.pentaho.reporting.engine.classic.core.function;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class TotalItemCountIT extends TestCase {
  private static final int[] GROUPCOUNTS = new int[] { 2, 3, 1, 14, 2, 1 };

  private static class TotalItemCountVerifyFunction extends AbstractFunction {
    private int index;

    /**
     * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
     * is added to the report's function collection.
     */
    public TotalItemCountVerifyFunction() {
      setName( "verification" );
    }

    /**
     * Receives notification that report generation initializes the current run.
     * <p/>
     * The event carries a ReportState.Started state. Use this to initialize the report.
     *
     * @param event
     *          The event.
     */
    public void reportInitialized( ReportEvent event ) {
      index = 0;
    }

    /**
     * Receives notification that a group has finished.
     *
     * @param event
     *          the event.
     */
    public void groupFinished( ReportEvent event ) {
      if ( event.getLevel() >= 0 ) {
        return;
      }
      assertCount( event );
      index += 1;
    }

    /**
     * Receives notification that a group has started.
     *
     * @param event
     *          the event.
     */
    public void groupStarted( ReportEvent event ) {
      if ( event.getLevel() >= 0 ) {
        return;
      }
      assertCount( event );
    }

    private void assertCount( ReportEvent event ) {
      // The itemcount function is only valid within the defined group.
      if ( FunctionUtilities.getCurrentGroup( event ).getName().equals( "Continent Group" ) ) {
        // the number of continents in the report1
        Number n = (Number) event.getDataRow().get( "continent-total-gc" );
        assertEquals( "continent-total-gc: " + index, GROUPCOUNTS[index], n.intValue() );
      }

      // the number of continents in the report1 + default group start
      Number n2 = (Number) event.getDataRow().get( "total-gc" );
      assertEquals( "total-gc", 23, n2.intValue() );
    }

    public Object getValue() {
      return null;
    }
  }

  public TotalItemCountIT() {
  }

  public TotalItemCountIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGroupItemCount() throws Exception {
    final URL url = getClass().getResource( "aggregate-function-test.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.addExpression( new EventMonitorFunction() );
    report.setDataFactory( new TableDataFactory( "default", new AggregateTestDataTableModel() ) );

    report.addExpression( new TotalItemCountVerifyFunction() );
    final RelationalGroup g = report.getGroupByName( "default" );
    if ( g != null ) {
      report.removeGroup( g );
    }

    final TotalItemCountFunction f = new TotalItemCountFunction();
    f.setName( "continent-total-gc" );
    f.setGroup( "Continent Group" );
    f.setDependencyLevel( 1 );
    report.addExpression( f );

    final TotalItemCountFunction f2 = new TotalItemCountFunction();
    f2.setName( "total-gc" );
    f2.setDependencyLevel( 1 );
    report.addExpression( f2 );

    DebugReportRunner.execGraphics2D( report );
  }

  public void testSimplifiedRun() throws Exception {
    final MasterReport report = new MasterReport();
    report.addExpression( new EventMonitorFunction() );
    report.addExpression( new TotalItemCountVerifyFunction() );
    report.setDataFactory( new TableDataFactory( "default", new AggregateTestDataTableModel() ) );
    report.setQuery( "default" );
    final RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
    rootGroup.clearFields();
    rootGroup.addField( "Continent" );
    rootGroup.setName( "Continent Group" );

    final TotalItemCountFunction f = new TotalItemCountFunction();
    f.setName( "continent-total-gc" );
    f.setGroup( "Continent Group" );
    f.setDependencyLevel( 1 );
    report.addExpression( f );

    final TotalItemCountFunction f2 = new TotalItemCountFunction();
    f2.setName( "total-gc" );
    f2.setDependencyLevel( 1 );
    report.addExpression( f2 );

    DebugReportRunner.execGraphics2D( report );
  }

}
