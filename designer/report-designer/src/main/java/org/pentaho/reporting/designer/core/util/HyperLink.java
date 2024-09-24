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
