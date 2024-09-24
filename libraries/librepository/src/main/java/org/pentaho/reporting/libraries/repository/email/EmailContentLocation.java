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

import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.util.HashMap;


/**
 * Creation-Date: 17.09.2008, 15:00:00
 *
 * @author Pedro Alves - WebDetails
 */
public class EmailContentLocation implements ContentLocation {

  private HashMap entries;
  private String name;
  private String contentId;
  private ContentLocation parent;
  private EmailRepository repository;

  public EmailContentLocation( final EmailRepository repository,
                               final ContentLocation parent,
                               final String name ) {
    this.repository = repository;
    this.parent = parent;
    this.name = name;
    this.entries = new HashMap();
    this.contentId = RepositoryUtilities.buildName( this, "/" ) + '/';
  }

  public ContentEntity[] listContents() throws ContentIOException {
    return (ContentEntity[]) entries.values().toArray( new ContentEntity[ entries.size() ] );
  }

  public ContentEntity getEntry( final String name ) throws ContentIOException {
    return (ContentEntity) entries.get( name );
  }

  /**
   * Creates a new data item in the current location. This method must never return null.
   *
   * @param name
   * @return
   * @throws ContentCreationException if the item could not be created.
   */
  public ContentItem createItem( final String name ) throws ContentCreationException {
    if ( entries.containsKey( name ) ) {
      throw new ContentCreationException( "Entry already exists" );
    }

    final EmailContentItem item = new EmailContentItem( name, repository, this );
    entries.put( name, item );
    return item;
  }

  public ContentLocation createLocation( final String name )
    throws ContentCreationException {

    throw new ContentCreationException( "createLocation not Implemented yet" );
  }

  public boolean exists( final String name ) {
    return entries.containsKey( name );
  }

  public String getName() {
    return name;
  }

  public Object getContentId() {
    return contentId;
  }

  public Object getAttribute( final String domain, final String key ) {
    return null;
  }

  public boolean setAttribute( final String domain, final String key, final Object value ) {
    return false;
  }

  public ContentLocation getParent() {
    return parent;
  }

  public Repository getRepository() {
    return repository;
  }

  public boolean delete() {
    return false;
  }
}
