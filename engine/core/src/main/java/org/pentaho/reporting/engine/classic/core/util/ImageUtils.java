/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
