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
