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
