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


package org.pentaho.reporting.libraries.resourceloader.modules.factory.wmf;

import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creation-Date: 05.04.2006, 17:58:42
 *
 * @author Thomas Morgner
 */
public class WMFImageFactoryModule extends AbstractWMFFactoryModule {
  public WMFImageFactoryModule() {
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceLoadingException {
    try {
      final long version = data.getVersion( caller );
      final InputStream stream = data.getResourceAsStream( caller );
      try {
        final WmfFile wmfFile = new WmfFile( stream, -1, -1 );
        final Image image = wmfFile.replay();
        return new SimpleResource( data.getKey(), image, Image.class, version );
      } finally {
        stream.close();
      }
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to process WMF file", e );
    }
  }

}
