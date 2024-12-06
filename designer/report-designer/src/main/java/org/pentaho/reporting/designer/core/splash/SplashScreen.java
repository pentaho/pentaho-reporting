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


package org.pentaho.reporting.designer.core.splash;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.i18n.BaseMessages;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
  private static final Log log = LogFactory.getLog(SplashScreen.class);

  private static final int XLOC = 300;
  private static final int YLOC = 80;
  private static final int TEXT_WIDTH = 700;
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
  private static final Font COPYRIGHT_FONT = new Font( Font.SANS_SERIF, Font.PLAIN, 12 );

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
    copyrightArea.setBounds( XLOC, YLOC + 25, TEXT_WIDTH, 25 );
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
    StringBuilder sb = new StringBuilder();
    String line;
    try {
        BufferedReader reader =
                new BufferedReader( new InputStreamReader( SplashScreen.class.getClassLoader().getResourceAsStream(
                        "org/pentaho/reporting/designer/core/license/license.txt" ) ) );
        while ( ( line = reader.readLine() ) != null ) {
          sb.append( line + System.getProperty( "line.separator" ) );
        }
    } catch ( Exception ex ) {
      log.error( Messages.getString( "SplashDialog.LicenseTextNotFound" ), ex );
    }
    final JTextArea licenseArea = new JTextArea( sb.toString() );
    licenseArea.setEditable( false );
    licenseArea.setBounds( XLOC, YLOC + 50, TEXT_WIDTH, 800 );
    licenseArea.setOpaque( false );
    licenseArea.setLineWrap( true );
    licenseArea.setWrapStyleWord( true );
    licenseArea.setFont( new Font( "Monospaced", Font.PLAIN, 9 ) );
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
