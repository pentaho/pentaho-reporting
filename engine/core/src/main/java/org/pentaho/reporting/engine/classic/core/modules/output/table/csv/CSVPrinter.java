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

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Creation-Date: 09.05.2007, 14:52:05
 *
 * @author Thomas Morgner
 */
public class CSVPrinter {
  private ContentLocation contentLocation;
  private NameGenerator contentNameGenerator;
  private String encoding;
  private ContentItem documentContentItem;
  private PrintWriter writer;
  private DefaultTextExtractor textExtractor;
  private CSVQuoter quoter;

  public CSVPrinter() {
  }

  public void initialize( final Configuration config ) {
    encoding =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.Encoding",
            EncodingRegistry.getPlatformDefaultEncoding() );
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    this.encoding = encoding;
  }

  public ContentLocation getContentLocation() {
    return contentLocation;
  }

  public void setContentLocation( final ContentLocation contentLocation ) {
    if ( contentLocation == null ) {
      throw new NullPointerException();
    }

    this.contentLocation = contentLocation;
  }

  public NameGenerator getContentNameGenerator() {
    return contentNameGenerator;
  }

  public void setContentNameGenerator( final NameGenerator contentNameGenerator ) {
    if ( contentNameGenerator == null ) {
      throw new NullPointerException();
    }
    this.contentNameGenerator = contentNameGenerator;
  }

  public void print( final LogicalPageBox logicalPage, final TableContentProducer contentProducer,
      final OutputProcessorMetaData metaData, final boolean incremental ) throws ContentProcessingException {
    try {
      if ( textExtractor == null ) {
        textExtractor = new DefaultTextExtractor( metaData );

        final String separator =
            metaData.getConfiguration().getConfigProperty( CSVTableModule.SEPARATOR, CSVTableModule.SEPARATOR_DEFAULT );
        if ( separator.length() == 0 ) {
          throw new IllegalArgumentException( "CSV separate cannot be an empty string." );
        }

        quoter = new CSVQuoter( separator.charAt( 0 ) );
      }

      if ( documentContentItem == null ) {
        if ( contentLocation == null ) {
          throw new IllegalStateException();
        }
        if ( contentNameGenerator == null ) {
          throw new IllegalStateException();
        }

        documentContentItem = contentLocation.createItem( contentNameGenerator.generateName( "content", "text/csv" ) );
        final OutputStream out = documentContentItem.getOutputStream();

        final String encoding = metaData.getConfiguration().getConfigProperty( CSVTableModule.ENCODING, this.encoding );

        writer = new PrintWriter( new OutputStreamWriter( out, encoding ) );
      }

      final SheetLayout sheetLayout = contentProducer.getSheetLayout();
      final int columnCount = contentProducer.getColumnCount();
      final int lastColumn = columnCount - 1;

      final int startRow = contentProducer.getFinishedRows();
      final int finishRow = contentProducer.getFilledRows();

      for ( int row = startRow; row < finishRow; row++ ) {
        for ( short col = 0; col < columnCount; col++ ) {
          final RenderBox content = contentProducer.getContent( row, col );
          if ( content == null ) {
            writer.print( quoter.getSeparator() );
            continue;
          }

          if ( content.isCommited() == false ) {
            throw new InvalidReportStateException( "Uncommited content encountered" );
          }

          final long contentOffset = contentProducer.getContentOffset( row, col );
          final long colPos = sheetLayout.getXPosition( col );
          final long rowPos = sheetLayout.getYPosition( row );
          if ( content.getX() != colPos || ( content.getY() + contentOffset ) != rowPos ) {
            // A spanned cell ..
            writer.print( quoter.getSeparator() );
            continue;
          }

          textExtractor.compute( content );
          final String formattedtext = textExtractor.getFormattedtext();
          quoter.doQuoting( formattedtext, writer );
          if ( col < lastColumn ) {
            writer.print( quoter.getSeparator() );
          }
          content.setFinishedTable( true );
        }

        writer.println();

      }
      if ( incremental == false ) {
        // cleanup ..
        writer.flush();
        writer.close();

        writer = null;
        documentContentItem = null;
      }
    } catch ( IOException e ) {
      writer = null;
      documentContentItem = null;

      throw new ContentProcessingException( "Failed to write content", e );
    } catch ( ContentCreationException e ) {
      writer = null;
      documentContentItem = null;

      throw new ContentProcessingException( "Failed to write content", e );
    } catch ( ContentIOException e ) {
      writer = null;
      documentContentItem = null;

      throw new ContentProcessingException( "Failed to write content", e );
    }

  }

  public void close() {

  }
}
