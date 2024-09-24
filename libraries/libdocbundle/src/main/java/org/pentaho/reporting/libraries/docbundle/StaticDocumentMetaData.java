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

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.docbundle.metadata.BundleManifest;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleMetaData;
import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleMetaData;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A static (read-only) implementation of the document-metadata interface.
 *
 * @author Thomas Morgner
 */
public class StaticDocumentMetaData implements DocumentMetaData {
  private String bundleType;
  private BundleMetaData metaData;
  private BundleManifest manifest;
  private ResourceManager resourceManager;
  private ResourceKey bundleKey;

  public StaticDocumentMetaData( final ResourceManager resourceManager,
                                 final ResourceKey bundleKey ) throws ResourceException {
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    if ( bundleKey == null ) {
      throw new NullPointerException();
    }


    this.resourceManager = resourceManager;
    this.bundleKey = bundleKey;

    // A bundle without a manifest is not valid.
    final ResourceKey manifestDataKey = resourceManager.deriveKey( bundleKey, "/META-INF/manifest.xml" );
    final Resource manifestDataResource = resourceManager.create( manifestDataKey, null, BundleManifest.class );
    manifest = (BundleManifest) manifestDataResource.getResource();

    metaData = createMetaData( resourceManager, bundleKey );

    bundleType = readBundleType();
    if ( bundleType == null ) {
      bundleType = manifest.getMimeType( "/" );
    }
    // bundle type can still be null.
  }

  private BundleMetaData createMetaData( final ResourceManager resourceManager, final ResourceKey bundleKey )
    throws ResourceException {
    try {
      final ResourceKey metaDataKey = resourceManager.deriveKey( bundleKey, "/meta.xml" );
      // make sure we trigger a resource-loading exception ..
      final ResourceData metaDataBytes = resourceManager.load( metaDataKey );

      final Resource metaDataResource = resourceManager.create( metaDataKey, null, BundleMetaData.class );
      return (BundleMetaData) metaDataResource.getResource();
    } catch ( ResourceKeyCreationException e ) {
      return new DefaultBundleMetaData();
    } catch ( ResourceLoadingException e ) {
      return new DefaultBundleMetaData();
    }
  }

  private String readBundleType() {
    try {
      final ResourceKey mimeKey = this.resourceManager.deriveKey( bundleKey, "mimetype" );
      final ResourceData mimeData = this.resourceManager.load( mimeKey );
      final byte[] data = mimeData.getResource( this.resourceManager );
      return new String( data, "ASCII" );
    } catch ( Exception cioe ) {
      return null;
    }
  }

  public String getBundleType() {
    return bundleType;
  }

  public String getEntryMimeType( final String entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }

    return manifest.getMimeType( entry );
  }

  public Object getBundleAttribute( final String namespace, final String attributeName ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( attributeName == null ) {
      throw new NullPointerException();
    }


    return metaData.getBundleAttribute( namespace, attributeName );
  }

  public String[] getManifestEntryNames() {
    return manifest.getEntries();
  }

  public String[] getMetaDataNamespaces() {
    return metaData.getNamespaces();
  }

  public String[] getMetaDataNames( final String namespace ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }

    return metaData.getNames( namespace );
  }

  public String getEntryAttribute( final String entryName, final String attributeName ) {
    return manifest.getAttribute( entryName, attributeName );
  }

  public String[] getEntryAttributeNames( final String entryName ) {
    return manifest.getAttributeNames( entryName );
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
