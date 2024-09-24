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

package org.pentaho.reporting.libraries.repository.email;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Creation-Date: 17.09.2008, 15:00:00
 *
 * @author Pedro Alves - WebDetails
 */
public class EmailContentItem implements ContentItem {
  private boolean newItem;
  private String name;
  private String contentId;
  private EmailRepository repository;
  private EmailContentLocation parent;
  private String contentType;

  public EmailContentItem( final String name,
                           final EmailRepository repository,
                           final EmailContentLocation parent ) {
    this.name = name;
    this.repository = repository;
    this.parent = parent;
    this.contentId = RepositoryUtilities.buildName( this, "/" );
    this.newItem = true;
  }

  public String getMimeType() throws ContentIOException {
    return getRepository().getMimeRegistry().getMimeType( this );
  }

  public OutputStream getOutputStream() throws ContentIOException, IOException {
    if ( newItem == false ) {
      throw new ContentIOException( "This item is no longer writeable." );
    }
    newItem = false;
    return new EmailEntryOutputStream( this );
  }

  public InputStream getInputStream() throws ContentIOException, IOException {
    throw new ContentIOException( "This item is not readable." );
  }

  public boolean isReadable() {
    return false;
  }

  public boolean isWriteable() {
    return newItem;
  }

  public String getName() {
    return name;
  }

  public Object getContentId() {
    return contentId;
  }

  public Object getAttribute( final String domain, final String key ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) &&
      LibRepositoryBoot.CONTENT_TYPE.equals( key ) ) {
      return this.contentType;
    }
    return null;
  }

  public boolean setAttribute( final String domain, final String key, final Object value ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) &&
      LibRepositoryBoot.CONTENT_TYPE.equals( key ) ) {
      this.contentType = (String) value;
      return true;
    }

    return false;
  }

  public Repository getRepository() {
    return repository;
  }

  public ContentLocation getParent() {
    return parent;
  }

  public boolean delete() {
    return false;
  }
}
