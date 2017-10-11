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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import java.util.List;

@SuppressWarnings( "HardCodedStringLiteral" )
public class KeepTogetherIT extends TestCase {
  public KeepTogetherIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "2", "row-1" );
    model.addRow( "b", "1", "row-2" );
    model.addRow( "b", "2", "row-3" );
    model.addRow( "b", "2", "row-4" );
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
    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE,
        Boolean.TRUE );
    group1.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE );

    // PdfReportUtil.createPDF(report, "/tmp/WidowTest.pdf");
    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2, 3, 4, 5, 6, 7 );

    final LogicalPageBox page1 = pages.get( 0 );
    assertElementExists( "outer-header-field", page1 );
    assertElementExists( "inner-footer-field", page1 );
    assertElementExists( "row-0", page1 );
    assertElementExists( "inner-header-field", page1 );

    final LogicalPageBox page2 = pages.get( 1 );
    assertElementExists( "inner-header-field", page2 );
    assertElementExists( "row-1", page2 );
    assertElementExists( "inner-footer-field", page2 );
    assertElementExists( "outer-footer-field", page2 );
    assertElementExists( "outer-header-field", page2 );

    final LogicalPageBox page3 = pages.get( 2 );
    assertElementExists( "inner-header-field", page3 );
    assertElementExists( "row-2", page3 );
    assertElementExists( "inner-footer-field", page3 );
    assertElementDoesNotExist( "row-3", page3 );

    final LogicalPageBox page4 = pages.get( 3 );
    assertElementExists( "inner-header-field", page4 );
    assertElementExists( "row-5", page4 );
    assertElementDoesNotExist( "row-6", page4 );

    final LogicalPageBox page5 = pages.get( 4 );
    assertElementExists( "inner-header-field", page5 );
    assertElementExists( "row-6", page5 );
    assertElementDoesNotExist( "row-7", page5 );

    final LogicalPageBox page6 = pages.get( 5 );
    assertElementExists( "inner-header-field", page6 );
    assertElementExists( "row-7", page6 );
    assertElementDoesNotExist( "row-8", page6 );

    final LogicalPageBox page7 = pages.get( 6 );
    assertElementExists( "inner-header-field", page7 );
    assertElementExists( "row-8", page7 );
    assertElementDoesNotExist( "row-9", page7 );

    final LogicalPageBox page8 = pages.get( 7 );
    assertElementExists( "inner-header-field", page8 );
    assertElementExists( "row-9", page8 );
    assertElementExists( "inner-footer-field", page8 );
    assertElementExists( "outer-footer-field", page8 );

    // BundleWriter.writeReportToZipFile(report, "/tmp/Prd-2087-Keep-Together-0.prpt");
  }

  public void testSubReport2() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "2", "row-1" );
    model.addRow( "b", "1", "row-2" );
    model.addRow( "b", "2", "row-3" );
    model.addRow( "b", "2", "row-4" );
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
    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE,
        Boolean.TRUE );
    group0.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE );

    final MasterReport master = new MasterReport();
    master.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );
    master.getReportHeader().addElement( report );

    // BundleWriter.writeReportToZipFile(master, "/tmp/Prd-2087-Keep-Together-2.prpt");
    // PdfReportUtil.createPDF(master, "/tmp/KeepTogetherTest5.pdf");
    // DebugReportRunner.createPDF(master);
    DebugReportRunner.createPDF( master );
  }

  public void testReport2() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "g0", String.class );
    model.addColumn( "g1", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "a", "1", "row-0" );
    model.addRow( "a", "2", "row-1" );
    model.addRow( "b", "1", "row-2" );
    model.addRow( "b", "2", "row-3" );
    model.addRow( "b", "2", "row-4" );
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
    final RelationalGroup group1 = (RelationalGroup) report.getGroup( 1 );
    group1.setName( "inner-group" );
    group1.addField( "g1" );
    group1.getHeader().addElement( createDataItem( "inner-header-field", 100, 20 ) );
    group1.getFooter().addElement( createDataItem( "inner-footer-field", 100, 20 ) );
    report.getItemBand().addElement( createFieldItem( "detail-field", 100, 20 ) );
    report.getItemBand().getParentSection().getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE,
        Boolean.TRUE );
    group0.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE );

    DebugReportRunner.createPDF( report );
    // BundleWriter.writeReportToZipFile(report, "/tmp/Prd-2087-Keep-Together-1.prpt");
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

  private static void assertElementDoesNotExist( String element, RenderBox box ) {
    final RenderNode ib1 = MatchFactory.findElementByName( box, element );
    assertNull( "Element '" + element + "' does NOT exist.", ib1 );
  }

  private static void assertElementExists( String element, RenderBox box ) {
    final RenderNode ib1 = MatchFactory.findElementByName( box, element );
    assertNotNull( "Element '" + element + "' exists.", ib1 );
  }
}
