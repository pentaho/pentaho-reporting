/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

public class FastExcelFormattedDataBuilder extends AbstractFormattedDataBuilder
{
  private final HashMap<InstanceID, CellLayoutInfo> layout;
  private ArrayList<CellLayoutInfo> backgroundCells;
  private long[] cellHeights;
  private final FastExcelPrinter excelPrinter;

  public FastExcelFormattedDataBuilder(final HashMap<InstanceID, CellLayoutInfo> layout,
                                       final ArrayList<CellLayoutInfo> backgroundCells,
                                       final long[] cellHeights,
                                       final FastExcelPrinter excelPrinter)
  {
    this.layout = layout;
    this.backgroundCells = backgroundCells;
    this.cellHeights = cellHeights;
    this.excelPrinter = excelPrinter;
  }

  public void compute(final Band band,
                      final ExpressionRuntime runtime,
                      final OutputStream out)
      throws ReportProcessingException, ContentProcessingException, IOException
  {
    SimpleStyleSheet computedStyle = band.getComputedStyle();
    if (computedStyle.getBooleanStyleProperty(ElementStyleKeys.VISIBLE) == false)
    {
      return;
    }

    this.excelPrinter.startSection(band, cellHeights);
    compute(band, runtime);
    this.excelPrinter.endSection(band, backgroundCells);
  }

  protected void inspect(final AbstractReportDefinition reportDefinition)
  {
    inspectElement(reportDefinition);
  }

  protected void inspectElement(final ReportElement element)
  {
    CellLayoutInfo tableRectangle = layout.get(element.getObjectID());
    if (tableRectangle != null)
    {
      try
      {
        this.excelPrinter.print(tableRectangle, element, getRuntime());
      }
      catch (ContentProcessingException e)
      {
        throw new InvalidReportStateException(e);
      }
    }
  }
}
