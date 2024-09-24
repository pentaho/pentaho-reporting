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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;

public class RenderedCrosstabLayout implements Cloneable {
  private boolean crosstabTableOpen;
  private boolean crosstabRowOpen;
  private boolean processingCrosstabHeader;
  private boolean crosstabHeaderOpen;
  private boolean detailsRendered;
  private boolean generateMeasureHeaders;
  private boolean generateColumnTitleHeaders;
  private boolean summaryRowPrintable;
  private int summaryRowGroupIndex;
  private String summaryRowField;

  private CrosstabSpecification crosstabSpecification;
  private int columnGroups;
  private int rowGroups;
  private int otherGroups;
  private String[] sortedKeys;
  private int crosstabGroupIndex;
  private InstanceID[] columnHeaderSubflows;
  private InstanceID[] rowHeaders;
  private InstanceID[] columnHeaders;
  private InstanceID[] columnTitleHeaders;
  private InstanceID crosstabId;

  private int firstRowGroupIndex;
  private int firstColGroupIndex;
  private CrosstabDetailMode detailMode;
  private TableLayout tableLayout;

  public RenderedCrosstabLayout() {
    firstRowGroupIndex = -1;
    firstColGroupIndex = -1;
  }

  public int getFirstRowGroupIndex() {
    return firstRowGroupIndex;
  }

  public void setFirstRowGroupIndex( final int firstRowGroupIndex ) {
    this.firstRowGroupIndex = firstRowGroupIndex;
  }

  public boolean isGenerateMeasureHeaders() {
    return generateMeasureHeaders;
  }

  public boolean isGenerateColumnTitleHeaders() {
    return generateColumnTitleHeaders;
  }

  public boolean isDetailsRendered() {
    return detailsRendered;
  }

  public void setDetailsRendered( final boolean detailsRendered ) {
    this.detailsRendered = detailsRendered;
  }

  public boolean isCrosstabRowOpen() {
    return crosstabRowOpen;
  }

  public void setCrosstabRowOpen( final boolean crosstabRowOpen ) {
    this.crosstabRowOpen = crosstabRowOpen;
  }

  public boolean isCrosstabTableOpen() {
    return crosstabTableOpen;
  }

  public boolean isCrosstabHeaderOpen() {
    return crosstabHeaderOpen;
  }

  public void setCrosstabHeaderOpen( final boolean crosstabHeaderOpen ) {
    this.crosstabHeaderOpen = crosstabHeaderOpen;
  }

  public void setCrosstabTableOpen( final boolean crosstabTableOpen ) {
    this.crosstabTableOpen = crosstabTableOpen;
  }

  public boolean isProcessingCrosstabHeader() {
    return processingCrosstabHeader;
  }

  public void setProcessingCrosstabHeader( final boolean processingCrosstabHeader ) {
    this.processingCrosstabHeader = processingCrosstabHeader;
  }

  public CrosstabSpecification getCrosstabSpecification() {
    return crosstabSpecification;
  }

  public int getColumnGroups() {
    return columnGroups;
  }

  public int getRowGroups() {
    return rowGroups;
  }

  public int getOtherGroups() {
    return otherGroups;
  }

  public String[] getSortedKeys() {
    return sortedKeys;
  }

  public int getCrosstabGroupIndex() {
    return crosstabGroupIndex;
  }

