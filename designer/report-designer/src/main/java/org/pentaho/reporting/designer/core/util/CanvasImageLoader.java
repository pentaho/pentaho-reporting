/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.util;

import javax.swing.*;

public class CanvasImageLoader {

  private static final CanvasImageLoader instance = new CanvasImageLoader();

  private ImageIcon backgroundImage;
  private ImageIcon leftShadowImage;
  private ImageIcon rightShadowImage;
  private ImageIcon bottomShadowImage;
  private ImageIcon leftCornerShadowImage;
  private ImageIcon rightCornerShadowImage;

  public static CanvasImageLoader getInstance() {
    return instance;
  }

  private CanvasImageLoader() {
    backgroundImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/canvas_background.jpg" ) ); // NON-NLS
    leftShadowImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/left_shadow.png" ) ); // NON-NLS
    rightShadowImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/right_shadow.png" ) ); // NON-NLS
    bottomShadowImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/bottom_shadow.png" ) ); // NON-NLS
    leftCornerShadowImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/left_corner_shadow.png" ) ); // NON-NLS
    rightCornerShadowImage = new ImageIcon( CanvasImageLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/right_corner_shadow.png" ) ); // NON-NLS
  }

  public ImageIcon getLeftShadowImage() {
    return leftShadowImage;
  }

  public ImageIcon getRightShadowImage() {
    return rightShadowImage;
  }

  public ImageIcon getBottomShadowImage() {
    return bottomShadowImage;
  }

  public ImageIcon getLeftCornerShadowImage() {
    return leftCornerShadowImage;
  }

  public ImageIcon getRightCornerShadowImage() {
    return rightCornerShadowImage;
  }

  public ImageIcon getBackgroundImage() {
    return backgroundImage;
  }

}
