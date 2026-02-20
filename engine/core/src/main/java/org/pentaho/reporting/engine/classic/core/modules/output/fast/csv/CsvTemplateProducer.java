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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.TemplatingOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVTableModule;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

public class CsvTemplateProducer implements FastExportTemplateProducer {
  private OutputProcessorMetaData metaData;
  private SheetLayout sheetLayout;
  private String encoding;
  private HashMap<InstanceID, String> idMapping;
  private String template;
  private CSVQuoter quoter;

  public CsvTemplateProducer( final OutputProcessorMetaData metaData, final SheetLayout sheetLayout,
      final String encoding ) {
    this.metaData = metaData;
    this.sheetLayout = sheetLayout;
    this.encoding = encoding;
    this.idMapping = new HashMap<InstanceID, String>();

    final String separator =
        metaData.getConfiguration().getConfigProperty( CSVTableModule.SEPARATOR, CSVTableModule.SEPARATOR_DEFAULT );
    if ( separator.length() == 0 ) {
      throw new IllegalArgumentException( "CSV separate cannot be an empty string." );
    }

    final boolean forceQuoting = 
            "true".equals(metaData.getConfiguration().getConfigProperty( CSVTableModule.FORCE_QUOTING, CSVTableModule.FORCE_QUOTING_DEFAULT ).toLowerCase());

    final String quoteChar =
            metaData.getConfiguration().getConfigProperty( CSVTableModule.QUOTE_CHAR, CSVTableModule.QUOTE_CHAR_DEFAULT );
        if ( quoteChar.length() == 0 ) {
          throw new IllegalArgumentException( "CSV quote char cannot be an empty string." );
        }

            
    if ( this.encoding == null ) {
      this.encoding =
          metaData.getConfiguration().getConfigProperty(
              "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.Encoding",
              EncodingRegistry.getPlatformDefaultEncoding() );
    }

    quoter = new CSVQuoter(separator.charAt( 0 ), quoteChar.charAt(0), forceQuoting );
  }

  public void produceTemplate( final LogicalPageBox pageBox ) {
    TableContentProducer contentProducer =
        TemplatingOutputProcessor.produceTableLayout( pageBox, sheetLayout, metaData );

    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int columnCount = contentProducer.getColumnCount();
    final int lastColumn = columnCount - 1;

    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();
    StringWriter swriter = new StringWriter();
    PrintWriter writer = new PrintWriter( swriter );

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
        final TableRectangle rectangle =
            sheetLayout.getTableBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content
                .getHeight(), null );
        if ( rectangle.isOrigin( col, row ) == false ) {
          // A spanned cell ..
          /* [PRD-5412] Since it is a spanned cell with no actual content within it, we need to print a separator here
             so that we can accurately set the cells (this was previously a copy from the Excel Template producer, but
             they handle spanned cells differently.
          */
          writer.print( quoter.getSeparator() );
          continue;
        }

        InstanceID instanceId = content.getNodeLayoutProperties().getInstanceId();
        String uuid = idMapping.get( instanceId );
        if ( uuid == null ) {
          uuid = String.valueOf( idMapping.size() );
          idMapping.put( instanceId, uuid );
        }

        final String formattedtext = "$(" + uuid + ")";
        writer.write( formattedtext );
        if ( col < lastColumn ) {
          writer.print( quoter.getSeparator() );
        }
        content.setFinishedTable( true );
      }

      writer.println();
    }

    writer.close();
    template = swriter.toString();
  }

  public CSVQuoter getQuoter() {
    return quoter;
  }

  public String getTemplate() {
    return template;
  }

  public FormattedDataBuilder createDataBuilder() {
    MessageFormatSupport messageFormatSupport = new MessageFormatSupport();
    messageFormatSupport.setLocale( Locale.ENGLISH );
    messageFormatSupport.setFormatString( getTemplate() );
    messageFormatSupport.setNullString( "" );
    return new CsvFormattedDataBuilder( idMapping, messageFormatSupport, getQuoter(), encoding );
  }
}