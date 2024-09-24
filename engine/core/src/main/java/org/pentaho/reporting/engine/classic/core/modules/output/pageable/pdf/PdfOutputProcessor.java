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
