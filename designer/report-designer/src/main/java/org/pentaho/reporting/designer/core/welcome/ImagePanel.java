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

package org.pentaho.reporting.designer.core.welcome;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class ImagePanel extends JPanel {
  private boolean scaleX;
  private boolean scaleY;
  private Image image;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public ImagePanel( final Image image, final boolean scaleX, final boolean scaleY ) {
    this.image = image;
    this.scaleX = scaleX;
    this.scaleY = scaleY;
  }

  public void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( image == null ) {
      return;
    }

    if ( scaleX && scaleY ) {
      g.drawImage( image, 0, 0, getWidth(), getHeight(), this );
    } else if ( scaleX ) {
      g.drawImage( image, 0, 0, getWidth(), image.getHeight( this ), this );
    } else if ( scaleY ) {
      g.drawImage( image, 0, 0, image.getWidth( this ), getHeight(), this );
    } else {
      g.drawImage( image, 0, 0, image.getWidth( this ), image.getHeight( this ), this );
    }
  }
}
