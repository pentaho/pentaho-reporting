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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastSheetLayoutProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;

import java.io.OutputStream;

public class FastExcelExportTemplate implements FastExportTemplate {
  private OutputStream outputStream;
  private boolean useXlsx;
  private SheetLayout sharedSheetLayout;
  private FastExportTemplate processor;

  public FastExcelExportTemplate( final OutputStream outputStream, final boolean useXlsx ) {
    this.outputStream = outputStream;
    this.useXlsx = useXlsx;
  }

  public void initialize( final ReportDefinition report, final ExpressionRuntime runtime, final boolean pagination ) {
    OutputProcessorMetaData metaData = runtime.getProcessingContext().getOutputProcessorMetaData();
    if ( pagination ) {
      this.sharedSheetLayout = new SheetLayout( metaData );
      this.processor = new FastSheetLayoutProducer( sharedSheetLayout );
      this.processor.initialize( report, runtime, pagination );
    } else {
      this.processor = new FastExcelContentProducerTemplate( sharedSheetLayout, outputStream, useXlsx );
      this.processor.initialize( report, runtime, pagination );
    }
  }

  public void write( final Band band, final ExpressionRuntime runtime ) throws InvalidReportStateException {
    try {
      this.processor.write( band, runtime );
    } catch ( InvalidReportStateException re ) {
      throw re;
    } catch ( Exception e ) {
      throw new InvalidReportStateException( "Other failure", e );
    }
  }

  public void finishReport() throws ReportProcessingException {
    this.processor.finishReport();
  }
}
