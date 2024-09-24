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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AbstractPageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PrinterDriver;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * Creation-Date: 13.05.2007, 13:06:24
 *
 * @author Thomas Morgner
 */
public class PageableTextOutputProcessor extends AbstractPageableOutputProcessor {
  private TextDocumentWriter writer;
  private OutputProcessorMetaData metaData;
  private PrinterDriver driver;
  private PageFlowSelector flowSelector;
  private String encoding;

  public PageableTextOutputProcessor( final PrinterDriver driver, final Configuration configuration ) {
    if ( driver == null ) {
      throw new NullPointerException();
    }
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    this.driver = driver;
    this.metaData = new TextOutputProcessorMetaData( driver.getLinesPerInch(), driver.getCharactersPerInch() );
    this.flowSelector = new AllPageFlowSelector();
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding( final String encoding ) {
    this.encoding = encoding;
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
      metaData.commit();
      writer.close();
    }
  }

  protected void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) throws ContentProcessingException {
    try {
      if ( writer == null ) {
        if ( encoding == null ) {
          final String encoding =
              metaData.getConfiguration().getConfigProperty(
                  "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.Encoding",
                  EncodingRegistry.getPlatformDefaultEncoding() );

          writer = new TextDocumentWriter( metaData, driver, encoding );
        } else {
          writer = new TextDocumentWriter( metaData, driver, this.encoding );
        }

        writer.open();

        final byte[] sequence = PlainTextReportUtil.getInitSequence( metaData.getConfiguration() );
        if ( sequence != null ) {
          driver.printRaw( sequence );
        }

      }
      writer.processPhysicalPage( pageGrid, logicalPage, row, col, pageKey );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate the PlainText document", e );
    }
  }

  protected void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    try {
      if ( writer == null ) {
        if ( encoding == null ) {
          final String encoding =
              metaData.getConfiguration().getConfigProperty(
                  "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.Encoding",
                  EncodingRegistry.getPlatformDefaultEncoding() );

          writer = new TextDocumentWriter( metaData, driver, encoding );
        } else {
          writer = new TextDocumentWriter( metaData, driver, this.encoding );
        }
        writer.open();

        final byte[] sequence = PlainTextReportUtil.getInitSequence( metaData.getConfiguration() );
        if ( sequence != null ) {
          driver.printRaw( sequence );
        }
      }
      writer.processLogicalPage( key, logicalPage );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate the PlainText document", e );
    }
  }
}
