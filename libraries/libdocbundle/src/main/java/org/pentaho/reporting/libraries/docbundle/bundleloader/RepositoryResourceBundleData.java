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

package org.pentaho.reporting.libraries.docbundle.bundleloader;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.docbundle.BundleResourceManagerBackend;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.UnrecognizedLoaderException;
import org.pentaho.reporting.libraries.resourceloader.loader.AbstractResourceData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * A resource-bundle data implementation that uses a LibRepository repository as backend.
 *
 * @author Thomas Morgner
 */
public class RepositoryResourceBundleData extends AbstractResourceData implements ResourceBundleData {
  private ResourceKey bundleKey;
  private Repository repository;
  private ResourceKey mainKey;
  private ContentLocation root;
  private ContentItem contentItem;
  private static final long serialVersionUID = -1661440107629389952L;

  /**
   * Creates a new RepositoryResourceBundleData object.
   *
   * @param bundleKey         points to the physical location of the document bundle.
   * @param repository        the repository object loaded from the bundle-key.
   * @param mainKey           the bundles main entry.
   * @param failOnMissingData a flag indicating whether to fail on missing entries.
   * @throws ResourceLoadingException
   */
  public RepositoryResourceBundleData( final ResourceKey bundleKey,
                                       final Repository repository,
                                       final ResourceKey mainKey,
                                       final boolean failOnMissingData ) throws ResourceLoadingException {
    if ( bundleKey == null ) {
      throw new NullPointerException();
    }
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( mainKey == null ) {
      throw new NullPointerException();
    }
    try {
      this.bundleKey = bundleKey;
      this.repository = repository;
      this.mainKey = mainKey;
      this.root = repository.getRoot();
      final String identifier = (String) mainKey.getIdentifier();
      final String[] name = RepositoryUtilities.split( identifier, "/" );
      if ( RepositoryUtilities.isExistsEntity( repository, name ) == false ) {
        if ( failOnMissingData ) {
          throw new UnrecognizedLoaderException( "This bundle data does not point to readable content: " + identifier );
        } else {
          this.contentItem = null;
        }
      } else {
        final ContentEntity contentEntity = RepositoryUtilities.getEntity( repository, name );
        if ( contentEntity instanceof ContentItem == false ) {
          if ( failOnMissingData ) {
            throw new UnrecognizedLoaderException(
              "This bundle data does not point to readable content. Content entity is not a ContentItem" );
          } else {
            this.contentItem = null;
          }
        } else {
          this.contentItem = (ContentItem) contentEntity;
        }
      }
    } catch ( ContentIOException e ) {
      throw new ResourceLoadingException( "Failed to create Bundle-Data", e );
    }
  }

  public Repository getRepository() {
    return repository;
  }

  public ResourceBundleData deriveData( final ResourceKey key ) throws ResourceLoadingException {
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( ObjectUtilities.equal( key.getParent(), bundleKey ) == false ) {
      throw new IllegalArgumentException( "This key is no derivate of the current bundle." );
    }
    return new RepositoryResourceBundleData( bundleKey, repository, key, true );
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }

    if ( contentItem == null ) {
      throw new ResourceLoadingException( "Failure: Missing data" );
    }

    try {
      return contentItem.getInputStream();
    } catch ( ContentIOException cioe ) {
      throw new ResourceLoadingException( "Failure", cioe );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failure", e );
    }
  }

  public ResourceKey getBundleKey() {
    return bundleKey;
  }

  public Object getAttribute( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( contentItem == null ) {
      return null;
    }

    if ( ResourceData.CONTENT_TYPE.equals( key ) ) {
      try {
        return contentItem.getMimeType();
      } catch ( ContentIOException e ) {
        return null;
      }
    } else if ( ResourceData.CONTENT_LENGTH.equals( key ) ) {
      return contentItem.getAttribute( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.SIZE_ATTRIBUTE );
    } else if ( ResourceData.FILENAME.equals( key ) ) {
      return contentItem.getName();
    }
    return null;
  }

  public ResourceKey getKey() {
    return mainKey;
  }

  public long getVersion( final ResourceManager caller ) throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }

    final ContentEntity entity;
    if ( contentItem != null ) {
      entity = contentItem;
    } else {
      entity = root;
    }

    final Object attribute =
      entity.getAttribute( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.VERSION_ATTRIBUTE );
    if ( attribute instanceof Number ) {
      final Number n = (Number) attribute;
      return n.longValue();
    } else if ( attribute instanceof Date ) {
      final Date d = (Date) attribute;
      return d.getTime();
    }
    // No version information available ..
    return -1;
  }

  public ResourceManager deriveManager( final ResourceManager parent ) throws ResourceLoadingException {
    return new ResourceManager( parent,
      new BundleResourceManagerBackend( repository, parent.getBackend(), bundleKey ) );
  }
}
