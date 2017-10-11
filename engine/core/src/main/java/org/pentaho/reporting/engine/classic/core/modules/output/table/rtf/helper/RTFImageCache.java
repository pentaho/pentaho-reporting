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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;

public class RTFImageCache {
  private LFUMap cachedImages;
  private static final Log logger = LogFactory.getLog( RTFImageCache.class );
  private ResourceManager resourceManager;

  public RTFImageCache( final ResourceManager resourceManager ) {
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.resourceManager = resourceManager;
    this.cachedImages = new LFUMap( 100 );
  }

  private boolean isSupportedFormat( final URL sourceURL ) {
    final String file = sourceURL.getFile();
    if ( StringUtils.endsWithIgnoreCase( file, ".png" ) ) {
      return true;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".jpg" ) || StringUtils.endsWithIgnoreCase( file, ".jpeg" ) ) {
      return true;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".bmp" ) || StringUtils.endsWithIgnoreCase( file, ".ico" ) ) {
      return true;
    }
    return false;
  }

  /**
   * Helperfunction to extract an image from an imagereference. If the image is contained as java.awt.Image object only,
   * the image is recoded into an PNG-Image.
   *
   * @param reference
   *          the image reference.
   * @return an image.
   * @throws com.lowagie.text.DocumentException
   *           if no PDFImageElement could be created using the given ImageReference.
   * @throws java.io.IOException
   *           if the image could not be read.
   */
  public Image getImage( final ImageContainer reference ) throws DocumentException, IOException {
    if ( reference == null ) {
      throw new NullPointerException();
    }

    Object identity = null;
    java.awt.Image image = null;
    if ( reference instanceof URLImageContainer ) {
      final URLImageContainer urlImageContainer = (URLImageContainer) reference;
      final ResourceKey url = urlImageContainer.getResourceKey();
      if ( url != null && urlImageContainer.isLoadable() ) {
        identity = url;
        final Image cached = (Image) cachedImages.get( identity );
        if ( cached != null ) {
          return cached;
        }

        try {
          final ResourceData resourceData = resourceManager.load( url );
          final byte[] data = resourceData.getResource( resourceManager );
          final Image itextimage = Image.getInstance( data );
          cachedImages.put( identity, itextimage );
          return itextimage;
        } catch ( ResourceException re ) {
          RTFImageCache.logger.info( "Caught illegal Image, will recode to PNG instead", re );
        } catch ( BadElementException be ) {
          RTFImageCache.logger.info( "Caught illegal Image, will recode to PNG instead", be );
        } catch ( IOException ioe ) {
          RTFImageCache.logger.info( "Unable to read the raw-data, will try to recode image-data.", ioe );
        }

        try {
          final Resource resource = resourceManager.create( url, null, Image.class );
          image = (java.awt.Image) resource.getResource();
        } catch ( ResourceException re ) {
          RTFImageCache.logger.info( "Caught illegal Image, will try to find local instance", re );
        }
      }
    }

    if ( reference instanceof LocalImageContainer && image == null ) {
      final LocalImageContainer localImageContainer = (LocalImageContainer) reference;
      image = localImageContainer.getImage();
      if ( image != null ) {
        // check, if the content was cached ...
        identity = localImageContainer.getIdentity();
        if ( identity != null ) {
          final Image cachedImage = (Image) cachedImages.get( identity );
          if ( cachedImage != null ) {
            return cachedImage;
          }
        }

      }
    }
    if ( image == null ) {
      return null;
    }

    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();

    try {
      final byte[] data = RenderUtility.encodeImage( image );
      final Image itextimage = Image.getInstance( data );
      if ( identity != null ) {
        cachedImages.put( identity, itextimage );
      }
      return itextimage;
    } catch ( UnsupportedEncoderException uee ) {
      logger.warn( "Assertation-Failure: PNG encoding failed.", uee );
      return null;
    }

  }

}
