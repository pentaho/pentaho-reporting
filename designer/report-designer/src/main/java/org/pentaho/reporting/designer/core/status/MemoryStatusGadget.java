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

package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

import javax.swing.*;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class MemoryStatusGadget extends JLabel {

  private static final Color BG = SystemColor.textHighlight;

  private double tm;
  private double fm;

  private Timer timer;

  public MemoryStatusGadget() {
    if ( MacOSXIntegration.MAC_OS_X ) {
      setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 10 ),
        BorderFactory.createLineBorder( SystemColor.controlShadow ) ) );
    } else {
      setBorder( BorderFactory.createLineBorder( SystemColor.controlShadow ) );
    }
    setFont(
      StyleContext.getDefaultStyleContext().getFont( getFont().getName(), Font.PLAIN, getFont().getSize() - 2 ) );
    setHorizontalAlignment( JLabel.CENTER );

    timer = new Timer( 500, new MemoryStatusUpdateAction() );
    timer.setRepeats( true );
    timer.start();

    addMouseListener( new GarbageCollectorAction() );
  }


  @Override
  protected void paintComponent( final Graphics g ) {
    final Color origColor = g.getColor();

    g.setColor( SystemColor.control );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    final int w = (int) ( getWidth() * ( ( tm - fm ) / tm ) );
    g.setColor( BG );
    g.fillRect( 0, 0, w, getHeight() );

    g.setColor( origColor );
    super.paintComponent( g );
  }


  public void dispose() {
    timer.stop();
  }

  private static class GarbageCollectorAction extends MouseAdapter {
    @Override
    public void mouseClicked( final MouseEvent e ) {
      System.gc();
    }
  }

  private class MemoryStatusUpdateAction implements ActionListener {
    public void actionPerformed( final ActionEvent e ) {
      final long totalMemory = Runtime.getRuntime().totalMemory();
      final long freeMemory = Runtime.getRuntime().freeMemory();
      tm = ( totalMemory / ( 1024. * 1024 ) );
      fm = ( freeMemory / ( 1024. * 1024 ) );

      setText( Messages.getString( "MemoryStatusGadget.Text", Double.valueOf( tm - fm ), Double.valueOf( tm ) ) );
    }
  }

}
