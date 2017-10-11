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

package org.pentaho.reporting.engine.classic.core;

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * An DefaultImageReference encapsulates the source of an image together with a <code>java.awt.Image</code>. The source
 * is used to create a higher resolution version if needed. The source file/URL may also be inlined into the output
 * target, to create better results.
 * <p/>
 * This implementation provides a reasonable default implementation to encapsualte local AWT-images into reports.
 * <p/>
 * The given image might specify a fixed scale factor for the given image. The scaling will be applied before any layout
 * computations will be performed.
 *
 * @author Thomas Morgner
 */
public class DefaultImageReference implements Serializable, URLImageContainer, LocalImageContainer {
  /**
   * A unique identifier for long term persistance.
   */
  private static final long serialVersionUID = 3223926147102983309L;

  /**
   * The image.
   */
  private Image image;

  /**
   * The image URL.
   */
  private URL url;

  /**
   * The width of the (unscaled) image.
   */
  private int width;

  /**
   * The height of the (unscaled) image.
   */
  private int height;

  /**
   * The scale factor.
   */
  private float scaleX = 1.0f;

  /**
   * The scale factor.
   */
  private float scaleY = 1.0f;
  private ResourceKey resourceKey;

  /**
   * Creates a new ImageReference without an assigned URL for the Image. This image reference will not be loadable and
   * cannot be used to embedd the original rawdata of the image into the generated content.
   *
   * @param img
   *          the image for this reference.
   * @throws NullPointerException
   *           if the image is null.
   * @throws java.io.IOException
   *           if an IOError occured while loading the image.
   */
  public DefaultImageReference( final Image img ) throws IOException {
    if ( img == null ) {
      throw new NullPointerException();
    }
    this.image = img;
    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();
    if ( obs.isError() ) {
      throw new IOException( "Failed to load the image. ImageObserver signaled an error." );
    }
    this.width = image.getWidth( null );
    this.height = image.getHeight( null );
  }

  public DefaultImageReference( final Resource imageResource ) throws ResourceException {
    if ( imageResource == null ) {
      throw new NullPointerException();
    }
    final Object o = imageResource.getResource();
    if ( o instanceof Image == false ) {
      throw new ResourceException( "ImageResource does not contain a java.awt.Image object." );
    }
    final ResourceKey resKey = imageResource.getSource();
    final Object identifier = resKey.getIdentifier();
    if ( identifier instanceof URL ) {
      this.url = (URL) identifier;
    }
    this.resourceKey = resKey;
    this.image = (Image) o;
    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();
    if ( obs.isError() ) {
      throw new ResourceException( "Failed to load the image. ImageObserver signaled an error." );
    }
    this.width = image.getWidth( null );
    this.height = image.getHeight( null );
  }

  /**
   * Creates a new image reference without assigning either an Image or an URL. This DefaultImageReference will act as
   * place holder to reserve space during the layouting, no content will be generated.
   *
   * @param w
   *          the width of the unscaled image.
   * @param h
   *          the height of the unscaled image.
   */
  public DefaultImageReference( final int w, final int h ) {
    this.width = w;
    this.height = h;
  }

  /**
   * Copies the contents of the given DefaultImageReference.
   *
   * @param parent
   *          the parent.
   */
  public DefaultImageReference( final DefaultImageReference parent ) {
    if ( parent == null ) {
      throw new NullPointerException( "The given parent must not be null." );
    }
    this.width = parent.width;
    this.height = parent.height;
    this.image = parent.image;
    this.url = parent.url;
    this.resourceKey = parent.resourceKey;
  }

  /**
   * Returns the original image if available.
   *
   * @return The current image instance, or null, if no image has been assigned.
   */
  public Image getImage() {
    return image;
  }

  /**
   * Returns the source URL for the image.
   *
   * @return The URL from where the image has been loaded, or null, if the source URL is not known.
   */
  public URL getSourceURL() {
    return url;
  }

  /**
   * Returns the a string version of the source URL. If no URL has been assigned, this method will return null.
   *
   * @return a String representing the assigned URL.
   */
  public String getSourceURLString() {
    if ( url == null ) {
      return null;
    }
    return url.toExternalForm();
  }

