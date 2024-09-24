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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlContentGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriteException;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultHtmlContentGenerator implements HtmlContentGenerator {
  public static class ImageData {
    private byte[] imageData;
    private String mimeType;
    private String originalFileName;

    private ImageData( final byte[] imageData, final String mimeType, final String originalFileName ) {
      if ( imageData == null ) {
        throw new NullPointerException();
      }
      if ( mimeType == null ) {
        throw new NullPointerException();
      }
      if ( originalFileName == null ) {
        throw new NullPointerException();
      }

      this.imageData = imageData;
      this.mimeType = mimeType;
      this.originalFileName = originalFileName;
    }

    public byte[] getImageData() {
      return imageData;
    }

    public String getMimeType() {
      return mimeType;
    }

    public String getOriginalFileName() {
      return originalFileName;
    }
  }

  private static final Log logger = LogFactory.getLog( DefaultHtmlContentGenerator.class );

  private final HashSet<String> validRawTypes;
  private ResourceManager resourceManager;
  private HashMap<ResourceKey, String> knownResources;
  private HashMap<String, String> knownImages;
  private boolean copyExternalImages;
  private ContentLocation dataLocation;
  private NameGenerator dataNameGenerator;
  private ContentUrlReWriteService rewriterService;

  public DefaultHtmlContentGenerator( final ResourceManager resourceManager ) {
    this.knownImages = new HashMap<String, String>();
    this.resourceManager = resourceManager;
    this.knownResources = new HashMap<ResourceKey, String>();

    this.validRawTypes = new HashSet<String>();
    this.validRawTypes.add( "image/gif" );
    this.validRawTypes.add( "image/x-xbitmap" );
    this.validRawTypes.add( "image/gi_" );
    this.validRawTypes.add( "image/jpeg" );
    this.validRawTypes.add( "image/jpg" );
    this.validRawTypes.add( "image/jp_" );
    this.validRawTypes.add( "application/jpg" );
    this.validRawTypes.add( "application/x-jpg" );
    this.validRawTypes.add( "image/pjpeg" );
    this.validRawTypes.add( "image/pipeg" );
    this.validRawTypes.add( "image/vnd.swiftview-jpeg" );
    this.validRawTypes.add( "image/x-xbitmap" );
    this.validRawTypes.add( "image/png" );
    this.validRawTypes.add( "application/png" );
    this.validRawTypes.add( "application/x-png" );

  }

  public void setDataWriter( final ContentLocation dataLocation, final NameGenerator dataNameGenerator,
      final ContentUrlReWriteService rewriterService ) {
    this.dataNameGenerator = dataNameGenerator;
    this.dataLocation = dataLocation;
    this.rewriterService = rewriterService;
  }

  public void setCopyExternalImages( final boolean copyExternalImages ) {
    this.copyExternalImages = copyExternalImages;
  }

  public boolean isCopyExternalImages() {
    return copyExternalImages;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public void registerFailure( final ResourceKey source ) {
    knownResources.put( source, null );
  }

  public void registerContent( final ResourceKey source, final String name ) {
    knownResources.put( source, name );
  }

  public boolean isRegistered( final ResourceKey source ) {
    return knownResources.containsKey( source );
  }

  public String getRegisteredName( final ResourceKey source ) {
    final String o = knownResources.get( source );
    if ( o != null ) {
      return o;
    }
    return null;
  }

  public String writeRaw( final ResourceKey source ) throws IOException {
    if ( source == null ) {
      throw new NullPointerException();
    }

    if ( copyExternalImages == false ) {
      final Object identifier = source.getIdentifier();
      if ( identifier instanceof URL ) {
        final URL url = (URL) identifier;
        final String protocol = url.getProtocol();
        if ( "http".equalsIgnoreCase( protocol ) || "https".equalsIgnoreCase( protocol )
            || "ftp".equalsIgnoreCase( protocol ) ) {
          return url.toExternalForm();
        }
      }
    }

    if ( dataLocation == null ) {
      return null;
    }

    try {
      final ResourceData resourceData = resourceManager.load( source );
      final String mimeType = queryMimeType( resourceData );
      if ( isValidImage( mimeType ) ) {
        // lets do some voodo ..
        final ContentItem item =
            dataLocation.createItem( dataNameGenerator.generateName( extractFilename( resourceData ), mimeType ) );
        if ( item.isWriteable() ) {
          item.setAttribute( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.CONTENT_TYPE, mimeType );

          // write it out ..
          final InputStream stream = new BufferedInputStream( resourceData.getResourceAsStream( resourceManager ) );
          try {
            final OutputStream outputStream = new BufferedOutputStream( item.getOutputStream() );
            try {
              IOUtils.getInstance().copyStreams( stream, outputStream );
            } finally {
              outputStream.close();
            }
          } finally {
            stream.close();
          }

          return rewriterService.rewriteContentDataItem( item );
        }
      }
    } catch ( ResourceLoadingException e ) {
      // Ok, loading the resource failed. Not a problem, so we will
      // recode the raw-object instead ..
    } catch ( ContentIOException e ) {
      // ignore it ..
    } catch ( URLRewriteException e ) {
      logger.warn( "Rewriting the URL failed.", e );
      throw new RuntimeException( "Failed", e );
    }
    return null;
  }

  public String rewrite( final ImageContainer image, ContentItem contentItem ) {
    if ( image == null ) {
      throw new NullPointerException();
    }

    if ( dataLocation == null ) {
      return null;
    }

    final String cacheKey;
    if ( image instanceof URLImageContainer ) {
      final URLImageContainer uic = (URLImageContainer) image;
      cacheKey = uic.getSourceURLString();
      final String retval = knownImages.get( cacheKey );
      if ( retval != null ) {
        return retval;
      }

      final String sourceURLString = uic.getSourceURLString();
      if ( uic.isLoadable() == false && sourceURLString != null ) {
        knownImages.put( cacheKey, sourceURLString );
        return sourceURLString;
      }
    } else {
      cacheKey = null;
    }

    try {
      final String contentURL = rewriterService.rewriteContentDataItem( contentItem );
      if ( cacheKey != null ) {
        knownImages.put( cacheKey, contentURL );
      }

      return contentURL;
    } catch ( URLRewriteException re ) {
      // cannot handle this ..
      logger.warn( "Failed to write the URL: Reason given was: " + re.getMessage() );
      return null;
    }
  }

  public ContentItem writeImage( final ImageContainer image, final String encoderType, final float quality,
                           final boolean alpha ) throws ContentIOException, IOException {
    try {
      final ImageData data = getImageData( image, encoderType, quality, alpha );
      if ( data == null ) {
        return null;
      }
      // write the encoded picture ...
      final String filename = IOUtils.getInstance().stripFileExtension( data.getOriginalFileName() );
      final ContentItem dataFile =
          dataLocation.createItem( dataNameGenerator.generateName( filename, data.getMimeType() ) );

      // a png encoder is included in JCommon ...
      final OutputStream out = new BufferedOutputStream( dataFile.getOutputStream() );
      try {
        out.write( data.getImageData() );
        out.flush();
      } finally {
        out.close();
      }
      return dataFile;
    } catch ( ContentCreationException cce ) {
      // Can't create the content
      logger.warn( "Failed to create the content image: Reason given was: " + cce.getMessage() );
      return null;
    } catch ( UnsupportedEncoderException e ) {
      logger.warn( "Failed to write the URL: Reason given was: " + e.getMessage() );
      return null;
    }
  }

  private String queryMimeType( final ResourceData resourceData ) throws ResourceLoadingException, IOException {
    final Object contentType = resourceData.getAttribute( ResourceData.CONTENT_TYPE );
    if ( contentType instanceof String ) {
      return (String) contentType;
    }

    // now we are getting very primitive .. (Kids, dont do this at home)
    final byte[] data = new byte[12];
    resourceData.getResource( resourceManager, data, 0, data.length );
    return queryMimeType( data );
  }

  private String queryMimeType( final byte[] data ) throws IOException {
    final ByteArrayInputStream stream = new ByteArrayInputStream( data );
    if ( isGIF( stream ) ) {
      return "image/gif";
    }
    stream.reset();
    if ( isJPEG( stream ) ) {
      return "image/jpeg";
    }
    stream.reset();
    if ( isPNG( stream ) ) {
      return "image/png";
    }
    return null;
  }

  private boolean isPNG( final ByteArrayInputStream data ) {
    final int[] PNF_FINGERPRINT = { 137, 80, 78, 71, 13, 10, 26, 10 };
    for ( int i = 0; i < PNF_FINGERPRINT.length; i++ ) {
      if ( PNF_FINGERPRINT[i] != data.read() ) {
        return false;
      }
    }
    return true;
  }

  private boolean isJPEG( final InputStream data ) throws IOException {
    final int[] JPG_FINGERPRINT_1 = { 0xFF, 0xD8, 0xFF, 0xE0 };
    for ( int i = 0; i < JPG_FINGERPRINT_1.length; i++ ) {
      if ( JPG_FINGERPRINT_1[i] != data.read() ) {
        return false;
      }
    }
    // then skip two bytes ..
    if ( data.read() == -1 ) {
      return false;
    }
    if ( data.read() == -1 ) {
      return false;
    }

    final int[] JPG_FINGERPRINT_2 = { 0x4A, 0x46, 0x49, 0x46, 0x00 };
    for ( int i = 0; i < JPG_FINGERPRINT_2.length; i++ ) {
      if ( JPG_FINGERPRINT_2[i] != data.read() ) {
        return false;
      }
    }
    return true;
  }

  private boolean isGIF( final InputStream data ) throws IOException {
    final int[] GIF_FINGERPRINT = { 'G', 'I', 'F', '8' };
    for ( int i = 0; i < GIF_FINGERPRINT.length; i++ ) {
      if ( GIF_FINGERPRINT[i] != data.read() ) {
        return false;
      }
    }
    return true;
  }

  private String extractFilename( final ResourceData resourceData ) {
    final String filename = (String) resourceData.getAttribute( ResourceData.FILENAME );
    if ( filename == null ) {
      return "image";
    }

    final String pureFileName = IOUtils.getInstance().getFileName( filename );
    return IOUtils.getInstance().stripFileExtension( pureFileName );
  }

  private boolean isValidImage( final String mimeType ) {
    return validRawTypes.contains( mimeType );
  }

  public ImageData getImageData( final ImageContainer image, final String encoderType, final float quality,
      final boolean alpha ) throws IOException, UnsupportedEncoderException {
    ResourceManager resourceManager = getResourceManager();
    ResourceKey url = null;
    // The image has an assigned URL ...
    if ( image instanceof URLImageContainer ) {
      final URLImageContainer urlImage = (URLImageContainer) image;

      url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if ( url != null ) {
        if ( urlImage.isLoadable() && isSupportedImageFormat( url ) ) {
          try {
            final ResourceData data = resourceManager.load( url );
            final byte[] imageData = data.getResource( resourceManager );
            final String mimeType = queryMimeType( imageData );
            final URL maybeRealURL = resourceManager.toURL( url );
            if ( maybeRealURL != null ) {
              final String originalFileName = IOUtils.getInstance().getFileName( maybeRealURL );
              return new ImageData( imageData, mimeType, originalFileName );
            } else {
              return new ImageData( imageData, mimeType, "picture" );
            }
          } catch ( ResourceException re ) {
            // ok, try as local ...
            logger.debug( "Failed to process image as raw-data, trying as processed data next", re );
          }
        }
      }
    }

    if ( image instanceof LocalImageContainer ) {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) image;
      Image awtImage = li.getImage();
      if ( awtImage == null ) {
        if ( url != null ) {
          try {
            final Resource resource = resourceManager.createDirectly( url, Image.class );
            awtImage = (Image) resource.getResource();
          } catch ( ResourceException e ) {
            // ignore.
          }
        }
      }
      if ( awtImage != null ) {
        // now encode the image. We don't need to create digest data
        // for the image contents, as the image is perfectly identifyable
        // by its URL
        final byte[] imageData = RenderUtility.encodeImage( awtImage, encoderType, quality, alpha );
        final String originalFileName;
        if ( url != null ) {
          final URL maybeRealURL = resourceManager.toURL( url );
          if ( maybeRealURL != null ) {
            originalFileName = IOUtils.getInstance().getFileName( maybeRealURL );
          } else {
            // we just need the picture part, the file-extension will be replaced by one that matches
            // the mime-type.
            originalFileName = "picture";
          }
        } else {
          // we just need the picture part, the file-extension will be replaced by one that matches
          // the mime-type.
          originalFileName = "picture";
        }
        return new ImageData( imageData, encoderType, originalFileName );
      }
    }
    return null;
  }

  /**
   * Tests, whether the given URL points to a supported file format for common browsers. Returns true if the URL
   * references a JPEG, PNG or GIF image, false otherwise.
   * <p/>
   * The checked filetypes are the ones recommended by the W3C.
   *
   * @param key
   *          the url that should be tested.
   * @return true, if the content type is supported by the browsers, false otherwise.
   */
  protected boolean isSupportedImageFormat( final ResourceKey key ) {
    ResourceManager resourceManager = getResourceManager();
    final URL url = resourceManager.toURL( key );
    if ( url == null ) {
      return false;
    }

    final String file = url.getFile();
    if ( StringUtils.endsWithIgnoreCase( file, ".jpg" ) ) {
      return true;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".jpeg" ) ) {
      return true;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".png" ) ) {
      return true;
    }
    if ( StringUtils.endsWithIgnoreCase( file, ".gif" ) ) {
      return true;
    }
    return false;
  }

  public ContentItem createItem( final String name, final String mimeType ) throws ContentIOException {
    return dataLocation.createItem( dataNameGenerator.generateName( name, mimeType ) );
  }

  public boolean isExternalContentAvailable() {
    return dataLocation != null;
  }
}
