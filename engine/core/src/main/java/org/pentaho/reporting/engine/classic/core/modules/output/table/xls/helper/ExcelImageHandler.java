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
 *  Copyright (c) 2006 - 2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A specialized class containing all image handling functionality for Excel exports.
 */
public class ExcelImageHandler {
  private static final Log logger = LogFactory.getLog( ExcelPrinter.class );
  private ResourceManager resourceManager;
  private ExcelPrinterBase printerBase;

  public ExcelImageHandler( final ResourceManager resourceManager, final ExcelPrinterBase printerBase ) {
    ArgumentNullException.validate( "resourceManager", resourceManager ); // NON-NLS
    ArgumentNullException.validate( "printerBase", printerBase ); // NON-NLS

    this.resourceManager = resourceManager;
    this.printerBase = printerBase;
  }

  /**
   * Produces the content for image or drawable cells. Excel does not support image-content in cells. Images are
   * rendered to an embedded OLE canvas instead, which is then positioned over the cell that would contain the image.
   *
   * @param layoutContext
   *          the stylesheet of the render node that produced the image.
   * @param image
   *          the image object
   * @param currentLayout
   *          the current sheet layout containing all row and column breaks
   * @param rectangle
   *          the current cell in grid-coordinates
   * @param cellBounds
   *          the bounds of the cell.
   */
  public void createImageCell( final StyleSheet layoutContext, final ImageContainer image,
      final SlimSheetLayout currentLayout, TableRectangle rectangle, final StrictBounds cellBounds ) {
    try {
      if ( rectangle == null ) {
        // there was an error while computing the grid-position for this
        // element. Evil me...
        logger.debug( "Invalid reference: I was not able to compute the rectangle for the content." ); // NON-NLS
        return;
      }

      final boolean shouldScale = layoutContext.getBooleanStyleProperty( ElementStyleKeys.SCALE );

      final int imageWidth = image.getImageWidth();
      final int imageHeight = image.getImageHeight();
      if ( imageWidth < 1 || imageHeight < 1 ) {
        return;
      }

      final double scaleFactor = computeImageScaleFactor();

      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.ALIGNMENT );
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.VALIGNMENT );

      final long internalImageWidth = StrictGeomUtility.toInternalValue( scaleFactor * imageWidth );
      final long internalImageHeight = StrictGeomUtility.toInternalValue( scaleFactor * imageHeight );

      final long cellWidth = cellBounds.getWidth();
      final long cellHeight = cellBounds.getHeight();

