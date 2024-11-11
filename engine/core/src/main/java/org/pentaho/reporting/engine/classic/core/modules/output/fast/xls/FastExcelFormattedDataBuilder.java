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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractFormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.CellLayoutInfo;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FastExcelFormattedDataBuilder extends AbstractFormattedDataBuilder {
  private final HashMap<InstanceID, CellLayoutInfo> layout;
  private ArrayList<CellLayoutInfo> backgroundCells;
  private long[] cellHeights;
  private final FastExcelPrinter excelPrinter;

  public FastExcelFormattedDataBuilder( final HashMap<InstanceID, CellLayoutInfo> layout,
      final ArrayList<CellLayoutInfo> backgroundCells, final long[] cellHeights, final FastExcelPrinter excelPrinter ) {
    this.layout = layout;
    this.backgroundCells = backgroundCells;
    this.cellHeights = cellHeights;
    this.excelPrinter = excelPrinter;
  }

  public void compute( final Band band, final ExpressionRuntime runtime, final OutputStream out )
    throws ReportProcessingException, ContentProcessingException, IOException {
    SimpleStyleSheet computedStyle = band.getComputedStyle();
    if ( computedStyle.getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return;
    }

    this.excelPrinter.startSection( band, cellHeights );
    compute( band, runtime );
    this.excelPrinter.endSection( band, backgroundCells );
  }

  protected void inspect( final AbstractReportDefinition reportDefinition ) {
    inspectElement( reportDefinition );
  }

  protected void inspectElement( final ReportElement element ) {
    CellLayoutInfo tableRectangle = layout.get( element.getObjectID() );
    if ( tableRectangle != null ) {
      try {
        this.excelPrinter.print( tableRectangle, element, getRuntime() );
      } catch ( ContentProcessingException e ) {
        throw new InvalidReportStateException( e );
      }
    }
  }
}
