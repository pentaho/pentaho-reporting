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


package org.pentaho.reporting.libraries.docbundle.bundlefactory;

import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.StaticDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.bundleloader.RepositoryResourceBundleData;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.resourceloader.ContentNotRecognizedException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;
import org.pentaho.reporting.libraries.resourceloader.cache.CachingResourceBundleData;

public class BundleResourceFactory implements ResourceFactory {
  public BundleResourceFactory() {
  }

  public Class getFactoryType() {
    return DocumentBundle.class;
  }

  public Resource create( final ResourceManager manager,
                          ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    if ( data instanceof CachingResourceBundleData ) {
      // todo: Friggin' cheap hack
      final CachingResourceBundleData cachingResourceBundleData = (CachingResourceBundleData) data;
      data = cachingResourceBundleData.getBackend();
    }
    if ( data instanceof RepositoryResourceBundleData == false ) {
      data = manager.loadResourceBundle( data.getKey() );
      if ( data instanceof CachingResourceBundleData ) {
        // todo: Friggin' cheap hack 2
        final CachingResourceBundleData cachingResourceBundleData = (CachingResourceBundleData) data;
        data = cachingResourceBundleData.getBackend();
      }
    }
    if ( data instanceof RepositoryResourceBundleData == false ) {
      throw new ContentNotRecognizedException( "No valid handler for the given content." );
    }

    final RepositoryResourceBundleData bdata = (RepositoryResourceBundleData) data;
    final Repository repository = bdata.getRepository();
    try {
      final StaticDocumentBundle bundle = new StaticDocumentBundle( repository, manager, bdata.getBundleKey() );
      return new SimpleResource( data.getKey(), bundle, getFactoryType(), data.getVersion( manager ) );
    } catch ( ResourceException e ) {
      throw new ResourceCreationException( "Unable to interpret document-bundle", e );
    }
  }

  public void initializeDefaults() {

  }
}
