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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;

import java.io.IOException;
import java.util.HashMap;

public abstract class AbstractContentProducerTemplate implements FastExportTemplate {
  private SheetLayout sharedSheetLayout;
  private OutputProcessorMetaData metaData;
  private HashMap<DynamicStyleKey, FormattedDataBuilder> bandFormatter;

  public AbstractContentProducerTemplate( final SheetLayout sharedSheetLayout ) {
    this.sharedSheetLayout = sharedSheetLayout;
    this.bandFormatter = new HashMap<DynamicStyleKey, FormattedDataBuilder>();
  }

  protected OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected SheetLayout getSharedSheetLayout() {
    return sharedSheetLayout;
  }

  public void write( final Band band, final ExpressionRuntime runtime ) throws InvalidReportStateException {
    try {
      DynamicStyleKey dynamicStyleKey = DynamicStyleKey.create( band, runtime );
      FormattedDataBuilder messageFormatSupport = bandFormatter.get( dynamicStyleKey );
      if ( messageFormatSupport == null ) {
        messageFormatSupport = createTemplate( band, runtime );
        bandFormatter.put( dynamicStyleKey, messageFormatSupport );
      }

      writeContent( band, runtime, messageFormatSupport );
    } catch ( IOException e ) {
      throw new InvalidReportStateException( "Failed to write content", e );
    } catch ( ContentProcessingException e ) {
      throw new InvalidReportStateException( "Failed to write content", e );
    } catch ( ReportProcessingException e ) {
      throw new InvalidReportStateException( "Failed to write content", e );
    }
  }

  public void finishReport() throws ReportProcessingException {

  }

  protected abstract void writeContent( final Band band, final ExpressionRuntime runtime,
      final FormattedDataBuilder messageFormatSupport ) throws IOException, ReportProcessingException,
    ContentProcessingException;

  public void initialize( final ReportDefinition report, final ExpressionRuntime runtime, final boolean pagination ) {
    metaData = runtime.getProcessingContext().getOutputProcessorMetaData();
  }

  protected FormattedDataBuilder createTemplate( final Band band, final ExpressionRuntime runtime )
    throws ReportProcessingException, ContentProcessingException {
    FastExportTemplateProducer templateListener = createTemplateProducer();
    final OutputProcessor op =
        new TemplatingOutputProcessor( runtime.getProcessingContext().getOutputProcessorMetaData(), templateListener );
    FastSheetLayoutProducer.performLayout( band, runtime, op );
    return templateListener.createDataBuilder();
  }

  protected abstract FastExportTemplateProducer createTemplateProducer();

}
