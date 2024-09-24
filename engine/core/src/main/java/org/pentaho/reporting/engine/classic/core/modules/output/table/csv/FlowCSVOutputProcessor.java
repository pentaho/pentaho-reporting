/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.csv;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.helper.CSVOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

/**
 * Creation-Date: 09.05.2007, 14:36:28
 *
 * @author Thomas Morgner
 */
public class FlowCSVOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private CSVPrinter printer;

  public FlowCSVOutputProcessor( final Configuration config ) {
    if ( config == null ) {
      throw new NullPointerException();
    }

    this.metaData = new CSVOutputProcessorMetaData( CSVOutputProcessorMetaData.PAGINATION_MANUAL );
    this.flowSelector = new DisplayAllFlowSelector();
    this.printer = new CSVPrinter();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
    super.processingStarted( report, processingContext );
    this.printer.initialize( processingContext.getConfiguration() );
  }

  public String getEncoding() {
    return printer.getEncoding();
  }

  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }

    printer.setEncoding( encoding );
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void setFlowSelector( final FlowSelector flowSelector ) {
    this.flowSelector = flowSelector;
  }

  public FlowSelector getFlowSelector() {
    return flowSelector;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    printer.print( logicalPage, contentProducer, metaData, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    printer.print( logicalPageBox, tableContentProducer, metaData, true );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.printer.close();
    this.metaData.commit();
  }

  public ContentLocation getContentLocation() {
    return printer.getContentLocation();
  }

  public void setContentLocation( final ContentLocation contentLocation ) {
    printer.setContentLocation( contentLocation );
  }

  public NameGenerator getContentNameGenerator() {
    return printer.getContentNameGenerator();
  }

  public void setContentNameGenerator( final NameGenerator contentNameGenerator ) {
    printer.setContentNameGenerator( contentNameGenerator );
  }

}
