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


package org.pentaho.reporting.libraries.resourceloader.modules.factory.imageio;

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;
import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageIOFactoryModule extends AbstractFactoryModule {
  private static final int[] EMPTY_FINGERPRINT = new int[ 0 ];
  private static final String[] EMPTY_FILETYPE = new String[ 0 ];

  public ImageIOFactoryModule() {
  }

  private Image createImage( final byte[] imageData )
    throws IOException {
    return ImageIO.read( new ByteArrayInputStream( imageData ) );
  }


  public int canHandleResource( final ResourceManager caller, final ResourceData data )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final byte[] resource = data.getResource( caller );
      if ( createImage( resource ) != null ) {
        return RECOGNIZED_CONTENTTYPE;
      }
    } catch ( IOException e ) {
      // ignore me ..
    }
    return REJECTED;
  }


  public Resource create( final ResourceManager caller, final ResourceData data, final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    final long version = data.getVersion( caller );
    final byte[] resource = data.getResource( caller );
    final Image image;
    try {
      image = createImage( resource );
    } catch ( IOException e ) {
      throw new ResourceCreationException( "Failed to load the image.", e );
    }

    if ( image == null ) {
      throw new ResourceCreationException( "Failed to load the image. ImageIO#read returned null" );
    }

    return new SimpleResource( data.getKey(), image, Image.class, version );
  }

  protected String[] getMimeTypes() {
    return EMPTY_FILETYPE;
  }

  protected String[] getFileExtensions() {
    return EMPTY_FILETYPE;
  }

  protected int[] getFingerPrint() {
    return EMPTY_FINGERPRINT;
  }

  public int getHeaderFingerprintSize() {
    return 0;
  }
}
