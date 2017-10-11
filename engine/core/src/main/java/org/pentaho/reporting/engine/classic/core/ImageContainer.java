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
