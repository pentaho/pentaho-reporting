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

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Creation-Date: 17.09.2008, 15:00:00
 *
 * @author Pedro Alves - WebDetails
 */
public class EmailRepository implements Repository {

  private MimeRegistry mimeRegistry;
  private EmailContentLocation root;
  private MimeMessage htmlEmail;
  private boolean treatHtmlContentAsBody;
  private MimeMultipart multipart;
  private MimeBodyPart bodypart;

  public EmailRepository( final MimeRegistry mimeRegistry,
                          final Session mailSession ) throws ContentIOException, MessagingException {
    if ( mimeRegistry == null ) {
      throw new NullPointerException();
    }

    this.mimeRegistry = mimeRegistry;
    this.root = new EmailContentLocation( this, null, "" );
    this.htmlEmail = new MimeMessage( mailSession );
    this.bodypart = new MimeBodyPart();
    this.multipart = new MimeMultipart( "related" );
    this.multipart.addBodyPart( bodypart );
    this.htmlEmail.setContent( multipart );
    this.treatHtmlContentAsBody = true;
  }

  public EmailRepository( final MimeMessage htmlEmail, final MimeRegistry mimeRegistry )
    throws ContentIOException, IOException, MessagingException {
    if ( htmlEmail == null ) {
      throw new NullPointerException();
    }
    if ( mimeRegistry == null ) {
      throw new NullPointerException();
    }
    this.htmlEmail = htmlEmail;

    final Object content = this.htmlEmail.getContent();
    if ( content instanceof MimeMultipart == false ) {
      this.multipart = new MimeMultipart( "related" );
    } else {
      this.multipart = (MimeMultipart) content;
    }
    this.treatHtmlContentAsBody = true;
    this.mimeRegistry = mimeRegistry;
    this.root = new EmailContentLocation( this, null, "" );

  }

  public EmailRepository( final MimeMessage htmlEmail )
    throws ContentIOException, IOException, MessagingException {
    this( htmlEmail, new DefaultMimeRegistry() );
  }

  public EmailRepository( final Session session ) throws ContentIOException, MessagingException {
    this( new DefaultMimeRegistry(), session );
  }

  public ContentLocation getRoot() throws ContentIOException {
    return root;
  }

  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }

  public void writeEmail( final OutputStream out ) throws ContentIOException {
    try {
      htmlEmail.writeTo( out );
    } catch ( Exception ex ) {
      throw new ContentIOException( ex.getMessage(), ex );
    }

  }

  public MimeMessage getEmail() {
    return htmlEmail;
  }

  public MimeMultipart getMultipart() {
    return multipart;
  }

  public boolean isTreatHtmlContentAsBody() {
    return treatHtmlContentAsBody;
  }

  public void setTreatHtmlContentAsBody( final boolean treatHtmlContentAsBody ) {
    this.treatHtmlContentAsBody = treatHtmlContentAsBody;
  }

  public MimeBodyPart getBodypart() {
    return bodypart;
  }
}
