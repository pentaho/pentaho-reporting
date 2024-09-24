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
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

import java.io.OutputStream;

/**
 * Creation-Date: 09.05.2007, 14:36:28
 *
 * @author Thomas Morgner
 */
public class StreamCSVOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private CSVPrinter printer;

  public StreamCSVOutputProcessor( final OutputStream outputStream ) {
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    this.metaData = new CSVOutputProcessorMetaData( CSVOutputProcessorMetaData.PAGINATION_NONE );
    this.flowSelector = new DisplayAllFlowSelector();

    this.printer = new CSVPrinter();

    final ContentLocation root = new StreamRepository( outputStream ).getRoot();
    this.printer.setContentLocation( root );
    this.printer.setContentNameGenerator( new DefaultNameGenerator( root ) );
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
    super.processingStarted( report, processingContext );
    this.printer.initialize( processingContext.getConfiguration() );
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected FlowSelector getFlowSelector() {
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

  public String getEncoding() {
    return printer.getEncoding();
  }

  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }

    printer.setEncoding( encoding );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }
    this.metaData.commit();
    this.printer.close();
  }
}
