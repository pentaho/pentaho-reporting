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


package org.pentaho.reporting.libraries.resourceloader.loader.resource;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.AbstractResourceData;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.io.InputStream;

/**
 * Creation-Date: 05.04.2006, 15:15:36
 *
 * @author Thomas Morgner
 */
public class ClassloaderResourceData extends AbstractResourceData {
  private ResourceKey key;
  private String resourcePath;
  private static final long serialVersionUID = 980009972320937886L;

  public ClassloaderResourceData( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    this.key = key;
    final String rawPath = (String) key.getIdentifier();
    this.resourcePath = rawPath.substring( 6 );
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    final InputStream stream = ObjectUtilities.getResourceAsStream
      ( resourcePath, ClassloaderResourceData.class );
    if ( stream == null ) {
      throw new ResourceLoadingException( "Resource is not available: " + resourcePath );
    }
    return stream;
  }

  public Object getAttribute( final String key ) {
    // we do not support attributes ...
    if ( key.equals( ResourceData.FILENAME ) ) {
      return LoaderUtils.getFileName( this.resourcePath );
    }
    return null;
  }

  public long getVersion( final ResourceManager caller )
    throws ResourceLoadingException {
    // We assume, that the data does never change
    // This way, we get the benefit of the cache.
    return 0;
  }

  public ResourceKey getKey() {
    return key;
  }
}
