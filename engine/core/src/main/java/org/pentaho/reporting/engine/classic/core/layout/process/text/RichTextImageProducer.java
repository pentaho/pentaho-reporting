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

package org.pentaho.reporting.engine.classic.core.layout.process.text;

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ReplacedContentUtil;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class RichTextImageProducer {
  private static final Log logger = LogFactory.getLog( RichTextImageProducer.class );

  private OutputProcessorMetaData metaData;
  private ResourceManager resourceManager;

  public RichTextImageProducer( final OutputProcessorMetaData metaData, final ResourceManager resourceManager ) {
    this.metaData = metaData;
    this.resourceManager = resourceManager;
  }

  public Image createImagePlaceholder( final RenderableReplacedContentBox content ) {
    final long bcw = ProcessUtility.computeBlockContextWidth( content );
    final long width = ReplacedContentUtil.computeWidth( content );
    final long height = ReplacedContentUtil.computeHeight( content, bcw, width );

    final int w = (int) Math.max( 1, StrictGeomUtility.toExternalValue( width ) );
    final int h = (int) Math.max( 1, StrictGeomUtility.toExternalValue( height ) );

    if ( metaData.isFeatureSupported( OutputProcessorFeature.DIRECT_RICHTEXT_RENDERING ) ) {
      final Image img =
          processRenderableReplacedContent( content.getStyleSheet(), width, height, content.getContent().getRawObject() );
      if ( img != null ) {
        return img;
      }
      // preserve layout as much as possible.
      return new BufferedImage( 1, 1, BufferedImage.TYPE_3BYTE_BGR ).getScaledInstance( w, h, Image.SCALE_FAST );
    }

    return new BufferedImage( 1, 1, BufferedImage.TYPE_3BYTE_BGR ).getScaledInstance( w, h, Image.SCALE_FAST );
  }

  private Image processRenderableReplacedContent( final StyleSheet styleSheet, final long width, final long height,
      final Object rawObject ) {
    // Fallback: (At the moment, we only support drawables and images.)
    if ( rawObject instanceof LocalImageContainer ) {
      LocalImageContainer li = (LocalImageContainer) rawObject;
      return li.getImage();
    }
    if ( rawObject instanceof URLImageContainer ) {
      final URLImageContainer imageContainer = (URLImageContainer) rawObject;
      if ( imageContainer.isLoadable() == false ) {
        logger.info( "URL-image cannot be rendered, as it was declared to be not loadable." );
        return null;
      }

      final ResourceKey sourceURL = imageContainer.getResourceKey();
      if ( sourceURL == null ) {
        logger.info( "URL-image cannot be rendered, as it did not return a valid URL." );
      }

      try {
        final Resource resource = resourceManager.create( sourceURL, null, Image.class );
        return (Image) resource.getResource();
      } catch ( ResourceException e ) {
        logger.info( "URL-image cannot be rendered, as the image was not loadable.", e );
      }
    }

    if ( rawObject instanceof DrawableWrapper ) {
      final DrawableWrapper drawable = (DrawableWrapper) rawObject;
      // render it into an Buffered image and make it a PNG file.
      final StrictBounds cb = new StrictBounds( 0, 0, width, height );
      final DefaultImageReference image = RenderUtility.createImageFromDrawable( drawable, cb, styleSheet, metaData );
      if ( image == null ) {
        // xmlWriter.writeComment("Drawable content [No image generated]:" + source);
        return null;
      }

      return image.getImage();
    }
    return null;
  }
}
