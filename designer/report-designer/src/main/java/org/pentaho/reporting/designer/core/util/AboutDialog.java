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

import org.pentaho.reporting.designer.core.splash.SplashScreen;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AboutDialog extends JDialog {
  private class OKAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private OKAction() {
      putValue( Action.NAME, UtilMessages.getInstance().getString( "AboutDialog.OKAction" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }
  }

  private class DisposeOnClickHandler extends MouseAdapter {

    public void mouseClicked( final MouseEvent e ) {
      dispose();
    }


    public void mousePressed( final MouseEvent e ) {
      dispose();
    }


    public void mouseReleased( final MouseEvent e ) {
      dispose();
    }
  }

  /**
   * Creates a new modal About-Dialog with the given title.
   */
  public AboutDialog() {
    setModal( true );
    init();
  }

  public AboutDialog( final Frame owner )
    throws HeadlessException {
    super( owner, true );
    init();
  }

  public AboutDialog( final Dialog owner )
    throws HeadlessException {
    super( owner, true );
    init();
  }

  private void init() {
    setTitle( UtilMessages.getInstance().getString( "AboutDialog.Title" ) );
    addMouseListener( new DisposeOnClickHandler() );

    final JPanel imagePanel = new JPanel( new BorderLayout() );
    imagePanel.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY ) );

    final JPanel aboutImage = SplashScreen.createSplashPanel();
    imagePanel.add( aboutImage, BorderLayout.CENTER );

    setSize( aboutImage.getPreferredSize() );

    setContentPane( imagePanel );

    getRootPane().getInputMap().put( KeyStroke.getKeyStroke( "ESCAPE" ), "close" );//NON-NLS
    getRootPane().getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ), "close" );//NON-NLS
    getRootPane().getActionMap().put( "close", new OKAction() );

    setUndecorated( true );

    pack();
    setResizable( false );
    LibSwingUtil.centerDialogInParent( this );
    setVisible( true );
  }
}
