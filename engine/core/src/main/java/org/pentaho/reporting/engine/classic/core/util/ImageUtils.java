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

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Provides utility methods for image creation and manipulation.
 *
 * @author Thomas Morgner
 */
public final class ImageUtils {
  /**
   * DefaultConstructor.
   */
  private ImageUtils() {
  }

  /**
   * Creates a transparent image. These can be used for aligning menu items.
   *
   * @param width
   *          the width.
   * @param height
   *          the height.
   * @return the created transparent image.
   */
  public static BufferedImage createTransparentImage( final int width, final int height ) {
    return new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
  }

  /**
   * Creates a transparent icon. The Icon can be used for aligning menu items.
   *
   * @param width
   *          the width of the new icon
   * @param height
   *          the height of the new icon
   * @return the created transparent icon.
   */
  public static Icon createTransparentIcon( final int width, final int height ) {
    return new ImageIcon( createTransparentImage( width, height ) );
  }
}
