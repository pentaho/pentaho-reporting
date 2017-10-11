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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class StaticDocumentBundle implements DocumentBundle {
  private static final Log logger = LogFactory.getLog( StaticDocumentBundle.class );

  private StaticDocumentMetaData documentMetaData;
  private Repository repository;
  private ResourceManager resourceManager;
  private ResourceKey bundleKey;

  public StaticDocumentBundle( final Repository repository,
                               final ResourceManager resourceManager,
                               final ResourceKey outsideContextKey )
    throws ResourceException {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }


    this.repository = repository;
    final BundleResourceManagerBackend backend =
      new BundleResourceManagerBackend( repository, resourceManager.getBackend(), outsideContextKey );
    this.bundleKey = backend.getBundleMainKey();
    this.resourceManager = new ResourceManager( resourceManager, backend );

    this.documentMetaData = new StaticDocumentMetaData( this.resourceManager, this.bundleKey );
  }

  public StaticDocumentBundle( final Repository repository ) throws ResourceException {
    this( repository, createDefaultResourceManager(), null );
  }

  private static ResourceManager createDefaultResourceManager() {
    return new ResourceManager();
  }

  public DocumentMetaData getMetaData() {
    return documentMetaData;
  }

  public boolean isEntryExists( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }


    final String[] splitName = RepositoryUtilities.split( name, "/" );
    try {
      return RepositoryUtilities.isExistsEntity( repository, splitName );
    } catch ( ContentIOException e ) {
      return false;
    }
  }

  public boolean isEntryReadable( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }


    try {
      final String[] splitName = RepositoryUtilities.split( name, "/" );
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( repository, splitName );
      return ( contentEntity instanceof ContentItem );
    } catch ( ContentIOException cioe ) {
      return false;
    }
  }

  public InputStream getEntryAsStream( final String name ) throws IOException {
    if ( name == null ) {
      throw new NullPointerException();
    }

    try {
      final String[] splitName = RepositoryUtilities.split( name, "/" );
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( repository, splitName );
      if ( contentEntity instanceof ContentItem ) {
        final ContentItem contentItem = (ContentItem) contentEntity;
        return contentItem.getInputStream();
      }
    } catch ( ContentIOException cioe ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to lookup entry for entry " + name, cioe );
      }

      throw new IOException( "Failure while looking up the stream: " + cioe );
    }
    throw new IOException( "No such stream: " + name );
  }

  public String getEntryMimeType( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    final String definedMimeType = documentMetaData.getEntryMimeType( name );
    if ( definedMimeType != null ) {
      return definedMimeType;
    }

    try {
      final String[] splitName = RepositoryUtilities.split( name, "/" );
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( repository, splitName );
      if ( contentEntity instanceof ContentItem ) {
        final ContentItem contentItem = (ContentItem) contentEntity;
        return contentItem.getMimeType();
      }
    } catch ( ContentIOException cioe ) {
      // ignored.
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to lookup entry mime-type for entry " + name, cioe );
      }
    }

    return "";
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public ResourceKey getBundleKey() {
    return bundleKey;
  }
}