      final StrictBounds cb;
      final int pictureId;
      try {
        if ( shouldScale ) {
          final double scaleX;
          final double scaleY;

          final boolean keepAspectRatio = layoutContext.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
          if ( keepAspectRatio ) {
            final double imgScaleFactor =
                Math.min( cellWidth / (double) internalImageWidth, cellHeight / (double) internalImageHeight );
            scaleX = imgScaleFactor;
            scaleY = imgScaleFactor;
          } else {
            scaleX = cellWidth / (double) internalImageWidth;
            scaleY = cellHeight / (double) internalImageHeight;
          }

          final long clipWidth = (long) ( scaleX * internalImageWidth );
          final long clipHeight = (long) ( scaleY * internalImageHeight );

          final long alignmentX = RenderUtility.computeHorizontalAlignment( horizontalAlignment, cellWidth, clipWidth );
          final long alignmentY = RenderUtility.computeVerticalAlignment( verticalAlignment, cellHeight, clipHeight );

          cb =
              new StrictBounds( cellBounds.getX() + alignmentX, cellBounds.getY() + alignmentY, Math.min( clipWidth,
                  cellWidth ), Math.min( clipHeight, cellHeight ) );

          // Recompute the cells that this image will cover (now that it has been resized)
          rectangle = currentLayout.getTableBounds( cb, rectangle );

          pictureId = loadImage( image );
          if ( printerBase.isUseXlsxFormat() ) {
            if ( pictureId < 0 ) {
              return;
            }
          } else if ( pictureId <= 0 ) {
            return;
          }
        } else {
          // unscaled ..
          if ( internalImageWidth <= cellWidth && internalImageHeight <= cellHeight ) {
            // No clipping needed.
            final long alignmentX =
                RenderUtility.computeHorizontalAlignment( horizontalAlignment, cellBounds.getWidth(),
                    internalImageWidth );
            final long alignmentY =
                RenderUtility.computeVerticalAlignment( verticalAlignment, cellBounds.getHeight(), internalImageHeight );

            cb =
                new StrictBounds( cellBounds.getX() + alignmentX, cellBounds.getY() + alignmentY, internalImageWidth,
                    internalImageHeight );

            // Recompute the cells that this image will cover (now that it has been resized)
            rectangle = currentLayout.getTableBounds( cb, rectangle );

            pictureId = loadImage( image );
            if ( printerBase.isUseXlsxFormat() ) {
              if ( pictureId < 0 ) {
                return;
              }
            } else if ( pictureId <= 0 ) {
              return;
            }
          } else {
            // at least somewhere there is clipping needed.
            final long clipWidth = Math.min( cellWidth, internalImageWidth );
            final long clipHeight = Math.min( cellHeight, internalImageHeight );
            final long alignmentX =
                RenderUtility.computeHorizontalAlignment( horizontalAlignment, cellBounds.getWidth(), clipWidth );
            final long alignmentY =
                RenderUtility.computeVerticalAlignment( verticalAlignment, cellBounds.getHeight(), clipHeight );
            cb =
                new StrictBounds( cellBounds.getX() + alignmentX, cellBounds.getY() + alignmentY, clipWidth, clipHeight );

            // Recompute the cells that this image will cover (now that it has been resized)
            rectangle = currentLayout.getTableBounds( cb, rectangle );

            pictureId = loadImageWithClipping( image, clipWidth, clipHeight, scaleFactor );
            if ( printerBase.isUseXlsxFormat() ) {
              if ( pictureId < 0 ) {
                return;
              }
            } else if ( pictureId <= 0 ) {
              return;
            }
          }
        }
      } catch ( final UnsupportedEncoderException uee ) {
        // should not happen, as PNG is always supported.
        logger.warn( "Assertation-Failure: PNG encoding failed.", uee ); // NON-NLS
        return;
      }

      final ClientAnchor anchor = computeClientAnchor( currentLayout, rectangle, cb );

      Drawing patriarch = printerBase.getDrawingPatriarch();

