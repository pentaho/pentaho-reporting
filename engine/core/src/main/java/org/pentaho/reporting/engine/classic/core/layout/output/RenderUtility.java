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

package org.pentaho.reporting.engine.classic.core.layout.output;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoder;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoderRegistry;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Creation-Date: 12.05.2007, 15:58:43
 *
 * @author Thomas Morgner
 */
public class RenderUtility {
  private static final Log logger = LogFactory.getLog( RenderUtility.class );

  private RenderUtility() {
  }

  public static String getEncoderType( final ReportAttributeMap attributes ) {
    final Object attribute =
        attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.IMAGE_ENCODING_TYPE );
    if ( attribute == null ) {
      return ImageEncoderRegistry.IMAGE_PNG;
    }

    final String encoder = String.valueOf( attribute );
    if ( ImageEncoderRegistry.getInstance().isEncoderAvailable( encoder ) ) {
      return encoder;
    }

    return ImageEncoderRegistry.IMAGE_PNG;
  }

  public static float getEncoderQuality( final ReportAttributeMap attributeMap ) {
    final Object attribute =
        attributeMap.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.IMAGE_ENCODING_QUALITY );
    if ( attribute == null ) {
      return 0.9f;
    }

    if ( attribute instanceof Number ) {
      final Number n = (Number) attribute;
      final float v = n.floatValue();
      if ( v < 0.01 ) {
        return 0.01f;
      }
      if ( v > 0.999 ) {
        return 0.999f;
      }
      return v;
    }
    return 0.9f;
  }

  public static boolean isFontSmooth( final StyleSheet styleSheet, final OutputProcessorMetaData metaData ) {
    final double fontSize =
        styleSheet.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, metaData
            .getNumericFeatureValue( OutputProcessorFeature.DEFAULT_FONT_SIZE ) );

    final FontSmooth smoothing = (FontSmooth) styleSheet.getStyleProperty( TextStyleKeys.FONT_SMOOTH );
    final boolean antiAliasing;
    if ( FontSmooth.NEVER.equals( smoothing ) ) {
      antiAliasing = false;
    } else if ( FontSmooth.AUTO.equals( smoothing )
        && fontSize <= metaData.getNumericFeatureValue( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD ) ) {
      antiAliasing = false;
    } else {
      antiAliasing = true;
    }
    return antiAliasing;
  }

  /**
   * Encodes the given image as PNG, stores the image in the generated file and returns the name of the new image file.
   *
   * @param image
   *          the image to be encoded
   * @return the name of the image, never null.
   * @throws IOException
   *           if an IO error occurred.
   */
  public static byte[] encodeImage( final Image image ) throws UnsupportedEncoderException, IOException {
    return encodeImage( image, ImageEncoderRegistry.IMAGE_PNG, 0.9f, true );
  }

  public static byte[] encodeImage( final Image image, final String mimeType, final float quality, final boolean alpha )
    throws UnsupportedEncoderException, IOException {
    final MemoryByteArrayOutputStream byteOut = new MemoryByteArrayOutputStream( 65536, 65536 * 2 );
    encodeImage( byteOut, image, mimeType, quality, alpha );
    return byteOut.toByteArray();
  }

  public static void encodeImage( final OutputStream outputStream, final Image image, final String mimeType,
      final float quality, final boolean alpha ) throws UnsupportedEncoderException, IOException {
    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();

    final ImageEncoder imageEncoder = ImageEncoderRegistry.getInstance().createEncoder( mimeType );
    if ( imageEncoder == null ) {
      throw new UnsupportedEncoderException( "The encoder for mime-type '" + mimeType + "' is not available" );
    }

    imageEncoder.encodeImage( image, outputStream, quality, alpha );
  }

  public static Image scaleImage( final Image img, final int targetWidth, final int targetHeight,
      final Object hintValue, final boolean higherQuality ) {
    final int type = BufferedImage.TYPE_INT_ARGB;

    Image ret = img;
    int w;
    int h;
    do {

      if ( higherQuality ) {
        final int imageWidth = ret.getWidth( null );
        final int imageHeight = ret.getHeight( null );
        if ( imageWidth < targetWidth ) {
          // This is a up-scale operation.
          w = Math.min( imageWidth << 1, targetWidth );
        } else if ( imageWidth > targetWidth ) {
          // downscale
          w = Math.max( imageWidth >> 1, targetWidth );
        } else {
          w = imageWidth;
        }

        if ( imageHeight < targetHeight ) {
          // This is a up-scale operation.
          h = Math.min( imageHeight << 1, targetHeight );
        } else if ( imageHeight > targetHeight ) {
          // downscale
          h = Math.max( imageHeight >> 1, targetHeight );
        } else {
          h = imageHeight;
        }
      } else {
        w = targetWidth;
        h = targetHeight;
      }

      final BufferedImage tmp = new BufferedImage( w, h, type );
      final Graphics2D g2 = tmp.createGraphics();
      g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, hintValue );
      // this one scales the image ..
      if ( ret instanceof BufferedImage ) {
        if ( g2.drawImage( ret, 0, 0, w, h, null ) == false ) {
          logger.debug( "Failed to scale the image. This should not happen." );
        }
      } else {
        final WaitingImageObserver obs = new WaitingImageObserver( ret );
        while ( g2.drawImage( ret, 0, 0, w, h, null ) == false ) {
          obs.waitImageLoaded();
          if ( obs.isError() ) {
            logger.warn( "Error while loading the image during the rendering." );
            break;
          }
        }

      }
      g2.dispose();

      ret = tmp;
    } while ( w != targetWidth || h != targetHeight );

    return ret;
  }

  public static double getNormalizationScale( final OutputProcessorMetaData metaData ) {
    final double devResolution = metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    final double scale;
    if ( metaData.isFeatureSupported( OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING ) && devResolution > 0 ) {
      scale = devResolution / 72.0;
    } else {
      scale = 1;
    }
    return scale;
  }

  public static ImageContainer createImageFromDrawable( final DrawableWrapper drawable, final StrictBounds rect,
      final RenderNode box, final OutputProcessorMetaData metaData ) {
    return createImageFromDrawable( drawable, rect, box.getStyleSheet(), metaData );
  }

  public static DefaultImageReference createImageFromDrawable( final DrawableWrapper drawable, final StrictBounds rect,
      final StyleSheet box, final OutputProcessorMetaData metaData ) {
    final int imageWidth = (int) StrictGeomUtility.toExternalValue( rect.getWidth() );
    final int imageHeight = (int) StrictGeomUtility.toExternalValue( rect.getHeight() );

    if ( imageWidth == 0 || imageHeight == 0 ) {
      return null;
    }

    final double scale = RenderUtility.getNormalizationScale( metaData );
    final Image image = ImageUtils.createTransparentImage( (int) ( imageWidth * scale ), (int) ( imageHeight * scale ) );
    final Graphics2D g2 = (Graphics2D) image.getGraphics();

    final Object attribute = box.getStyleProperty( ElementStyleKeys.ANTI_ALIASING );
    if ( attribute != null ) {
      if ( Boolean.TRUE.equals( attribute ) ) {
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      } else if ( Boolean.FALSE.equals( attribute ) ) {
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
      }

    }
    if ( RenderUtility.isFontSmooth( box, metaData ) ) {
      g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
    } else {
      g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
    }

    g2.scale( scale, scale );
    // the clipping bounds are a sub-area of the whole drawable
    // we only want to print a certain area ...

    final String fontName = (String) box.getStyleProperty( TextStyleKeys.FONT );
    final int fontSize = box.getIntStyleProperty( TextStyleKeys.FONTSIZE, 8 );
    final boolean bold = box.getBooleanStyleProperty( TextStyleKeys.BOLD );
    final boolean italics = box.getBooleanStyleProperty( TextStyleKeys.ITALIC );
    if ( bold && italics ) {
      g2.setFont( new Font( fontName, Font.BOLD | Font.ITALIC, fontSize ) );
    } else if ( bold ) {
      g2.setFont( new Font( fontName, Font.BOLD, fontSize ) );
    } else if ( italics ) {
      g2.setFont( new Font( fontName, Font.ITALIC, fontSize ) );
    } else {
      g2.setFont( new Font( fontName, Font.PLAIN, fontSize ) );
    }

    g2.setStroke( (Stroke) box.getStyleProperty( ElementStyleKeys.STROKE ) );
    g2.setPaint( (Paint) box.getStyleProperty( ElementStyleKeys.PAINT ) );

    drawable.draw( g2, new Rectangle2D.Double( 0, 0, imageWidth, imageHeight ) );
    g2.dispose();

    try {
      return new DefaultImageReference( image );
    } catch ( final IOException e1 ) {
      logger.warn( "Unable to fully load a given image. (It should not happen here.)", e1 );
      return null;
    }
  }

  public static long computeHorizontalAlignment( final ElementAlignment alignment, final long width,
      final long imageWidth ) {
    if ( ElementAlignment.RIGHT.equals( alignment ) ) {
      return Math.max( 0, width - imageWidth );
    }
    if ( ElementAlignment.CENTER.equals( alignment ) ) {
      return Math.max( 0, ( width - imageWidth ) / 2 );
    }
    return 0;
  }

  public static long computeVerticalAlignment( final ElementAlignment alignment, final long height,
      final long imageHeight ) {
    if ( ElementAlignment.BOTTOM.equals( alignment ) ) {
      return Math.max( 0, height - imageHeight );
    }
    if ( ElementAlignment.MIDDLE.equals( alignment ) ) {
      return Math.max( 0, ( height - imageHeight ) / 2 );
    }
    return 0;
  }

  @Deprecated
  public static ImageMap extractImageMap( final RenderableReplacedContentBox node, final DrawableWrapper drawable ) {
    return extractImageMap( drawable, node.getWidth(), node.getHeight() );
  }

  private static ImageMap extractImageMap( final DrawableWrapper drawable, final long width, final long height ) {
    final Object backend = drawable.getBackend();
    if ( backend instanceof ReportDrawable ) {
      final ReportDrawable rdrawable = (ReportDrawable) backend;
      final int imageWidth = (int) StrictGeomUtility.toExternalValue( width );
      final int imageHeight = (int) StrictGeomUtility.toExternalValue( height );
      if ( imageWidth == 0 || imageHeight == 0 ) {
        return null;
      }
      return rdrawable.getImageMap( new Rectangle2D.Double( 0, 0, imageWidth, imageHeight ) );
    }
    return null;
  }

  public static ImageMap extractImageMap( final RenderableReplacedContentBox content ) {
    final ReportAttributeMap attributes = content.getAttributes();
    Object rawObject = content.getContent().getRawObject();
    return extractImageMap( attributes, rawObject, content.getWidth(), content.getHeight() );
  }

  public static ImageMap extractImageMap( final ReportAttributeMap attributes, final Object rawObject,
      final long width, final long height ) {
    final Object manualImageMap =
        attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.IMAGE_MAP );
    if ( manualImageMap instanceof ImageMap ) {
      return (ImageMap) manualImageMap;
    } else {
      if ( rawObject instanceof DrawableWrapper ) {
        final DrawableWrapper drawable = (DrawableWrapper) rawObject;
        return RenderUtility.extractImageMap( drawable, width, height );
      } else {
        return null;
      }
    }
  }
}
