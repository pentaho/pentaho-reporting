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


package org.pentaho.reporting.designer.core.widgets;

import org.pentaho.reporting.designer.core.util.CanvasImageLoader;

import javax.swing.*;
import java.awt.*;

public class FancyTabbedPane extends JTabbedPane {
  /**
   * Creates an empty <code>TabbedPane</code> with a default tab placement of <code>JTabbedPane.TOP</code>.
   *
   * @see #addTab
   */
  public FancyTabbedPane() {
  }

  protected void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( getTabCount() == 0 ) {
      final Image img = CanvasImageLoader.getInstance().getBackgroundImage().getImage();
      g.drawImage( img, 0, 0, this );
    }
  }
}
