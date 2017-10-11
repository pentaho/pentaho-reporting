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

package org.pentaho.reporting.engine.classic.core.crosstab;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.SeparateColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumn;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class Prd4523IT {
  public Prd4523IT() {
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testComplexCrosstab() throws ResourceException, ReportProcessingException {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4523.prpt" );
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.base.FailOnCellConflicts", "true" );
    HtmlReportUtil.createStreamHTML( report, new NullOutputStream() );
  }

  @Test
  public void testTableColumns() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4523.prpt" );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    assertEquals( 1, elementsByNodeType.length );
    TableRenderBox table = (TableRenderBox) elementsByNodeType[0];
    long width = table.getWidth();
    DebugLog.log( width );
    SeparateColumnModel columnModel = (SeparateColumnModel) table.getColumnModel();
    long sum = 0;
    final ArrayList<TableColumn> expected = new ArrayList<TableColumn>();
    expected.add( createTableColumn( 4748666, 4222000, 0, 0, 0 ) );
    expected.add( createTableColumn( 4694666, 4168000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 2824000 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 2824000 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 2824000, 0, 0 ) );
    expected.add( createTableColumn( 8415666, 7889000, 0, 0, 0 ) );

    TableColumn[] columns = columnModel.getColumns();
    for ( int i = 0; i < columns.length; i += 1 ) {
      TableColumn c = columnModel.getColumn( i );
      assertColumnsEqual( expected.get( i ), c );
    }
  }

  private void assertColumnsEqual( final TableColumn expected, final TableColumn result ) {
    assertEquals( expected.getEffectiveSize(), result.getEffectiveSize() );
    assertEquals( expected.getCachedSize( 1 ), result.getCachedSize( 1 ) );
    assertEquals( expected.getCachedSize( 2 ), result.getCachedSize( 2 ) );
    assertEquals( expected.getCachedSize( 3 ), result.getCachedSize( 3 ) );
    assertEquals( expected.getCachedSize( 4 ), result.getCachedSize( 4 ) );
  }

  private TableColumn createTableColumn( long effectiveSize, long size1, long size2, long size3, long size4 ) {
    TableColumn tc = new TableColumn( Border.EMPTY_BORDER, RenderLength.AUTO, false );
    tc.setEffectiveSize( effectiveSize );
    tc.setCachedSize( 1, size1 );
    tc.setCachedSize( 2, size2 );
    tc.setCachedSize( 3, size3 );
    tc.setCachedSize( 4, size4 );
    return tc;
  }
}