  /**
   * Returns a String representing this object. Useful for debugging.
   *
   * @return The string.
   */
  public String toString() {
    final StringBuffer buf = new StringBuffer( 100 );

    buf.append( "ImageReference={ URL=" );
    buf.append( getSourceURL() );
    buf.append( ", key=" );
    buf.append( getResourceKey() );
    buf.append( ", image=" );
    buf.append( getImage() );
    buf.append( ", width=" );
    buf.append( getImageWidth() );
    buf.append( ", height=" );
    buf.append( getImageHeight() );
    buf.append( ", scaleX=" );
    buf.append( getScaleX() );
    buf.append( ", scaleY=" );
    buf.append( getScaleY() );
    buf.append( '}' );

    return buf.toString();
  }

  public ResourceKey getResourceKey() {
    return resourceKey;
  }

  /**
   * Checks for equality.
   *
   * @param obj
   *          the object to test.
   * @return true if the specified object is equal to this one.
   */
  public boolean equals( final Object obj ) {
    if ( obj == null ) {
      return false;
    }
    if ( obj instanceof DefaultImageReference == false ) {
      return false;
    }
    final DefaultImageReference ref = (DefaultImageReference) obj;
    if ( ObjectUtilities.equal( String.valueOf( url ), String.valueOf( ref.url ) ) == false ) {
      return false;
    }
    if ( width != ref.width ) {
      return false;
    }
    if ( height != ref.height ) {
      return false;
    }
    if ( scaleX != ref.scaleX ) {
      return false;
    }
    if ( scaleY != ref.scaleY ) {
      return false;
    }
    return true;
  }

  /**
   * Compute a hashcode for this imageReference.
   *
   * @return the hashcode
   */
  public int hashCode() {
    int result = width;
    result = 29 * result + height;
    result = 29 * result + Float.floatToIntBits( scaleX );
    result = 29 * result + Float.floatToIntBits( scaleY );
    result = 29 * result + ( image != null ? image.hashCode() : 0 );
    result = 29 * result + ( url != null ? url.toString().hashCode() : 0 );
    return result;
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   * @throws CloneNotSupportedException
   *           this should never be thrown.
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Returns the (unscaled) image width.
   *
   * @return the image width.
   */
  public int getImageWidth() {
    return width;
  }

  /**
   * Returns the (unscaled) image height.
   *
   * @return the image height.
   */
  public int getImageHeight() {
    return height;
  }

  /**
   * Checks whether this image reference is loadable. A default image reference is loadable, if a valid URL has been
   * set.
   *
   * @return true, if it is loadable, false otherwise.
   */
  public boolean isLoadable() {
    return getResourceKey() != null;
  }

  /**
   * Returns the identity information. This instance returns the URL of the image, if any.
   *
   * @return the image identifier.
   */
  public Object getIdentity() {
    if ( url == null ) {
      return null;
    }
    return String.valueOf( url );
  }

  /**
   * Returns the name of this image reference. If an URL has been set, this will return the URL of the image, else null
   * is returned.
   *
   * @return the name.
   */
  public String getName() {
    if ( url != null ) {
      return url.toExternalForm();
    }
    return null;
  }

  /**
   * Checks whether this image has a assigned identity. Two identities should be equal, if the image contents are equal.
   *
   * @return true, if that image contains contains identity information, false otherwise.
   */
  public boolean isIdentifiable() {
    return url != null;
  }

  /**
   * Returns a predefined scaling factor. That scaling will be applied before any layout specific scaling is done.
   *
   * @return the scale factor.
   */
  public float getScaleX() {
    return scaleX;
  }

  /**
   * Returns a predefined scaling factor. That scaling will be applied before any layout specific scaling is done.
   *
   * @return the scale factor.
   */
  public float getScaleY() {
    return scaleY;
  }

  /**
   * Defines a predefined scaling factor. That scaling will be applied before any layout specific scaling is done.
   * <p/>
   * If your image has a higher resolution than 72dpi, this factor should be a value lower than 1 (the image will be
   * scaled down).
   *
   * @param sx
   *          the scale factor.
   * @param sy
   *          the scale factor.
   */
  public void setScale( final float sx, final float sy ) {
    this.scaleX = sx;
    this.scaleY = sy;
  }
}
