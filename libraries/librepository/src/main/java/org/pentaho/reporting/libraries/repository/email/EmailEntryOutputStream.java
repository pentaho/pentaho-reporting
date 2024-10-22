/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.repository.email;

import jakarta.activation.DataHandler;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Creation-Date: 17.09.2008, 15:00:00
 *
 * @author Pedro Alves - WebDetails
 */
public class EmailEntryOutputStream extends OutputStream {
  private ByteArrayOutputStream outputStream;
  private boolean closed;
  private EmailContentItem item;

  public EmailEntryOutputStream( final EmailContentItem item ) {
    this.item = item;
    this.outputStream = new ByteArrayOutputStream();
  }

  public void write( final int b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b );
  }

  public void write( final byte[] b, final int off, final int len )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b, off, len );
  }

  public void close()
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }

    outputStream.close();
    final byte[] data = outputStream.toByteArray();

    final EmailRepository repository = (EmailRepository) item.getRepository();

    try {
      // if name == index.html, use this as the emailHTMLBody
      if ( repository.isTreatHtmlContentAsBody() && item.getMimeType().endsWith( "text/html" ) ) {
        final MimeBodyPart messageBodyPart = repository.getBodypart();
        final ByteArrayDataSource dataSource = new ByteArrayDataSource( data, item.getMimeType() );
        messageBodyPart.setDataHandler( new DataHandler( dataSource ) );
      } else {
        // Normal Content
        final ByteArrayInputStream bin = new ByteArrayInputStream( data );
        final String contentId = (String) item.getContentId();
        final ByteArrayDataSource dataSource = new ByteArrayDataSource( bin, item.getMimeType() );
        final MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler( new DataHandler( dataSource ) );
        messageBodyPart.setHeader( "Content-ID", contentId );
        repository.getMultipart().addBodyPart( messageBodyPart );
        bin.close();
      }
    } catch ( Exception e ) {
      throw new IOException( "Error closing stream: " + e.getMessage() );
    }
    closed = true;

  }

  public void write( final byte[] b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b );
  }

  public void flush()
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.flush();
  }
}
