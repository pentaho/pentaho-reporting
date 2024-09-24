/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.repository.email;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
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
