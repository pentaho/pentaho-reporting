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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xml;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlDocumentWriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlTableOutputProcessorMetaData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlTableOutputProcessor extends AbstractTableOutputProcessor implements PageableOutputProcessor {
  private List<PhysicalPageKey> physicalPages;
  private FlowSelector flowSelector;
  private OutputProcessorMetaData metaData;
  private OutputStream outputStream;
  private XmlDocumentWriter writer;

  public XmlTableOutputProcessor( final OutputStream outputStream ) {
    this( outputStream, new XmlTableOutputProcessorMetaData() );
  }

  public XmlTableOutputProcessor( final OutputStream outputStream, final OutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException( "MetaData must not be null" );
    }
    if ( outputStream == null ) {
      throw new NullPointerException( "OutputStream must not be null" );
    }

    this.outputStream = outputStream;
    this.metaData = metaData;
    this.physicalPages = new ArrayList<PhysicalPageKey>();

    this.flowSelector = new DisplayAllFlowSelector();
  }

  protected void processingPagesFinished() {
    super.processingPagesFinished();
    physicalPages = Collections.unmodifiableList( physicalPages );
  }

  public int getPhysicalPageCount() {
    return physicalPages.size();
  }

  public PhysicalPageKey getPhysicalPage( final int page ) {
    if ( isPaginationFinished() == false ) {
      throw new IllegalStateException();
    }

    return physicalPages.get( page );
  }

  protected LogicalPageKey createLogicalPage( final int width, final int height ) {
    final LogicalPageKey key = super.createLogicalPage( width, height );
    for ( int h = 0; h < key.getHeight(); h++ ) {
      for ( int w = 0; w < key.getWidth(); w++ ) {
        physicalPages.add( key.getPage( w, h ) );
      }
    }
    return key;
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    if ( writer != null ) {
      try {
        this.metaData.commit();
        writer.close();
      } catch ( IOException e ) {
        throw new InvalidReportStateException( "Failed to close writer" );
      }
    }
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = new XmlDocumentWriter( outputStream, metaData );
        writer.open();
      }
      writer.processTableContent( logicalPage, metaData, contentProducer, false );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate PDF document", e );
    }
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final boolean performOutput ) throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = new XmlDocumentWriter( outputStream, metaData );
        writer.open();
      }
      writer.processTableContent( logicalPage, metaData, contentProducer, true );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate PDF document", e );
    }
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }
}
