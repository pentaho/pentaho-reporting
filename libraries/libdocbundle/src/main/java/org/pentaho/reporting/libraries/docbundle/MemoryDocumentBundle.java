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
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zip.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * A document bundle implementation that holds all entries in memory.
 *
 * @author Thomas Morgner
 */
public class MemoryDocumentBundle implements WriteableDocumentBundle {
  private static final Log logger = LogFactory.getLog( MemoryDocumentBundle.class );
  private ZipRepository zipRepository;
  private MemoryDocumentMetaData metaData;
  private ResourceKey bundleKey;
  private ResourceManager resourceManager;

  public MemoryDocumentBundle() {
    this( null );
  }

  public MemoryDocumentBundle( final ResourceKey parent ) {
    this.zipRepository = new ZipRepository();
    this.metaData = new MemoryDocumentMetaData();

    final ResourceManager defaultResourceManager = new ResourceManager();
    final BundleResourceManagerBackend backend =
      new BundleResourceManagerBackend( zipRepository, defaultResourceManager.getBackend(), parent );
    this.bundleKey = backend.getBundleMainKey();
    this.resourceManager = new ResourceManager( defaultResourceManager, backend );
  }

  public ResourceKey getBundleMainKey() {
    return bundleKey;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public OutputStream createEntry( final String path, final String mimetype ) throws IOException {
    if ( path == null ) {
      throw new NullPointerException();
    }
    if ( mimetype == null ) {
      throw new NullPointerException( "Invalid Bundle: There is no mime-type for entry " + path );
    }

    final String[] name = RepositoryUtilities.splitPath( path, "/" );
    try {
      final ContentItem contentItem = RepositoryUtilities.createItem( zipRepository, name );
      metaData.setEntryMimeType( path, mimetype );
      return contentItem.getOutputStream();
    } catch ( ContentIOException cioe ) {
      logger.warn( "Failed to create content item " + path, cioe );
      throw new IOException( "Failed to create content item " + path );
    }
  }

  public WriteableDocumentMetaData getWriteableDocumentMetaData() {
    return metaData;
  }

  public DocumentMetaData getMetaData() {
    return metaData;
  }

  public void createDirectoryEntry( final String name, final String mimeType ) throws IOException {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( mimeType == null ) {
      throw new NullPointerException();
    }
    try {
      RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( name, "/" ) );
      if ( ( name.length() > 0 && name.charAt( name.length() - 1 ) == '/' ) == false ) {
        metaData.setEntryMimeType( name + '/', mimeType );
      } else {
        metaData.setEntryMimeType( name, mimeType );
      }
    } catch ( ContentIOException e ) {
      throw new IOException( "Failed to create content-location " + name );
    }
  }

  public boolean isEntryExists( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    final String[] splitName = RepositoryUtilities.split( name, "/" );
    try {
      return RepositoryUtilities.isExistsEntity( zipRepository, splitName );
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
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( zipRepository, splitName );
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
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( zipRepository, splitName );
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

    final String definedMimeType = metaData.getEntryMimeType( name );
    if ( definedMimeType != null ) {
      return definedMimeType;
    }

    try {
      final String[] splitName = RepositoryUtilities.split( name, "/" );
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( zipRepository, splitName );
      if ( contentEntity instanceof ContentItem ) {
        final ContentItem contentItem = (ContentItem) contentEntity;
        return contentItem.getMimeType();
      }
      return ""; // for directories ..
    } catch ( ContentIOException cioe ) {
      // ignored.
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to lookup entry mime-type for entry " + name, cioe );
      }
      return null;
    }

  }

  public boolean removeEntry( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    try {
      final String[] splitName = RepositoryUtilities.split( name, "/" );
      final ContentEntity contentEntity = RepositoryUtilities.getEntity( zipRepository, splitName );
      if ( contentEntity == null ) {
        return false;
      }
      if ( contentEntity instanceof ContentItem ) {
        if ( contentEntity.delete() ) {
          metaData.removeEntry( name );
          return true;
        }
      } else if ( contentEntity.delete() ) {
        // its a directory, so removing is a bit more complicated.
        final String[] entryNames = metaData.getManifestEntryNames();
        for ( int i = 0; i < entryNames.length; i++ ) {
          final String entryName = entryNames[ i ];
          if ( entryName.startsWith( name ) ) {
            metaData.removeEntry( entryName );
          }
        }
        return true;
      }
    } catch ( ContentIOException cioe ) {
      // ignored.
      return false;
    }
    return false;

  }

  public ResourceKey createResourceKey( final String entryName,
                                        final Map factoryParameters ) throws ResourceKeyCreationException {
    if ( entryName == null ) {
      throw new NullPointerException();
    }

    final ResourceKey bundleKey = getBundleMainKey().getParent();
    final ResourceBundleLoader o = (ResourceBundleLoader)
      bundleKey.getFactoryParameters().get( new FactoryParameterKey( "repository-loader" ) );
    if ( o == null ) {
      throw new ResourceKeyCreationException( "Unable to create a inner-bundle key, no loader available." );
    }
    return o.deriveKey( getBundleMainKey(), entryName, factoryParameters );
  }

  public ResourceKey getBundleKey() {
    return getBundleMainKey();
  }

  public boolean isEmbeddedKey( final ResourceKey resourceKey ) {
    return ( resourceKey != null && getBundleMainKey().getParent().equals( resourceKey.getParent() ) );
  }
}