  public Object clone() {
    try {
      final RenderedCrosstabLayout layout = (RenderedCrosstabLayout) super.clone();
      if ( columnHeaderSubflows != null ) {
        layout.columnHeaderSubflows = columnHeaderSubflows.clone();
      }
      if ( rowHeaders != null ) {
        layout.rowHeaders = rowHeaders.clone();
      }
      if ( columnHeaders != null ) {
        layout.columnHeaders = columnHeaders.clone();
      }
      if ( columnTitleHeaders != null ) {
        layout.columnTitleHeaders = columnTitleHeaders.clone();
      }
      return layout;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public RenderedCrosstabLayout derive() {
    return (RenderedCrosstabLayout) clone();
  }

  public void initialize( final CrosstabSpecification crosstabSpecification, final CrosstabGroup group,
      final int crosstabGroupIndex ) {
    this.crosstabSpecification = crosstabSpecification;
    this.crosstabGroupIndex = crosstabGroupIndex;
    computeGroupCounts( group );

    CrosstabDetailMode detailMode = group.getDetailsMode();
    if ( detailMode == null ) {
      detailMode = CrosstabDetailMode.last;
    }
    this.detailMode = detailMode;
    this.generateMeasureHeaders =
        !( Boolean.FALSE.equals( group.getAttribute( AttributeNames.Crosstab.NAMESPACE,
            AttributeNames.Crosstab.PRINT_DETAIL_HEADER ) ) );
    this.generateColumnTitleHeaders =
        !( Boolean.FALSE.equals( group.getAttribute( AttributeNames.Crosstab.NAMESPACE,
            AttributeNames.Crosstab.PRINT_COLUMN_TITLE_HEADER ) ) );
    this.tableLayout = (TableLayout) group.getStyle().getStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );
  }

  private void computeGroupCounts( final CrosstabGroup crosstabGroup ) {
    final ArrayList<String> list = new ArrayList<String>();
    GroupBody body = crosstabGroup.getBody();
    while ( body != null ) {
      if ( body instanceof CrosstabOtherGroupBody ) {
        otherGroups += 1;
        final CrosstabOtherGroupBody cogb = (CrosstabOtherGroupBody) body;
        final CrosstabOtherGroup otherGroup = cogb.getGroup();
        list.add( otherGroup.getField() );
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabRowGroupBody ) {
        rowGroups += 1;
        final CrosstabRowGroupBody cogb = (CrosstabRowGroupBody) body;
        final CrosstabRowGroup otherGroup = cogb.getGroup();
        list.add( otherGroup.getField() );
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabColumnGroupBody ) {
        columnGroups += 1;
        final CrosstabColumnGroupBody cogb = (CrosstabColumnGroupBody) body;
        final CrosstabColumnGroup otherGroup = cogb.getGroup();
        list.add( otherGroup.getField() );
        body = otherGroup.getBody();
        continue;
      }

      break;
    }

    rowHeaders = new InstanceID[rowGroups];
    columnHeaders = new InstanceID[columnGroups];
    columnTitleHeaders = new InstanceID[columnGroups];
    sortedKeys = list.toArray( new String[list.size()] );
  }

  public void setColumnHeaderRowIds( final InstanceID[] columnHeaders ) {
    if ( columnHeaders == null ) {
      throw new NullPointerException();
    }
    if ( columnHeaders.length < 1 ) {
      throw new IllegalStateException();
    }
    this.columnHeaderSubflows = columnHeaders;
  }

  public InstanceID[] getColumnHeaderSubFlows() {
    return columnHeaderSubflows;
  }

  public InstanceID getRowTitleHeaderId() {
    if ( columnHeaderSubflows == null ) {
      throw new IllegalStateException();
    }
    return columnHeaderSubflows[columnHeaderSubflows.length - 1];
  }

  public InstanceID getColumnTitleHeaderSubflowId( final int gidx ) {
    if ( generateColumnTitleHeaders == false ) {
      throw new InvalidReportStateException();
    }
    final int offset = gidx - crosstabGroupIndex - otherGroups - rowGroups - 1;
    return columnHeaderSubflows[offset * 2];
  }

  public InstanceID getColumnHeaderSubflowId( final int gidx ) {
    final int offset = gidx - crosstabGroupIndex - otherGroups - rowGroups - 1;
    if ( generateColumnTitleHeaders ) {
      return columnHeaderSubflows[offset * 2 + 1];
    } else {
      return columnHeaderSubflows[offset];
    }
  }

  public InstanceID getMeasureHeaderSubflowId() {
    if ( generateMeasureHeaders == false ) {
      throw new InvalidReportStateException();
    }
    return columnHeaderSubflows[columnHeaderSubflows.length - 1];
  }

  public void setRowHeader( final int index, final InstanceID instanceId ) {
    rowHeaders[index] = instanceId;
  }

  public InstanceID getRowHeader( final int index ) {
    return rowHeaders[index];
  }

  public void setColumnHeaderCellId( final int index, final InstanceID instanceId ) {
    columnHeaders[index] = instanceId;
  }

  public InstanceID getColumnHeaderCellId( final int index ) {
    return columnHeaders[index];
  }

  public void setColumnTitleHeaderCellId( final int index, final InstanceID instanceId ) {
    columnTitleHeaders[index] = instanceId;
  }

  public InstanceID getColumnTitleHeaderCellId( final int index ) {
    return columnTitleHeaders[index];
  }

  public int getFirstColGroupIndex() {
    return firstColGroupIndex;
  }

  public void setFirstColGroupIndex( final int firstColGroupIndex ) {
    this.firstColGroupIndex = firstColGroupIndex;
  }

  public CrosstabDetailMode getDetailMode() {
    return detailMode;
  }

  public void startSummaryRowProcessing( final boolean summaryRowPrintable, final int summaryRowGroupIndex,
      final String summaryRowField ) {
    this.summaryRowPrintable = summaryRowPrintable;
    this.summaryRowGroupIndex = summaryRowGroupIndex;
    this.summaryRowField = summaryRowField;
  }

  public void endSummaryRowProcessing() {
    this.summaryRowPrintable = false;
    this.summaryRowGroupIndex = -1;
    this.summaryRowField = null;
  }

  public String getSummaryRowField() {
    return summaryRowField;
  }

  public int getSummaryRowGroupIndex() {
    return summaryRowGroupIndex;
  }

  public boolean isSummaryRowPrintable() {
    return summaryRowPrintable;
  }

  public TableLayout getTableLayout() {
    return tableLayout;
  }

  public InstanceID getCrosstabId() {
    return crosstabId;
  }

  public void setCrosstabId( final InstanceID crosstabId ) {
    this.crosstabId = crosstabId;
  }
}
