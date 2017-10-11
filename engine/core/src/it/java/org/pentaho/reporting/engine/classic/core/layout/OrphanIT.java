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
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.repository.ContentIOException;

import java.io.IOException;

@SuppressWarnings( "HardCodedStringLiteral" )
public class OrphanIT extends TestCase {
  public OrphanIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardLayout() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body-1" );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band detailBody2 = new Band();
    detailBody2.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody2.setName( "detail-body-2" );
    detailBody2.addElement( createBand( "ib1" ) );
    detailBody2.addElement( createBand( "ib2" ) );
    detailBody2.addElement( createBand( "ib3" ) );

    final Band insideGroup2 = new Band();
    insideGroup2.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup2.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup2.setName( "group-inside" );
    insideGroup2.addElement( createBand( "group-header-inside" ) );
    insideGroup2.addElement( detailBody2 );
    insideGroup2.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );
    outsideBody.addElement( insideGroup2 );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    final RenderNode grOut = MatchFactory.findElementByName( logicalPageBox, "group-outside" );
    assertTrue( grOut instanceof RenderBox );
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals( StrictGeomUtility.toInternalValue( 60 ), grOutBox.getOrphanConstraintSize() );

    final RenderNode grIn = MatchFactory.findElementByName( logicalPageBox, "group-inside" );
    assertTrue( grIn instanceof RenderBox );
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), grInBox.getOrphanConstraintSize() );

    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  public void testStandardLayoutKT() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    final Band detailBody = new Band();
    detailBody.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody.setName( "detail-body-1" );
    detailBody.addElement( createBand( "ib1" ) );
    detailBody.addElement( createBand( "ib2" ) );
    detailBody.addElement( createBand( "ib3" ) );

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup.setName( "group-inside" );
    insideGroup.addElement( createBand( "group-header-inside" ) );
    insideGroup.addElement( detailBody );
    insideGroup.addElement( createBand( "group-footer-inside" ) );

    final Band detailBody2 = new Band();
    detailBody2.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true );
    detailBody2.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    detailBody2.setName( "detail-body-2" );
    detailBody2.addElement( createBand( "ib1" ) );
    detailBody2.addElement( createBand( "ib2" ) );
    detailBody2.addElement( createBand( "ib3" ) );

    final Band insideGroup2 = new Band();
    insideGroup2.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    insideGroup2.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    insideGroup2.setName( "group-inside" );
    insideGroup2.addElement( createBand( "group-header-inside" ) );
    insideGroup2.addElement( detailBody2 );
    insideGroup2.addElement( createBand( "group-footer-inside" ) );

    final Band outsideBody = new Band();
    outsideBody.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    outsideBody.setName( "group-body-outside" );
    outsideBody.addElement( insideGroup );
    outsideBody.addElement( insideGroup2 );

    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false );
    band.setLayout( BandStyleKeys.LAYOUT_BLOCK );
    band.setName( "group-outside" );
    band.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    band.addElement( createBand( "group-header-outside" ) );
    band.addElement( outsideBody );
    band.addElement( createBand( "group-footer-outside" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    final RenderNode grOut = MatchFactory.findElementByName( logicalPageBox, "group-outside" );
    assertTrue( grOut instanceof RenderBox );
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals( StrictGeomUtility.toInternalValue( 100 ), grOutBox.getOrphanConstraintSize() );

    final RenderNode grIn = MatchFactory.findElementByName( logicalPageBox, "group-inside" );
    assertTrue( grIn instanceof RenderBox );
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals( StrictGeomUtility.toInternalValue( 80 ), grInBox.getOrphanConstraintSize() );

    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  public void testReport() throws ReportProcessingException, IOException, ContentIOException, BundleWriterException {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "2", "row-1" );
    model.addRow( "b", "1", "row-2" );
    model.addRow( "b", "2", "row-3" );
    model.addRow( "b", "2", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "3", "row-6" );
    model.addRow( "a", "1", "row-7" );
    model.addRow( "b", "1", "row-8" );
    model.addRow( "b", "2", "row-9" );

    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createDataItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    // BundleWriter.writeReportToZipFile(report, "/tmp/Prd-2087-Orphan-0.prpt");
    DebugReportRunner.createPDF( report );
    // PdfReportUtil.createPDF(report, "/tmp/OrphanTest.pdf");
  }

  public void testSubReport() throws ReportProcessingException, IOException, ContentIOException, BundleWriterException {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "2", "row-1" );
    model.addRow( "b", "1", "row-2" );
    model.addRow( "b", "2", "row-3" );
    model.addRow( "b", "2", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "3", "row-6" );
    model.addRow( "a", "1", "row-7" );
    model.addRow( "b", "1", "row-8" );
    model.addRow( "b", "2", "row-9" );

    final SubReport report = new SubReport();
    report.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 200f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 100f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 20f );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createDataItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final MasterReport master = new MasterReport();
    master.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    master.getReportHeader().addElement( report );

    // BundleWriter.writeReportToZipFile(master, "/tmp/Prd-2087-Orphan-3.prpt");
    // PdfReportUtil.createPDF(master, "/tmp/OrphanTest3.pdf");
    DebugReportRunner.createPDF( master );
  }

  public void testInvalidReport() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "1", "row-1" );
    model.addRow( "a", "1", "row-2" );
    model.addRow( "a", "2", "row-3" );
    model.addRow( "b", "1", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "2", "row-6" );
    model.addRow( "b", "2", "row-7" );
    model.addRow( "b", "3", "row-8" );
    model.addRow( "b", "3", "row-9" );
    model.addRow( "b", "3", "row-10" );
    model.addRow( "b", "3", "row-11" );
    model.addRow( "a", "1", "row-12" );
    model.addRow( "b", "1", "row-13" );
    model.addRow( "b", "2", "row-14" );

    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 200 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    // BundleWriter.writeReportToZipFile(report, "/tmp/Prd-2087-Orphan-1.prpt");
    DebugReportRunner.createPDF( report );
    // PdfReportUtil.createPDF(report, "/tmp/OrphanTest1.pdf");

    // ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 1));
    // ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 2));
  }

  public void testInvalidSubReport() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "1", "row-1" );
    model.addRow( "a", "1", "row-2" );
    model.addRow( "a", "2", "row-3" );
    model.addRow( "b", "1", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "2", "row-6" );
    model.addRow( "b", "2", "row-7" );
    model.addRow( "b", "3", "row-8" );
    model.addRow( "b", "3", "row-9" );
    model.addRow( "b", "3", "row-10" );
    model.addRow( "b", "3", "row-11" );
    model.addRow( "a", "1", "row-12" );
    model.addRow( "b", "1", "row-13" );
    model.addRow( "b", "2", "row-14" );

    final SubReport report = new SubReport();
    report.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 200f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 100f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 20f );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 200 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final MasterReport master = new MasterReport();
    master.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    master.getReportHeader().addElement( report );

    // BundleWriter.writeReportToZipFile(master, "/tmp/Prd-2087-Orphan-4.prpt");
    // PdfReportUtil.createPDF(master, "/tmp/OrphanTest4.pdf");
    DebugReportRunner.createPDF( master );
  }

  public void testInvalidReport2() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "1", "row-1" );
    model.addRow( "a", "1", "row-2" );
    model.addRow( "a", "2", "row-3" );
    model.addRow( "b", "1", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "2", "row-6" );
    model.addRow( "b", "2", "row-7" );
    model.addRow( "b", "3", "row-8" );
    model.addRow( "b", "3", "row-9" );
    model.addRow( "b", "3", "row-10" );
    model.addRow( "b", "3", "row-11" );
    model.addRow( "b", "3", "row-12" );
    model.addRow( "a", "1", "row-13" );
    model.addRow( "b", "1", "row-14" );
    model.addRow( "b", "2", "row-15" );

    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 200 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    // PdfReportUtil.createPDF(report, "/tmp/OrphanTest2.pdf");
    // BundleWriter.writeReportToZipFile(report, "/tmp/Prd-2087-Orphan-2.prpt");
    DebugReportRunner.createPDF( report );

    // ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 4));
    // ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 5));
  }

  public void testInvalidSubReport2() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "1", "row-1" );
    model.addRow( "a", "1", "row-2" );
    model.addRow( "a", "2", "row-3" );
    model.addRow( "b", "1", "row-4" );
    model.addRow( "b", "2", "row-5" );
    model.addRow( "b", "2", "row-6" );
    model.addRow( "b", "2", "row-7" );
    model.addRow( "b", "3", "row-8" );
    model.addRow( "b", "3", "row-9" );
    model.addRow( "b", "3", "row-10" );
    model.addRow( "b", "3", "row-11" );
    model.addRow( "b", "3", "row-12" );
    model.addRow( "a", "1", "row-13" );
    model.addRow( "b", "1", "row-14" );
    model.addRow( "b", "2", "row-15" );

    final SubReport report = new SubReport();
    report.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 200f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 100f );
    report.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 20f );
    report.addGroup( new RelationalGroup() );
    report.setDataFactory( new TableDataFactory( "query", model ) );
    report.setQuery( "query" );

    final RelationalGroup group0 = (RelationalGroup) report.getGroup( 0 );
    group0.setName( "outer-group" );
    group0.addField( "g0" );
    group0.getHeader().addElement( createDataItem( "outer-header-field", 100, 20 ) );
    group0.getFooter().addElement( createDataItem( "outer-footer-field", 100, 20 ) );
    group0.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 200 );
    group1.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, 2 );

    final MasterReport master = new MasterReport();
    master.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    master.getReportHeader().addElement( report );

    // BundleWriter.writeReportToZipFile(master, "/tmp/Prd-2087-Orphan-5.prpt");
    // PdfReportUtil.createPDF(master, "/tmp/OrphanTest5.pdf");
    DebugReportRunner.createPDF( master );
  }

  public static Element createDataItem( final String text, final float width, final float height ) {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setName( text );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    return label;
  }

  public static Element createFieldItem( final String text, final float width, final float height ) {
    final FormulaExpression fe = new FormulaExpression();
    fe.setFormula( "=[value]" );

    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    label.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, fe );
    label.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, fe );
    return label;
  }

  private static void assertElementDoesNotExist( final String element, final RenderBox box ) {
    final RenderNode ib1 = MatchFactory.findElementByName( box, element );
    assertNull( "Element '" + element + "' does NOT exist.", ib1 );
  }

  private static void assertElementExists( final String element, final RenderBox box ) {
    final RenderNode ib1 = MatchFactory.findElementByName( box, element );
    assertNotNull( "Element '" + element + "' exists.", ib1 );
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
