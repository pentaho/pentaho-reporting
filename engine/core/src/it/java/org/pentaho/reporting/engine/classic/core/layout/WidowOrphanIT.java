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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.util.List;

@SuppressWarnings( { "HardCodedStringLiteral", "AutoBoxing" } )
public class WidowOrphanIT extends TestCase {
  public WidowOrphanIT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardLayout() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    final RenderNode grOut = MatchFactory.findElementByName( logicalPageBox, "group-outside" );
    assertTrue( grOut instanceof RenderBox );
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals( StrictGeomUtility.toInternalValue( 60 ), grOutBox.getOrphanConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 60 ), grOutBox.getWidowConstraintSize() );

    final RenderNode grIn = MatchFactory.findElementByName( logicalPageBox, "group-inside" );
    assertTrue( grIn instanceof RenderBox );
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), grInBox.getOrphanConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), grInBox.getWidowConstraintSize() );

    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  public void testStandardLayoutPageBreak() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final Band band = new Band();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    final ReportHeader header = report.getReportHeader();
    header.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true );
    header.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    header.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    header.addElement( createBand( "placeholder", 60 ) );
    header.addElement( band );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    final LogicalPageBox logicalPageBox = pages.get( 0 );
    // if keep-together works, then we avoid the pagebreak between the inner-group-header and the first itemband.
    // therefore the first page only contains the placeholder element.
    final RenderNode grHOut = MatchFactory.findElementByName( logicalPageBox, "group-header-outside" );
    assertNull( grHOut );
    final RenderNode grOut = MatchFactory.findElementByName( logicalPageBox, "group-outside" );
    assertNull( grOut );

    final LogicalPageBox logicalPageBox2 = pages.get( 1 );
    final RenderNode grHOut2 = MatchFactory.findElementByName( logicalPageBox2, "group-header-outside" );
    assertNotNull( grHOut2 );
    final RenderNode grOut2 = MatchFactory.findElementByName( logicalPageBox2, "group-outside" );
    assertNotNull( grOut2 );
    final RenderNode ib3_miss = MatchFactory.findElementByName( logicalPageBox2, "ib3" );
    assertNull( ib3_miss );

    final LogicalPageBox logicalPageBox3 = pages.get( 2 );
    final RenderNode ib3 = MatchFactory.findElementByName( logicalPageBox3, "ib3" );
    assertNotNull( ib3 );
    // ModelPrinter.INSTANCE.print(logicalPageBox3);
  }

  /**
   * Widows override keep-together, and widow declarations and the widow-blockout-area do not take parent keep-together
   * areas into account. A orphan combines with keep-together, but if conflicting, the orphan rule will win.
   *
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testKeepTogetherEffect() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );
    detailBody.addElement( createBand( "ib4" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    final RenderNode grOut = MatchFactory.findElementByName( logicalPageBox, "group-outside" );
    assertTrue( grOut instanceof RenderBox );
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals( StrictGeomUtility.toInternalValue( 120 ), grOutBox.getOrphanConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 60 ), grOutBox.getWidowConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 60 ), grOutBox.getWidowConstraintSizeWithKeepTogether() );

    final RenderNode grIn = MatchFactory.findElementByName( logicalPageBox, "group-inside" );
    assertTrue( grIn instanceof RenderBox );
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals( StrictGeomUtility.toInternalValue( 100 ), grInBox.getOrphanConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), grInBox.getWidowConstraintSize() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), grInBox.getWidowConstraintSizeWithKeepTogether() );
  }

  /**
   * Tests the combined effect of widow, orphan and keep-together rules. Orphan rules trump keep-together declarations
   * for elements included in the orphan calculation. A keep-together child will expand the orphan area and thus
   * triggers an early orphan-pagebreak. After that break the band stays with the orphans.
   * <p/>
   * Keep-together wins over widow rules to the effect that the keep-together area expands the widow area.
   *
   * @throws Exception
   */
  public void testKeepTogetherEffectPagebreak() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1 );
    final LogicalPageBox logicalPageBox1 = pages.get( 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox1 );
    final RenderNode grHOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-header-outside" );
    assertNotNull( grHOut2 );
    final RenderNode grOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-outside" );
    assertNotNull( grOut2 );
    final RenderNode ib1 = MatchFactory.findElementByName( logicalPageBox1, "ib1" );
    assertNotNull( ib1 );
    final RenderNode ib2 = MatchFactory.findElementByName( logicalPageBox1, "ib2" );
    assertNotNull( ib2 );
    final RenderNode ib3miss = MatchFactory.findElementByName( logicalPageBox1, "ib3" );
    assertNull( ib3miss );

    final LogicalPageBox logicalPageBox2 = pages.get( 1 );
    ModelPrinter.INSTANCE.print( logicalPageBox2 );
    final RenderNode ib3 = MatchFactory.findElementByName( logicalPageBox2, "ib3" );
    assertNotNull( ib3 );
  }

  /**
   * Same as above with an additional padding band added. This ensures that itembands for testing do not naturally fall
   * on a pagebreak.
   *
   * @throws Exception
   */
  public void testKeepTogetherEffectPagebreak2() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.addElement( createRootBand( "ib1" ) );
    detailBody.addElement( createRootBand( "ib2" ) );
    detailBody.addElement( createRootBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createRootBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createRootBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final Band band = new Band();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createRootBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createRootBand( "group-footer-outside" ) );

    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );
    report.getReportHeader().addElement( createBand( "Dummy", 15 ) );
    report.getReportHeader().addElement( band );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1 );
    final LogicalPageBox logicalPageBox1 = pages.get( 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox1 );
    final RenderNode grHOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-header-outside" );
    assertNotNull( grHOut2 );
    final RenderNode grOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-outside" );
    assertNotNull( grOut2 );
    final RenderNode ib1 = MatchFactory.findElementByName( logicalPageBox1, "ib1" );
    assertNotNull( ib1 );
    final RenderNode ib2 = MatchFactory.findElementByName( logicalPageBox1, "ib2" );
    assertNotNull( ib2 );
    final RenderNode ib3miss = MatchFactory.findElementByName( logicalPageBox1, "ib3" );
    assertNull( ib3miss );

    final LogicalPageBox logicalPageBox2 = pages.get( 1 );
    ModelPrinter.INSTANCE.print( logicalPageBox2 );
    final RenderNode ib3 = MatchFactory.findElementByName( logicalPageBox2, "ib3" );
    assertNotNull( ib3 );
  }

  /**
   * Tests the combined effect of widow, orphan and keep-together rules. Orphan rules trump keep-together declarations
   * for elements included in the orphan calculation. A keep-together child will expand the orphan area and thus
   * triggers an early orphan-pagebreak. After that break the band stays with the orphans.
   * <p/>
   * Keep-together wins over widow rules to the effect that the keep-together area expands the widow area.
   *
   * @throws Exception
   */
  public void testKeepTogetherEffectPagebreak3() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );
    detailBody.addElement( createBand( "ib4" ) );
    detailBody.addElement( createBand( "ib5" ) );
    detailBody.addElement( createBand( "ib6" ) );
    detailBody.addElement( createBand( "ib7" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    final LogicalPageBox logicalPageBox1 = pages.get( 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox1 );
    final RenderNode grHOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-header-outside" );
    assertNotNull( grHOut2 );
    final RenderNode grOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-outside" );
    assertNotNull( grOut2 );
    final RenderNode ib1 = MatchFactory.findElementByName( logicalPageBox1, "ib1" );
    assertNotNull( ib1 );
    final RenderNode ib2 = MatchFactory.findElementByName( logicalPageBox1, "ib2" );
    assertNotNull( ib2 );
    final RenderNode ib3miss = MatchFactory.findElementByName( logicalPageBox1, "ib3" );
    assertNotNull( ib3miss );

    final LogicalPageBox logicalPageBox2 = pages.get( 1 );
    ModelPrinter.INSTANCE.print( logicalPageBox2 );
    final RenderNode ib3 = MatchFactory.findElementByName( logicalPageBox2, "ib3" );
    assertNull( ib3 );
    final RenderNode ib6 = MatchFactory.findElementByName( logicalPageBox2, "ib6" );
    assertNotNull( ib6 );
    final RenderNode ib7miss = MatchFactory.findElementByName( logicalPageBox1, "ib7" );
    assertNull( ib7miss );

    final LogicalPageBox logicalPageBox3 = pages.get( 2 );
    ModelPrinter.INSTANCE.print( logicalPageBox3 );
    final RenderNode ib7 = MatchFactory.findElementByName( logicalPageBox3, "ib7" );
    assertNotNull( ib7 );
  }

  public void testKeepTogetherEffectPagebreakLarger() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body" );
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );
    detailBody.addElement( createBand( "ib4" ) );
    detailBody.addElement( createBand( "ib5" ) );
    detailBody.addElement( createBand( "ib6" ) );
    detailBody.addElement( createBand( "ib7" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    final LogicalPageBox logicalPageBox1 = pages.get( 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox1 );
    final RenderNode grHOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-header-outside" );
    assertNotNull( grHOut2 );
    final RenderNode grOut2 = MatchFactory.findElementByName( logicalPageBox1, "group-outside" );
    assertNotNull( grOut2 );
    final RenderNode ib1 = MatchFactory.findElementByName( logicalPageBox1, "ib1" );
    assertNotNull( ib1 );
    final RenderNode ib2 = MatchFactory.findElementByName( logicalPageBox1, "ib2" );
    assertNotNull( ib2 );
    final RenderNode ib3miss = MatchFactory.findElementByName( logicalPageBox1, "ib3" );
    assertNotNull( ib3miss );

    final LogicalPageBox logicalPageBox2 = pages.get( 1 );
    ModelPrinter.INSTANCE.print( logicalPageBox2 );
    final RenderNode ib3 = MatchFactory.findElementByName( logicalPageBox2, "ib3" );
    assertNull( ib3 );
    final RenderNode ib6 = MatchFactory.findElementByName( logicalPageBox2, "ib6" );
    assertNotNull( ib6 );
    final RenderNode ib7miss = MatchFactory.findElementByName( logicalPageBox2, "ib7" );
    assertNull( ib7miss );

    final LogicalPageBox logicalPageBox3 = pages.get( 2 );
    final RenderNode ib7 = MatchFactory.findElementByName( logicalPageBox3, "ib7" );
    assertNotNull( ib7 );
  }

  private Band createRootBand( final String name ) {
    final Band band = createBand( name, 20 );
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    return band;
  }

  private Band createBand( final String name ) {
    return createBand( name, 20 );
  }

  private Band createBand( final String name, final float height ) {
    final Band ghO1 = new Band();
    ghO1.setName( name );
    ghO1.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, false );
    ghO1.addElement( TableTestUtil.createDataItem( name, 100, height ) );
    return ghO1;
  }
}