      final Picture picture = patriarch.createPicture( anchor, pictureId );
      logger.info( String.format( "Created image: %d => %s", pictureId, picture ) ); // NON-NLS
    } catch ( final IOException e ) {
      logger.warn( "Failed to add image. Ignoring.", e ); // NON-NLS
    }
  }

  private double computeImageScaleFactor() {
    OutputProcessorMetaData metaData = printerBase.getMetaData();
    final double scaleFactor;
    final double devResolution = metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    if ( metaData.isFeatureSupported( OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING ) ) {
      if ( devResolution != 72.0 && devResolution > 0 ) {
        // Need to scale the device to its native resolution before attempting to draw the image..
        scaleFactor = ( 72.0 / devResolution );

      } else {
        scaleFactor = 1;
      }
    } else {
      scaleFactor = 1;
    }
    return scaleFactor;
  }

  protected ClientAnchor computeClientAnchor( final SlimSheetLayout currentLayout, final TableRectangle rectangle,
      final StrictBounds cb ) {
    if ( printerBase.isUseXlsxFormat() ) {
      return computeExcel2003ClientAnchor( currentLayout, rectangle, cb );
    } else {
      return computeExcel97ClientAnchor( currentLayout, rectangle, cb );
    }
  }

  protected ClientAnchor computeExcel97ClientAnchor( final SlimSheetLayout currentLayout,
      final TableRectangle rectangle, final StrictBounds cb ) {
    final int cell1x = rectangle.getX1();
    final int cell1y = rectangle.getY1();
    final int cell2x = Math.max( cell1x, rectangle.getX2() - 1 );
    final int cell2y = Math.max( cell1y, rectangle.getY2() - 1 );

    final long cell1width = currentLayout.getCellWidth( cell1x );
    final long cell1height = currentLayout.getRowHeight( cell1y );
    final long cell2width = currentLayout.getCellWidth( cell2x );
    final long cell2height = currentLayout.getRowHeight( cell2y );

    final long cell1xPos = currentLayout.getXPosition( cell1x );
    final long cell1yPos = currentLayout.getYPosition( cell1y );
    final long cell2xPos = currentLayout.getXPosition( cell2x );
    final long cell2yPos = currentLayout.getYPosition( cell2y );

    final int dx1 = (int) ( 1023 * ( ( cb.getX() - cell1xPos ) / (double) cell1width ) );
    final int dy1 = (int) ( 255 * ( ( cb.getY() - cell1yPos ) / (double) cell1height ) );
    final int dx2 = (int) ( 1023 * ( ( cb.getX() + cb.getWidth() - cell2xPos ) / (double) cell2width ) );
    final int dy2 = (int) ( 255 * ( ( cb.getY() + cb.getHeight() - cell2yPos ) / (double) cell2height ) );

    final ClientAnchor anchor = printerBase.getWorkbook().getCreationHelper().createClientAnchor();
    anchor.setDx1( dx1 );
    anchor.setDy1( dy1 );
    anchor.setDx2( dx2 );
    anchor.setDy2( dy2 );
    anchor.setCol1( cell1x );
    anchor.setRow1( cell1y );
    anchor.setCol2( cell2x );
    anchor.setRow2( cell2y );
    anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_DONT_RESIZE );
    return anchor;
  }

  protected ClientAnchor computeExcel2003ClientAnchor( final SlimSheetLayout currentLayout,
      final TableRectangle rectangle, final StrictBounds cb ) {
    final int cell1x = rectangle.getX1();
    final int cell1y = rectangle.getY1();
    final int cell2x = Math.max( cell1x, rectangle.getX2() - 1 );
    final int cell2y = Math.max( cell1y, rectangle.getY2() - 1 );

    final long cell1xPos = currentLayout.getXPosition( cell1x );
    final long cell1yPos = currentLayout.getYPosition( cell1y );
    final long cell2xPos = currentLayout.getXPosition( cell2x );
    final long cell2yPos = currentLayout.getYPosition( cell2y );

    final int dx1 = (int) StrictGeomUtility.toExternalValue( ( cb.getX() - cell1xPos ) * Units.EMU_PER_POINT );
    final int dy1 = (int) StrictGeomUtility.toExternalValue( ( cb.getY() - cell1yPos ) * Units.EMU_PER_POINT );
    final int dx2 =
        (int) Math.max( 0, StrictGeomUtility.toExternalValue( ( cb.getX() + cb.getWidth() - cell2xPos )
            * Units.EMU_PER_POINT ) );
    final int dy2 =
        (int) Math.max( 0, StrictGeomUtility.toExternalValue( ( cb.getY() + cb.getHeight() - cell2yPos )
            * Units.EMU_PER_POINT ) );

    final ClientAnchor anchor = printerBase.getWorkbook().getCreationHelper().createClientAnchor();
    anchor.setDx1( dx1 );
    anchor.setDy1( dy1 );
    anchor.setDx2( dx2 );
    anchor.setDy2( dy2 );
    anchor.setCol1( cell1x );
    anchor.setRow1( cell1y );
    anchor.setCol2( cell2x );
    anchor.setRow2( cell2y );
    anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_DONT_RESIZE );
    return anchor;
  }

  private int getImageFormat( final ResourceKey key ) {
    final URL url = resourceManager.toURL( key );
    if ( url == null ) {
      return -1;
    }

    final String file = url.getFile();
    if ( StringUtils.endsWithIgnoreCase( file, ".png" ) ) { // NON-NLS
      return Workbook.PICTURE_TYPE_PNG;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".jpg" ) || // NON-NLS
        StringUtils.endsWithIgnoreCase( file, ".jpeg" ) ) { // NON-NLS
      return Workbook.PICTURE_TYPE_JPEG;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".bmp" ) || // NON-NLS
        StringUtils.endsWithIgnoreCase( file, ".ico" ) ) { // NON-NLS
      return Workbook.PICTURE_TYPE_DIB;
    }
    return -1;
  }

  private int loadImageWithClipping( final ImageContainer reference, final long clipWidth, final long clipHeight,
      final double deviceScaleFactor ) throws IOException, UnsupportedEncoderException {

    Image image = null;
    // The image has an assigned URL ...
    if ( reference instanceof URLImageContainer ) {
      final URLImageContainer urlImage = (URLImageContainer) reference;
      final ResourceKey url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if ( url != null && urlImage.isLoadable() ) {
        if ( reference instanceof LocalImageContainer ) {
          final LocalImageContainer li = (LocalImageContainer) reference;
          image = li.getImage();
        }
        if ( image == null ) {
          try {
            final Resource resource = resourceManager.create( url, null, Image.class );
            image = (Image) resource.getResource();
          } catch ( final ResourceException e ) {
            // ignore.
          }
        }
      }
    }

    if ( reference instanceof LocalImageContainer ) {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) reference;
      if ( image == null ) {
        image = li.getImage();
      }
    }

    if ( image != null ) {
      // now encode the image. We don't need to create digest data
      // for the image contents, as the image is perfectly identifyable
      // by its URL
      return clipAndEncodeImage( image, clipWidth, clipHeight, deviceScaleFactor );
    }
    return -1;
  }

  private int
    clipAndEncodeImage( final Image image, final long width, final long height, final double deviceScaleFactor )
      throws UnsupportedEncoderException, IOException {
    final int imageWidth = (int) StrictGeomUtility.toExternalValue( width );
    final int imageHeight = (int) StrictGeomUtility.toExternalValue( height );
    // first clip.
    final BufferedImage bi = ImageUtils.createTransparentImage( imageWidth, imageHeight );
    final Graphics2D graphics = (Graphics2D) bi.getGraphics();
    graphics.scale( deviceScaleFactor, deviceScaleFactor );

    if ( image instanceof BufferedImage ) {
      if ( graphics.drawImage( image, null, null ) == false ) {
        logger.debug( "Failed to render the image. This should not happen for BufferedImages" ); // NON-NLS
      }
    } else {
      final WaitingImageObserver obs = new WaitingImageObserver( image );
      obs.waitImageLoaded();

      while ( graphics.drawImage( image, null, obs ) == false ) {
        obs.waitImageLoaded();
        if ( obs.isError() ) {
          logger.warn( "Error while loading the image during the rendering." ); // NON-NLS
          break;
        }
      }
    }

    graphics.dispose();
    final byte[] data = RenderUtility.encodeImage( bi );
    return printerBase.getWorkbook().addPicture( data, Workbook.PICTURE_TYPE_PNG );
  }

  private int loadImage( final ImageContainer reference ) throws IOException, UnsupportedEncoderException {
    final Workbook workbook = printerBase.getWorkbook();
    Image image = null;
    // The image has an assigned URL ...
    if ( reference instanceof URLImageContainer ) {
      final URLImageContainer urlImage = (URLImageContainer) reference;
      final ResourceKey url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if ( url != null && urlImage.isLoadable() ) {
        // and the image is one of the supported image formats ...
        // we we can embedd it directly ...
        final int format = getImageFormat( url );
        if ( format == -1 ) {
          // This is a unsupported image format.
          if ( reference instanceof LocalImageContainer ) {
            final LocalImageContainer li = (LocalImageContainer) reference;
            image = li.getImage();
          }
          if ( image == null ) {
            try {
              final Resource resource = resourceManager.create( url, null, Image.class );
              image = (Image) resource.getResource();
            } catch ( final ResourceException re ) {
              logger.info( "Failed to load image from URL " + url, re ); // NON-NLS
            }
          }
        } else {
          try {
            final ResourceData data = resourceManager.load( url );
            // create the image
            return workbook.addPicture( data.getResource( resourceManager ), format );
          } catch ( final ResourceException re ) {
            logger.info( "Failed to load image from URL " + url, re ); // NON-NLS
          }

        }
      }
    }

    if ( reference instanceof LocalImageContainer ) {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) reference;
      if ( image == null ) {
        image = li.getImage();
      }
    }

    if ( image != null ) {
      // now encode the image. We don't need to create digest data
      // for the image contents, as the image is perfectly identifyable
      // by its URL
      final byte[] data = RenderUtility.encodeImage( image );
      return workbook.addPicture( data, Workbook.PICTURE_TYPE_PNG );
    }
    return -1;
  }

}
