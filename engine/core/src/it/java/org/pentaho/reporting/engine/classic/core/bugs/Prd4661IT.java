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
 * Copyright (c) 2005-2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4661IT extends TestCase {
  public Prd4661IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayoutAssumptions() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4661-minimal.prpt" );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final LogicalPageBox box = DebugReportRunner.layoutSingleBand( report, report.getItemBand(), false, false );
    Assert.assertEquals( 1100000, box.getLastChild().getY2() );
    Assert.assertEquals( 1100000, box.getHeight() );

    final LogicalPageBox box2 = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode[] itembands = MatchFactory.findElementsByElementType( box2, ItemBandType.INSTANCE );
    Assert.assertEquals( 1, itembands.length );
    Assert.assertEquals( 1100000, itembands[0].getY2() );
    Assert.assertEquals( 1100000, itembands[0].getHeight() );
  }

  public void testExcelCrash() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4661-minimal.prpt" );
    DebugReportRunner.createXLS( report );
  }

  public void testBorderExpandsTotalSize_Row() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4661-minimal.prpt" );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.getItemBand().getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, 40f );
    report.getItemBand().setLayout( BandStyleKeys.LAYOUT_ROW );
    report.getItemBand().setName( "itemband" );
    report.getItemBand().getElement( 0 ).setName( "element-0" );

    final LogicalPageBox box = DebugReportRunner.layoutSingleBand( report, report.getItemBand(), false, false );
    Assert.assertEquals( 5000000, box.getLastChild().getY2() );
    Assert.assertEquals( 5000000, box.getHeight() );
  }

  public void testBorderExpandsTotalSize_Block() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4661-minimal.prpt" );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.getItemBand().getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, 40f );
    report.getItemBand().setLayout( BandStyleKeys.LAYOUT_BLOCK );
    report.getItemBand().setName( "itemband" );
    report.getItemBand().getElement( 0 ).setName( "element-0" );

    final LogicalPageBox box = DebugReportRunner.layoutSingleBand( report, report.getItemBand(), false, false );
    Assert.assertEquals( 5000000, box.getLastChild().getY2() );
    Assert.assertEquals( 5000000, box.getHeight() );
  }

  public void testBorderExpandsTotalSize_Canvas() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4661-minimal.prpt" );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.getItemBand().getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, 40f );
    report.getItemBand().getElement( 0 ).getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    report.getItemBand().getElement( 0 ).getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );
    report.getItemBand().setLayout( BandStyleKeys.LAYOUT_CANVAS );
    report.getItemBand().setName( "itemband" );
    report.getItemBand().getElement( 0 ).setName( "element-0" );

    final LogicalPageBox box = DebugReportRunner.layoutSingleBand( report, report.getItemBand(), false, false );
    Assert.assertEquals( 5000000, box.getLastChild().getY2() );
    Assert.assertEquals( 5000000, box.getHeight() );
  }

}
