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
