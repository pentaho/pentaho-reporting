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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AbstractPageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfDocumentWriter;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.support.itext.BaseFontModule;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.OutputStream;

/**
 * A streaming target, which produces a PDF document.
 *
 * @author Thomas Morgner
 */
public class PdfOutputProcessor extends AbstractPageableOutputProcessor {
  private PdfOutputProcessorMetaData metaData;
  private PageFlowSelector flowSelector;
  private OutputStream outputStream;
  private PdfDocumentWriter writer;
  private ResourceManager resourceManager;

  public PdfOutputProcessor( final Configuration configuration, final OutputStream outputStream ) {
    this( configuration, outputStream, PdfOutputProcessor.createResourceManager() );
  }

  private static ResourceManager createResourceManager() {
    return new ResourceManager();
  }

  public PdfOutputProcessor( final Configuration configuration, final OutputStream outputStream,
      final ResourceManager resourceManager ) {
    if ( configuration == null ) {
      throw new NullPointerException( "Configuration must not be null" );
    }
    if ( outputStream == null ) {
      throw new NullPointerException( "OutputStream must not be null" );
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    this.resourceManager = resourceManager;
    this.outputStream = outputStream;
    this.flowSelector = new AllPageFlowSelector();

    // for the sake of simplicity, we use the AWT font registry for now.
    // This is less accurate than using the iText fonts, but completing
    // the TrueType registry or implementing an iText registry is too expensive
    // for now.
    final ITextFontStorage fontStorage = new ITextFontStorage( BaseFontModule.getFontRegistry() );

    metaData = new PdfOutputProcessorMetaData( fontStorage );

  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public PageFlowSelector getFlowSelector() {
    return flowSelector;
  }

  public void setFlowSelector( final PageFlowSelector flowSelector ) {
    if ( flowSelector == null ) {
      throw new NullPointerException();
    }

    this.flowSelector = flowSelector;
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    if ( writer != null ) {
      this.writer.close();
      this.metaData.commit();
    }
  }

  protected void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = createPdfDocumentWriter();
        writer.open();
      }
      writer.processPhysicalPage( pageGrid, logicalPage, row, col, pageKey );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate PDF document", e );
    }
  }

  protected void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = createPdfDocumentWriter();
        writer.open();
      }
      writer.processLogicalPage( key, logicalPage );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate PDF document", e );
    }
  }

  protected OutputStream getOutputStream() {
    return outputStream;
  }

  protected ResourceManager getResourceManager() {
    return resourceManager;
  }

  protected PdfDocumentWriter getWriter() {
    return writer;
  }

  protected PdfDocumentWriter createPdfDocumentWriter() {
    return new PdfDocumentWriter( (PdfOutputProcessorMetaData) getMetaData(), getOutputStream(), getResourceManager() );
  }

}
