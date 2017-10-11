/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import java.awt.Color;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class PdfImageHandler {
  private static final Log logger = LogFactory.getLog( PdfImageHandler.class );
  private LFUMap<ResourceKey, Image> imageCache;
  private ResourceManager resourceManager;
  private OutputProcessorMetaData metaData;

  public PdfImageHandler( final OutputProcessorMetaData metaData, final ResourceManager resourceManager,
      final LFUMap<ResourceKey, Image> imageCache ) {
    this.metaData = metaData;
    this.resourceManager = resourceManager;
    this.imageCache = imageCache;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public Image createImage( RenderableReplacedContentBox content ) {
    try {
      final Object o = content.getContent().getRawObject();
      if ( o instanceof DrawableWrapper ) {
        final DrawableWrapper drawableWrapper = (DrawableWrapper) o;
        final StrictBounds bounds =
            new StrictBounds( content.getX(), content.getY(), content.getWidth(), content.getHeight() );
        DefaultImageReference imageContainer =
            RenderUtility.createImageFromDrawable( drawableWrapper, bounds, content.getStyleSheet(), metaData );
        return Image.getInstance( imageContainer.getImage(), Color.WHITE );
      }
      if ( o instanceof java.awt.Image ) {
        java.awt.Image img = (java.awt.Image) o;
        return Image.getInstance( img, Color.WHITE );
      }

      if ( o instanceof URLImageContainer ) {
        final URLImageContainer imageContainer = (URLImageContainer) o;
        final Image image = createImage( imageContainer );
        if ( image != null ) {
          return image;
        }
      }

      if ( o instanceof LocalImageContainer ) {
        final LocalImageContainer imageContainer = (LocalImageContainer) o;
        final java.awt.Image image = imageContainer.getImage();
        return Image.getInstance( image, Color.WHITE );
      }
      return null;
    } catch ( IOException e ) {
      logger.info( "Unable to load image. Ignoring.", e );
    } catch ( BadElementException e ) {
      logger.info( "Unable to load image. Ignoring.", e );
    }
    return null;
  }

  public Image createImage( URLImageContainer imageContainer ) {
    if ( !imageContainer.isLoadable() ) {
      logger.info( "URL-image cannot be rendered, as it was declared to be not loadable: "
          + imageContainer.getSourceURLString() );
      return null;
    }

    final ResourceKey resource = imageContainer.getResourceKey();
    if ( resource == null ) {
      logger.info( "URL-image cannot be rendered, as it did not return a valid URL." );
      return null;
    }

    try {
      final ResourceManager resourceManager = getResourceManager();
      final com.lowagie.text.Image instance;
      final com.lowagie.text.Image maybeImage = imageCache.get( resource );
      if ( maybeImage != null ) {
        instance = maybeImage;
      } else {
        final ResourceData data = resourceManager.load( resource );
        instance = com.lowagie.text.Image.getInstance( data.getResource( resourceManager ) );
        imageCache.put( resource, instance );
      }
      return instance;
    } catch ( InvalidReportStateException re ) {
      throw re;
    } catch ( Exception e ) {
      logger.info( "URL-image cannot be rendered, as the image was not loadable.", e );
    }

    return null;
  }
}
