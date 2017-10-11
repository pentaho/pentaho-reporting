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

package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBoxNonAutoIterator;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.style.resolver.StyleResolver;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public final class CrosstabOutputHelper {
  private CrosstabOutputHelper() {

  }

  public static TableSectionRenderBox findTableHeaderSection( RenderNode node ) {
    RenderBox tableBox = null;
    while ( node != null ) {
      if ( node.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
        tableBox = (RenderBox) node;
        break;
      }
      node = node.getParent();
    }
    return getTableSectionRenderBox( tableBox );
  }

  public static TableSectionRenderBox getTableSectionRenderBox( final RenderBox tableBox ) {
    if ( tableBox == null ) {
      return null;
    }
    final RenderBoxNonAutoIterator it = new RenderBoxNonAutoIterator( tableBox );
    while ( it.hasNext() ) {
      final RenderNode next = it.next();
      if ( next.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
        final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) next;
        if ( sectionRenderBox.getDisplayRole() == TableSectionRenderBox.Role.HEADER ) {
          return sectionRenderBox;
        }
      }
    }
    return null;
  }

  public static TableSectionRenderBox findTableSection( RenderNode node ) {
    while ( node != null ) {
      if ( node.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
        return (TableSectionRenderBox) node;
      }
      node = node.getParent();
    }
    return null;
  }

  public static RenderNode findNode( final TableSectionRenderBox node, final InstanceID id ) {
    if ( id == null ) {
      return null;
    }

    if ( node == null ) {
      return null;
    }

    return node.findNodeById( id );
  }

  public static Element createTableCell( final int colSpan, final int rowSpan, final boolean pagebreakBefore,
      final boolean pagebreakAfter ) {
    final CrosstabTableCell b = new CrosstabTableCell( colSpan, rowSpan );
    b.getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, pagebreakBefore );
    b.getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, pagebreakAfter );

    final StyleResolver resolver = new SimpleStyleResolver();
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    resolver.resolve( b, resolverTarget );
    b.setComputedStyle( new SimpleStyleSheet( resolverTarget ) );
    return b;
  }

  public static Band createTableRow() {
    return createTableBand( BandStyleKeys.LAYOUT_TABLE_ROW );
  }

  public static Band createTable( final TableLayout tableLayout ) {
    final Band b = new Band();
    b.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    b.getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, true );
    b.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, tableLayout );

    final StyleResolver resolver = new SimpleStyleResolver();
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    resolver.resolve( b, resolverTarget );
    b.setComputedStyle( new SimpleStyleSheet( resolverTarget ) );
    return b;
  }

  public static Band createTableBand( final String layout ) {
    final Band b = new Band();
    b.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, layout );
    b.getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, true );

    final StyleResolver resolver = new SimpleStyleResolver();
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    resolver.resolve( b, resolverTarget );
    b.setComputedStyle( new SimpleStyleSheet( resolverTarget ) );
    return b;
  }

  public static boolean isLastColumnGroup( final ReportEvent event ) {
    final int gidx = event.getState().getCurrentGroupIndex();
    final Group group = event.getReport().getGroup( gidx );
    if ( group.getBody() instanceof CrosstabCellBody ) {
      return true;
    }
    return false;
  }

  public static boolean closeCrosstabTable( final DefaultOutputFunction outputFunction ) {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( crosstabLayout.isCrosstabTableOpen() ) {
      // close the table.
      outputFunction.getRenderer().getNormalFlowLayoutModelBuilder().finishBox(); // table-body
      outputFunction.getRenderer().getNormalFlowLayoutModelBuilder().finishBox(); // table
      crosstabLayout.setCrosstabTableOpen( false );
      return true;
    }
    return false;
  }

  public static void printCrosstabSummary( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    // column summary is delayed by one level. So when we receive a group-finished for the inner most col-group,
    // we do not print a summary footer. The summary header for the inner most col-group is printed when the
    // previous group has finished.
    //
    // Example: A crosstab with one column group and one row group.
    //
    // Row Col Data
    // ----------------
    // R0 C0 1
    // R0 C1 2
    // R1 C0 3
    // R1 C1 4
    //
    // Both groups have summaries printed. The expected output would be:
    //
    // C0 C1 CSum
    // -------------------
    // R0 1 2 3
    // R1 3 4 7
    // RSum 4 6 10
    //
    // The contents of a single cell can consist of multiple data entries. The (classical) footer
    // printing does not happen for detail level group.
    //
    // The summary along a x-axis is defined in the column groups. The header for CSum is defined in the col-group,
    // the content for the cell is defined in a cell with the key "Col", and printed when the next group
    // finishes (here: row group).
    //
    // The summary along the y-axis is defined in the row groups. The row group header is defined in the
    // row-group itself, the content for the summary cell is contained in a cell with the key "Row". The row
    // summary is printed (in the same way as column groups) with a -1 delay. So in this example, the row sums
    // would be printed when the crosstab-other groups or crosstab groups finish.
    //
    // The total summary (10) does not have a own header, as it is the aggregation of an aggregation. The
    // contents for the cell are held in a cell with the keys "Row" and "Col" (set; order does not matter).
    // If that cell does not exist, we search for a row cell, if that does not exist we search for "Col".

    final int gidx = event.getState().getCurrentGroupIndex() + 1;
    final Group rawGroup = event.getReport().getGroup( gidx );
    if ( rawGroup instanceof CrosstabColumnGroup == false ) {
      return;
    }

    final CrosstabColumnGroup group = (CrosstabColumnGroup) rawGroup;
    if ( group.isPrintSummary() == false ) {
      return;
    }

    final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
    final CrosstabCell element = dataBody.findElement( null, group.getField() );
    if ( element == null ) {
      return;
    }

    // handle column summary. This can happen inline, with no new states fired.
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();

    if ( crosstabLayout.isCrosstabHeaderOpen() ) {
      // Expand all parent group cell-spans by one.

      expandColumnHeaderSpan( crosstabLayout, layoutModelBuilder, gidx );

      // and finally print the title-header and the summary header
      if ( crosstabLayout.isGenerateColumnTitleHeaders() ) {
        layoutModelBuilder.startSubFlow( crosstabLayout.getColumnTitleHeaderSubflowId( gidx ) );
        createAutomaticCell( layoutModelBuilder );
        crosstabLayout.setColumnTitleHeaderCellId( gidx - crosstabLayout.getFirstColGroupIndex(), layoutModelBuilder
            .dangerousRawAccess().getInstanceId() );
        outputFunction.getRenderer().add( group.getTitleHeader(), outputFunction.getRuntime() );
        layoutModelBuilder.finishBox();
        layoutModelBuilder.suspendSubFlow();
      }

      layoutModelBuilder.startSubFlow( crosstabLayout.getColumnHeaderSubflowId( gidx ) );
      createAutomaticCell( layoutModelBuilder );
      crosstabLayout.setColumnHeaderCellId( gidx - crosstabLayout.getFirstColGroupIndex(), layoutModelBuilder
          .dangerousRawAccess().getInstanceId() );
      outputFunction.getRenderer().add( group.getSummaryHeader(), outputFunction.getRuntime() );
      layoutModelBuilder.finishBox();
      layoutModelBuilder.suspendSubFlow();

      if ( crosstabLayout.isGenerateMeasureHeaders() ) {
        layoutModelBuilder.startSubFlow( crosstabLayout.getMeasureHeaderSubflowId() );
        createAutomaticCell( layoutModelBuilder );
        outputFunction.getRenderer().add( dataBody.getHeader(), outputFunction.getRuntime() );
        layoutModelBuilder.finishBox();
        layoutModelBuilder.suspendSubFlow();
      }
    }

    // now print the summary cell.
    createAutomaticCell( layoutModelBuilder );
    layoutModelBuilder.legacyFlagNotEmpty();

    outputFunction.getRenderer().startSection( Renderer.SectionType.NORMALFLOW );
    outputFunction.getRenderer().add( element, outputFunction.getRuntime() );
    outputFunction.addSubReportMarkers( outputFunction.getRenderer().endSection() );

    layoutModelBuilder.finishBox();
  }

  public static void expandColumnHeaderSpan( final RenderedCrosstabLayout crosstabLayout,
      final LayoutModelBuilder layoutModelBuilder, final int gidx ) {
    final TableSectionRenderBox section =
        CrosstabOutputHelper.findTableHeaderSection( layoutModelBuilder.dangerousRawAccess() );

    for ( int i = crosstabLayout.getFirstColGroupIndex(), count = 0; i < gidx; i += 1, count += 1 ) {
      if ( crosstabLayout.isGenerateColumnTitleHeaders() ) {
        final InstanceID columnTitleHeaderId =
            crosstabLayout.getColumnTitleHeaderCellId( i - crosstabLayout.getFirstColGroupIndex() );
        final RenderNode columnTitleHeaderCell = CrosstabOutputHelper.findNode( section, columnTitleHeaderId );
        if ( columnTitleHeaderCell instanceof TableCellRenderBox ) {
          final TableCellRenderBox cellBox = (TableCellRenderBox) columnTitleHeaderCell;
          cellBox.update( cellBox.getRowSpan(), cellBox.getColSpan() + 1 );
        } else {
          throw new IllegalStateException(
              "Unable to find node for previous column title header. Aborting report processing." );
        }
      }

      final InstanceID columnHeaderId =
          crosstabLayout.getColumnHeaderCellId( i - crosstabLayout.getFirstColGroupIndex() );
      final RenderNode columnHeaderCell = CrosstabOutputHelper.findNode( section, columnHeaderId );
      if ( columnHeaderCell instanceof TableCellRenderBox ) {
        final TableCellRenderBox cellBox = (TableCellRenderBox) columnHeaderCell;
        cellBox.update( cellBox.getRowSpan(), cellBox.getColSpan() + 1 );
      } else {
        throw new IllegalStateException(
            "Unable to find node for previous column title header. Aborting report processing." );
      }
    }
  }

  public static void createAutomaticCell( final LayoutModelBuilder layoutModelBuilder, final int colSpan,
      final int rowSpan, final Element element ) {
    final boolean pagebreakBefore = element.getComputedStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE );
    final boolean pagebreakAfter = element.getComputedStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER );
    createAutomaticCell( layoutModelBuilder, colSpan, rowSpan, pagebreakBefore, pagebreakAfter );
  }

  public static void createAutomaticCell( final LayoutModelBuilder layoutModelBuilder ) {
    createAutomaticCell( layoutModelBuilder, 1, 1, false, false );
  }

  public static void createAutomaticCell( final LayoutModelBuilder layoutModelBuilder, final int colSpan,
      final int rowSpan ) {
    createAutomaticCell( layoutModelBuilder, colSpan, rowSpan, false, false );
  }

  private static void createAutomaticCell( final LayoutModelBuilder layoutModelBuilder, final int colSpan,
      final int rowSpan, final boolean pagebreakBefore, final boolean pagebreakAfter ) {
    final Element tableCell = createTableCell( colSpan, rowSpan, pagebreakBefore, pagebreakAfter );
    layoutModelBuilder.startBox( tableCell );

  }

  public static RenderNode findParentNode( RenderNode renderNode, final InstanceID crosstabId ) {
    while ( renderNode != null ) {
      if ( renderNode.getInstanceId() == crosstabId ) {
        return renderNode;
      }
      renderNode = renderNode.getParent();
    }

    return null;
  }
}
