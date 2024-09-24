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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractContentProducerTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.libraries.repository.ContentIOException;

import java.io.IOException;

public class FastHtmlContentProducerTemplate extends AbstractContentProducerTemplate {
  private FastHtmlContentItems contentItems;
  private FastHtmlPrinter htmlPrinter;

  public FastHtmlContentProducerTemplate( final SheetLayout sheetLayout, final FastHtmlContentItems contentItems ) {
    super( sheetLayout );
    this.contentItems = contentItems;
  }

  public void initialize( final ReportDefinition report, final ExpressionRuntime runtime, final boolean pagination ) {
    super.initialize( report, runtime, pagination );
    this.htmlPrinter =
        new FastHtmlPrinter( getSharedSheetLayout(), runtime.getProcessingContext().getResourceManager(), contentItems );
    this.htmlPrinter.init( getMetaData(), report );
  }

  protected void writeContent( final Band band, final ExpressionRuntime runtime,
      final FormattedDataBuilder messageFormatSupport ) throws IOException, ReportProcessingException,
    ContentProcessingException {
    messageFormatSupport.compute( band, runtime, null );
  }

  public void finishReport() throws ReportProcessingException {
    try {
      this.htmlPrinter.close();
    } catch ( IOException e ) {
      throw new ReportProcessingException( "Failed to close report", e );
    } catch ( ContentIOException e ) {
      throw new ReportProcessingException( "Failed to close report", e );
    }
  }

  protected FastExportTemplateProducer createTemplateProducer() {
    return new FastHtmlTemplateProducer( getMetaData(), getSharedSheetLayout(), htmlPrinter );
  }
}
