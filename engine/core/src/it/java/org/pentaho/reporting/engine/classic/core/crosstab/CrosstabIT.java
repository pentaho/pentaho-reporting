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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;

public class CrosstabIT extends TestCase {
  public CrosstabIT() {
  }

  public CrosstabIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmptyCrosstab() throws Exception {
    final MasterReport report = new MasterReport();
    report.setRootGroup( new CrosstabGroup() );
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", new DefaultTableModel( 1, 1 ) ) );
    DebugReportRunner.executeAll( report );
  }

  public void testBreaking1() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3857-002.prpt" );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }
}
