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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HyperLink extends JLabel {
  private class HyperlinkMouseHandler extends MouseAdapter {
    public void mouseEntered( final MouseEvent evt ) {
      setForeground( Color.black );
      setCursor( new Cursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( final MouseEvent evt ) {
      setForeground( Color.blue );
      setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }
  }

  public HyperLink( final String linkName ) {
    super( String.format( "<html><u>%s</u></html>", linkName ) ); // NON-NLS
    this.addMouseListener( new HyperlinkMouseHandler() );
    this.setForeground( Color.blue );
    this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

  }

  public HyperLink( final String lbl, final ImageIcon icon ) {
    this( lbl );
    this.setIcon( icon );
  }
}
