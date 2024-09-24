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
* Copyright (c) 2002-2019 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.splash;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerInfo;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A splashscreen component that is used to show progress of the application loading.
 * <p/>
 * This component is based completely on awt, ssince this prevents the Swing defaults to be initialized and makes the
 * SplashScreen displayed much faster.
 *
 * @author schmm7
 */
public class SplashScreen extends JWindow {
  private static final int XLOC = 290;
  private static final int YLOC = 158;
  private static final int TEXT_WIDTH = 320;
  private static final int LICENSE_HEIGHT = 30;
  private static final int COPYRIGHT_HEIGHT = 180;
  private static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
  private static final Color WHITE = new Color( 255, 255, 255 );
  private static final Color DARK_GREY = new Color( 65, 65, 65 );
  private static final EmptyBorder BORDER = new EmptyBorder( 0, 0, 0, 0 );

  private class HideOnClickHandler extends MouseAdapter {
    public void mouseClicked( final MouseEvent e ) {
      SplashScreen.this.setVisible( false );
    }
  }

  private JLabel statusLabel;
  private static final Font LICENSE_FONT = new Font( Font.SANS_SERIF, Font.PLAIN, 12 );
  private static final Font COPYRIGHT_FONT = new Font( Font.SANS_SERIF, Font.PLAIN, 10 );

  public SplashScreen() {
    addMouseListener( new HideOnClickHandler() );

    statusLabel = new JLabel();
    statusLabel.setFont( LICENSE_FONT );
    statusLabel.setHorizontalAlignment( SwingConstants.LEADING );
    statusLabel.setOpaque( false );
    statusLabel.setForeground( DARK_GREY );

    final JPanel statusVersionPanel = new JPanel( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.5;
    c.insets = new Insets( 0, XLOC, 30, 0 );
    c.anchor = GridBagConstraints.LAST_LINE_START;
    statusVersionPanel.setOpaque( false );
    statusVersionPanel.setBackground( TRANSPARENT );
    statusVersionPanel.setBorder( BORDER );
    statusVersionPanel.add( statusLabel, c );

    final JPanel splashPanel = createSplashPanel();
    splashPanel.add( statusVersionPanel, BorderLayout.SOUTH );

    setContentPane( splashPanel );

    setSize( splashPanel.getPreferredSize() );
    SwingUtil.centerFrameOnScreen( this );
  }

  public static JPanel createSplashPanel() {
    final ImageIcon picture = IconLoader.getInstance().getAboutDialogPicture();

    // Create the image panel
    final JPanel imagePanel = new JPanel( new BorderLayout() );
    imagePanel.setUI( new BackgroundUI( picture ) );
    imagePanel.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY ) );

    // Overlay the version
    final JLabel versionLabel = new JLabel();
    final String buildString = ReportDesignerInfo.getInstance().getVersion();
    if ( buildString == null ) {
      versionLabel.setText( Messages.getString( "SplashScreen.DevelopmentVersion" ) );
    } else {
      versionLabel.setText( buildString );
    }
    versionLabel.setText( Messages.getString( "SplashScreen.Version", versionLabel.getText() ) );
    versionLabel.setFont( new Font( Font.SANS_SERIF, Font.BOLD, 14 ) );
    versionLabel.setOpaque( false );
    versionLabel.setBackground( TRANSPARENT );
    versionLabel.setForeground( DARK_GREY );
    versionLabel.setBorder( BORDER );
    versionLabel.setBounds( XLOC, YLOC, TEXT_WIDTH, versionLabel.getPreferredSize().height );

    // Overlay the license
    final String year = new SimpleDateFormat( "yyyy" ).format( new Date() );
    final JTextArea copyrightArea = new JTextArea( Messages.getString( "SplashScreen.Copyright", year ) );
    copyrightArea.setEditable( false );
    copyrightArea.setBounds( XLOC, YLOC + 20, TEXT_WIDTH, LICENSE_HEIGHT );
    copyrightArea.setOpaque( false );
    copyrightArea.setLineWrap( true );
    copyrightArea.setWrapStyleWord( true );
    copyrightArea.setFont( COPYRIGHT_FONT );
    copyrightArea.setEnabled( false );
    copyrightArea.setBackground( TRANSPARENT );
    copyrightArea.setForeground( DARK_GREY );
    copyrightArea.setBorder( BORDER );
    copyrightArea.setDisabledTextColor( copyrightArea.getForeground() );

    // Overlay the copyright
    final JTextArea licenseArea = new JTextArea( Messages.getString( "SplashScreen.License" ) );
    licenseArea.setEditable( false );
    licenseArea.setBounds( XLOC, YLOC + 14 + LICENSE_HEIGHT, TEXT_WIDTH, COPYRIGHT_HEIGHT );
    licenseArea.setOpaque( false );
    licenseArea.setLineWrap( true );
    licenseArea.setWrapStyleWord( true );
    licenseArea.setFont( LICENSE_FONT );
    licenseArea.setEnabled( false );
    licenseArea.setBackground( TRANSPARENT );
    licenseArea.setBorder( BORDER );
    licenseArea.setDisabledTextColor( copyrightArea.getForeground() );

    // Add all the overlays
    final JPanel imagePanelOverlay = new JPanel( null );
    imagePanelOverlay.setOpaque( false );
    imagePanelOverlay.add( versionLabel );
    imagePanelOverlay.add( copyrightArea );
    imagePanelOverlay.add( licenseArea );
    imagePanelOverlay.setBackground( TRANSPARENT );

    imagePanel.add( imagePanelOverlay );
    imagePanel.setPreferredSize( new Dimension( picture.getIconWidth(), picture.getIconHeight() ) );

    return imagePanel;
  }

  public void setStatus( final String status ) {
    this.statusLabel.setText( status );
    this.statusLabel.repaint();
  }

}
