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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.LayouterLevel;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

public class DesignerOutputFunction extends DefaultOutputFunction {
  public DesignerOutputFunction() {
  }

  protected ExpressionRuntime updateDetailsHeader( final ReportState state,
                                                   final ProcessingContext processingContext,
                                                   final ReportDefinition report,
                                                   final ExpressionRuntime runtime ) throws ReportProcessingException {
    return runtime;
  }

  protected boolean updateRepeatingFooters( final ReportEvent event,
                                            final LayouterLevel[] levels ) throws ReportProcessingException {
    return false;
  }

  protected boolean isPageFooterPrintable( final Band b, final boolean testSticky ) {
    return true;
  }

  protected boolean isNeedPrintRepeatingFooter( final ReportEvent event, final LayouterLevel[] levels ) {
    return false;
  }

  protected ExpressionRuntime updateRepeatingGroupHeader( final ReportState state,
                                                          final ProcessingContext processingContext,
                                                          final ReportDefinition report,
                                                          final LayouterLevel[] levels,
                                                          final ExpressionRuntime runtime )
    throws ReportProcessingException {
    return runtime;
  }

  protected void printDesigntimeFooter( final ReportEvent event ) throws ReportProcessingException {
    Renderer renderer = getRenderer();
    if ( isPrintHeaderAndFooter( event ) ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), event.getReport().getPageFooter() );
      addSubReportMarkers( renderer.endSection() );
    }
  }

  protected void printDesigntimeHeader( final ReportEvent event ) throws ReportProcessingException {
    Renderer renderer = getRenderer();
    final ReportDefinition report = event.getState().getReport();
    if ( isPrintHeaderAndFooter( event ) ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), report.getPageHeader() );
      addSubReportMarkers( renderer.endSection() );
    }
  }

  protected boolean isPrintHeaderAndFooter( final ReportEvent event ) {
    if ( event.getState().isSubReportEvent() == false ) {
      return false;
    }
    if ( event.getState().isInlineProcess() == false ) {
      return true;
    }
    return false;
  }

  protected boolean isDesignTime() {
    return true;
  }

  protected void printPerformanceStats() {
  }
}
