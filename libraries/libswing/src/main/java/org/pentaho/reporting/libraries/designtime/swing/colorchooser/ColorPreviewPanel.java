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

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import java.awt.*;

public class ColorPreviewPanel extends JComponent {
  private Color previous;
  private Color current;

  public ColorPreviewPanel() {
    previous = Color.white;
    current = Color.white;
  }

  public Color getPrevious() {
    return previous;
  }

  public void setPrevious( final Color previous ) {
    if ( previous == null ) {
      throw new NullPointerException();
    }
    this.previous = previous;
    repaint();
  }

  public Color getCurrent() {
    return current;
  }

  public void setCurrent( final Color current ) {
    if ( current == null ) {
      throw new NullPointerException();
    }
    this.current = current;
    repaint();
  }

  public Dimension getPreferredSize() {
    return new Dimension( 60, 60 );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 60, 60 );
  }

  protected void paintComponent( final Graphics g ) {
    final Graphics graphics = g.create();
    graphics.setColor( current );
    graphics.fillRect( 0, 0, getWidth(), getHeight() / 2 );
    graphics.setColor( previous );
    graphics.fillRect( 0, getHeight() / 2, getWidth(), getHeight() / 2 );
    graphics.dispose();
  }
}
