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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
