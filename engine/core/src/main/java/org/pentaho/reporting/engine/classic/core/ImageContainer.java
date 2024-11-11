/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

/**
 * A image container stores all layout information to process images in a report.
 * <p/>
 * The ImageContainer is the common base interface for the URLImageContainer (which references remote images) and the
 * LocalImageContainer (which references local AWT-Image instances).
 * <p/>
 * All the layouting engine needs to know about images, are the image dimensions and the possible scale factor for the
 * contained image. Only the content creators need the knowledge on how to access the contained image and and which
 * other container types might exist.
 *
 * @author Thomas Morgner
 */
public interface ImageContainer extends Cloneable {
  /**
   * Returns the unscaled width of the contained image. The width must be known during the layouting process, returning
   * -1 to indicate an unknown size (as the AWT does) is not valid.
   *
   * @return the width of the image.
   */
  public int getImageWidth();

  /**
   * Returns the unscaled height of the contained image. The height must be known during the layouting process,
   * returning -1 to indicate an unknown size (as the AWT does) is not valid.
   *
   * @return the height of the image.
   */
  public int getImageHeight();

  /**
   * Defines the image's horizontal scale. This is the factor to convert the image from it's original resolution to the
   * java resolution of 72dpi.
   * <p/>
   * This is not the scale that is computed by the layouter; that one is derived from the ImageContent itself.
   *
   * @return the horizontal scale.
   */
  public float getScaleX();

  /**
   * Defines the image's vertical scale. This is the factor to convert the image from it's original resolution to the
   * java resolution of 72dpi.
   * <p/>
   * This is not the scale that is computed by the layouter; that one is derived from the ImageContent.
   *
   * @return the vertical scale.
   */
  public float getScaleY();
}
