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

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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
