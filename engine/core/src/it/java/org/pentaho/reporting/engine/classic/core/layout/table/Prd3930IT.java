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

package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabGroupType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.PhysicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

@SuppressWarnings( "HardCodedStringLiteral" )
public class Prd3930IT extends TestCase {
  public Prd3930IT() {
  }

  public Prd3930IT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLargeTableSingleBandCanvas() throws Exception {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 1, 100 );
    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );

    // Test whether the final page has out-of-bounds boxes. The FillPhysicalPages step should have removed them
    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    for ( final LogicalPageBox logicalPageBox : pages ) {
      final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( TableRowRenderBox.class ) );
      for ( int i = 0; i < all.length; i += 1 ) {
        final RenderNode node = all[i];
        // temporary workaround:
        final RenderBox parent = node.getParent();
        if ( parent instanceof TableSectionRenderBox ) {
          final TableSectionRenderBox parentBox = (TableSectionRenderBox) parent;
          if ( parentBox.getDisplayRole() != TableSectionRenderBox.Role.BODY ) {
            continue;
          }
        }

        assertFalse( ( node.getY() + node.getHeight() ) <= logicalPageBox.getPageOffset() );
        assertFalse( node.getY() >= logicalPageBox.getPageEnd() );

        if ( node.getY() < logicalPageBox.getPageEnd()
            && ( node.getY() + node.getHeight() ) > logicalPageBox.getPageEnd() ) {
          fail( " y=" + node.getY() + " height=" + node.getHeight() );
        }
      }
    }
  }

  public void testLargeTableSingleBandBlock() throws Exception {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 1, 100 );
    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );

    // Test whether the final page has out-of-bounds boxes. The FillPhysicalPages step should have removed them
    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    for ( final LogicalPageBox logicalPageBox : pages ) {
      final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( TableRowRenderBox.class ) );
      for ( int i = 0; i < all.length; i += 1 ) {
        final RenderNode node = all[i];
        // temporary workaround:
        final RenderBox parent = node.getParent();
        if ( parent instanceof TableSectionRenderBox ) {
          final TableSectionRenderBox parentBox = (TableSectionRenderBox) parent;
          if ( parentBox.getDisplayRole() != TableSectionRenderBox.Role.BODY ) {
            continue;
          }
        }

        assertFalse( ( node.getY() + node.getHeight() ) <= logicalPageBox.getPageOffset() );
        assertFalse( node.getY() >= logicalPageBox.getPageEnd() );

        if ( node.getY() < logicalPageBox.getPageEnd()
            && ( node.getY() + node.getHeight() ) > logicalPageBox.getPageEnd() ) {
          fail( " y=" + node.getY() + " height=" + node.getHeight() );
        }
      }
    }
  }

  public void testPageBreakOnLargeCrosstab() throws Exception {
    final URL url = getClass().getResource( "Prd-3931.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final ReportElement crosstab = report.getChildElementByType( CrosstabGroupType.INSTANCE );
    crosstab
        .setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Crosstab.DETAIL_MODE, CrosstabDetailMode.first );

    // Test whether the final page has out-of-bounds boxes. The FillPhysicalPages step should have removed them
    final PrintReportProcessor rp = new PrintReportProcessor( report );
    for ( int page = 0; page < rp.getNumberOfPages(); page += 1 ) {
      final PhysicalPageDrawable pageDrawable = (PhysicalPageDrawable) rp.getPageDrawable( page );
      final LogicalPageBox logicalPageBox = pageDrawable.getPageDrawable().getLogicalPageBox();

      // ModelPrinter.print(logicalPageBox);
      // if (true) return;
      final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( TableRowRenderBox.class ) );
      for ( int i = 0; i < all.length; i += 1 ) {
        final RenderNode node = all[i];
        // temporary workaround:
        final RenderBox parent = node.getParent();
        if ( parent instanceof TableSectionRenderBox ) {
          final TableSectionRenderBox parentBox = (TableSectionRenderBox) parent;
          if ( parentBox.getDisplayRole() != TableSectionRenderBox.Role.BODY ) {
            continue;
          }
        }

        assertFalse( ( node.getY() + node.getHeight() ) <= logicalPageBox.getPageOffset() );
        assertFalse( node.getY() >= logicalPageBox.getPageEnd() );

        if ( node.getY() < logicalPageBox.getPageEnd()
            && ( node.getY() + node.getHeight() ) > logicalPageBox.getPageEnd() ) {
          fail( " y=" + node.getY() + " height=" + node.getHeight() );
        }
      }
    }
  }

  private class Prd3930ElementProducer extends TableTestUtil.DefaultElementProducer {
    private Prd3930ElementProducer() {
      super( 100, 10 );
    }

    public Band createCell( final int row, final int column ) {
      return TableTestUtil.createCell( 100, 10, 1, 1 );
    }
  }

  public void testLargeTableSingleBandBlockWithBreaks() throws Exception {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 0, 100, new Prd3930ElementProducer() );
    table.setName( "table" );
    final Band body = (Band) table.getElement( 0 );
    int numberOfPagebreaks = 0;
    for ( int i = 1; i < body.getElementCount(); i += 1 ) {
      if ( i % 20 == 0 ) {
        DebugLog.log( "Add pagebreak at row " + i );
        numberOfPagebreaks += 1;
        body.getElement( i ).getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, true );
      }
    }

    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );

    // Test whether the final page has out-of-bounds boxes. The FillPhysicalPages step should have removed them
    final PrintReportProcessor rp = new PrintReportProcessor( report );
    for ( int page = 0; page < rp.getNumberOfPages(); page += 1 ) {

      final PhysicalPageDrawable pageDrawable = (PhysicalPageDrawable) rp.getPageDrawable( page );
      final LogicalPageBox logicalPageBox = pageDrawable.getPageDrawable().getLogicalPageBox();

      // new FileModelPrinter("Prd-3930-page-" + page + "-", DebugReportRunner.createTestOutputFile()).print
      // (logicalPageBox);

      final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( TableRowRenderBox.class ) );
      for ( int i = 0; i < all.length; i += 1 ) {
        final RenderNode node = all[i];
        // temporary workaround:
        final RenderBox parent = node.getParent();
        if ( parent instanceof TableSectionRenderBox ) {
          final TableSectionRenderBox parentBox = (TableSectionRenderBox) parent;
          if ( parentBox.getDisplayRole() != TableSectionRenderBox.Role.BODY ) {
            continue;
          }
        }

        try {
          assertFalse( ( node.getY() + node.getHeight() ) <= logicalPageBox.getPageOffset() );
          assertFalse( node.getY() >= logicalPageBox.getPageEnd() );

          if ( node.getY() < logicalPageBox.getPageEnd()
              && ( node.getY() + node.getHeight() ) > logicalPageBox.getPageEnd() ) {
            fail( " y=" + node.getY() + " height=" + node.getHeight() );
          }
        } catch ( AssertionFailedError afe ) {
          ModelPrinter.INSTANCE.print( node );
          throw afe;
        }
      }
    }

    assertEquals( numberOfPagebreaks + 1, rp.getNumberOfPages() );

  }

  public void testLargeTableSingleBandBlockTableExport() throws Exception {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 1, 100, true );
    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    XmlTableReportUtil.createFlowXML( report, bout );
    final String text = bout.toString( "UTF-8" );
    for ( int i = 0; i < 100; i += 1 ) {
      assertTrue( text.contains( "value=\"Data-" + i + "-0" ) );
      assertTrue( text.contains( "value=\"Data-" + i + "-1" ) );
      assertTrue( text.contains( ">Data-" + i + "-0</text>" ) );
      assertTrue( text.contains( ">Data-" + i + "-1</text>" ) );
    }
  }

  public void testLargeTableSingleBandBlockTableExportWithBreaks() throws Exception {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 1, 100, new Prd3930ElementProducer() );
    final Band body = (Band) table.getElement( 1 );
    int numberOfPagebreaks = 0;
    for ( int i = 1; i < body.getElementCount(); i += 1 ) {
      if ( i % 20 == 0 ) {
        numberOfPagebreaks += 1;
        body.getElement( i ).getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, true );
      }
    }

    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    XmlTableReportUtil.createFlowXML( report, bout );
    final String text = bout.toString( "UTF-8" );

    for ( int i = 0; i < 100; i += 1 ) {
      assertTrue( "Found data-0 cell", text.contains( "value=\"Data-" + i + "-0" ) );
      assertTrue( "Found data-1 cell", text.contains( "value=\"Data-" + i + "-1" ) );
      assertTrue( "Found data-0 label", text.contains( ">Data-" + i + "-0</text>" ) );
      assertTrue( "Found data-1 label", text.contains( ">Data-" + i + "-1</text>" ) );
    }

    // count table-tags.
    int idx = 0;
    int count = 0;
    while ( idx != -1 ) {
      idx = text.indexOf( "</table>", idx + 1 );
      if ( idx != -1 ) {
        count += 1;
      }
    }

    assertEquals( numberOfPagebreaks + 1, count );
  }

  public void testLargeTableSingleBandBlockTableExportWithBreaksPage() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( PageSize.A4 ) );
    final Band table = TableTestUtil.createTable( 2, 1, 100, new Prd3930ElementProducer() );
    final Band body = (Band) table.getElement( 1 );
    int numberOfPagebreaks = 0;
    for ( int i = 1; i < body.getElementCount(); i += 1 ) {
      if ( i % 20 == 0 ) {
        numberOfPagebreaks += 1;
        body.getElement( i ).getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, true );
      }
    }

    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_BLOCK );

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml( report, bout );
    final String text = bout.toString( "UTF-8" );
    // DebugLog.log(text);
    for ( int i = 0; i < 100; i += 1 ) {
      assertTrue( text.contains( "value=\"Data-" + i + "-0" ) );
      assertTrue( text.contains( "value=\"Data-" + i + "-1" ) );
      assertTrue( text.contains( ">Data-" + i + "-0</text>" ) );
      assertTrue( text.contains( ">Data-" + i + "-1</text>" ) );
    }

    // count table-tags.
    int idx = 0;
    int count = 0;
    while ( idx != -1 ) {
      idx = text.indexOf( "</physical-page>", idx + 1 );
      if ( idx != -1 ) {
        count += 1;
      }
    }

    assertEquals( numberOfPagebreaks + 1, count );
  }

  public void testPageBreakOnCT2() throws Exception {
    final URL url = getClass().getResource( "Crashing-crosstab.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final ReportElement crosstabCell = report.getChildElementByType( CrosstabCellType.INSTANCE );
    // crosstabCell.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.CROSSTAB_DETAIL_MODE,
    // CrosstabDetailMode.first);

    // Test whether the final page has out-of-bounds boxes. The FillPhysicalPages step should have removed them
    final PrintReportProcessor rp = new PrintReportProcessor( report );
    DebugLog.log( "Pages: " + rp.getNumberOfPages() );
    for ( int page = 0; page < rp.getNumberOfPages(); page += 1 ) {
      final PhysicalPageDrawable pageDrawable = (PhysicalPageDrawable) rp.getPageDrawable( page );
      final LogicalPageBox logicalPageBox = pageDrawable.getPageDrawable().getLogicalPageBox();

      // ModelPrinter.print(logicalPageBox);
      // if (true) return;
      final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( TableRowRenderBox.class ) );
      for ( int i = 0; i < all.length; i += 1 ) {
        final RenderNode node = all[i];
        // temporary workaround:
        final RenderBox parent = node.getParent();
        if ( parent instanceof TableSectionRenderBox ) {
          final TableSectionRenderBox parentBox = (TableSectionRenderBox) parent;
          if ( parentBox.getDisplayRole() != TableSectionRenderBox.Role.BODY ) {
            continue;
          }
        }

        assertFalse( ( node.getY() + node.getHeight() ) <= logicalPageBox.getPageOffset() );
        assertFalse( node.getY() >= logicalPageBox.getPageEnd() );
      }
    }
  }

}
