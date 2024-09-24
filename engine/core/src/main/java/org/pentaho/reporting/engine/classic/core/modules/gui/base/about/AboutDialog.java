/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.about;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.libraries.base.versioning.Licenses;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * A dialog that displays information about the demonstration application.
 *
 * @author David Gilbert
 */
public class AboutDialog extends JDialog {

  /**
   * The preferred size for the frame.
   */
  public static final Dimension PREFERRED_SIZE = new Dimension( 560, 360 );

  /**
   * The default border for the panels in the tabbed pane.
   */
  public static final Border STANDARD_BORDER = BorderFactory.createEmptyBorder( 5, 5, 5, 5 );

  /**
   * Localised resources.
   */
  private ResourceBundle resources;

  /**
   * The application name.
   */
  private String application;

  /**
   * The application version.
   */
  private String version;

  /**
   * The copyright string.
   */
  private String copyright;

  /**
   * Other info about the application.
   */
  private String info;

  /**
   * The licence.
   */
  private String licence;

  /**
   * Constructs an about frame.
   *
   * @param title
   *          the frame title.
   * @param project
   *          information about the project.
   */
  public AboutDialog( final String title, final ProjectInformation project ) {

    init( title, project );

  }

  /**
   * Creates a non-modal dialog without a title with the specifed <code>Frame</code> as its owner.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   */
  public AboutDialog( final Frame owner, final String title, final ProjectInformation project ) {
    super( owner );
    init( title, project );
  }

  /**
   * Creates a non-modal dialog without a title with the specifed <code>Dialog</code> as its owner.
   *
   * @param owner
   *          the <code>Dialog</code> from which the dialog is displayed
   */
  public AboutDialog( final Dialog owner, final String title, final ProjectInformation project ) {
    super( owner );
    init( title, project );
  }

  /**
   * Constructs an 'About' frame.
   *
   * @param title
   *          the frame title.
   * @param libraries
   *          a list of libraries.
   */
  private void init( final String title, final ProjectInformation libraries ) {

    setTitle( title );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    this.application = libraries.getName();
    this.version = libraries.getVersion();
    this.copyright = libraries.getCopyright();
    this.info = libraries.getInfo();
    if ( "GPL".equalsIgnoreCase( libraries.getLicenseName() ) ) { // NON-NLS
      this.licence = Licenses.getInstance().getGPL();
    } else if ( "LGPL".equalsIgnoreCase( libraries.getLicenseName() ) ) { // NON-NLS
      this.licence = Licenses.getInstance().getLGPL();
    } else {
      this.licence = libraries.getLicenseName();
    }

    this.resources = ResourceBundle.getBundle( SwingPreviewModule.BUNDLE_NAME );

    final JPanel content = new JPanel( new BorderLayout() );
    content.setBorder( AboutDialog.STANDARD_BORDER );

    final JTabbedPane tabs = createTabs( libraries );
    content.add( tabs );
    setContentPane( content );

    pack();

  }

  /**
   * Returns the preferred size for the about frame.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    return AboutDialog.PREFERRED_SIZE;
  }

  /**
   * Creates a tabbed pane containing an about panel and a system properties panel.
   *
   * @return a tabbed pane.
   */
  private JTabbedPane createTabs( final ProjectInformation info ) {

    final JTabbedPane tabs = new JTabbedPane();

    final JPanel aboutPanel = createAboutPanel( info );
    aboutPanel.setBorder( AboutDialog.STANDARD_BORDER );
    final String aboutTab = this.resources.getString( "about-frame.tab.about" ); // NON-NLS
    tabs.add( aboutTab, aboutPanel );

    final JPanel systemPanel = new SystemPropertiesPanel();
    systemPanel.setBorder( AboutDialog.STANDARD_BORDER );
    final String systemTab = this.resources.getString( "about-frame.tab.system" ); // NON-NLS
    tabs.add( systemTab, systemPanel );

    return tabs;

  }

  /**
   * Creates a panel showing information about the application, including the name, version, copyright notice, URL for
   * further information, and a list of contributors.
   *
   * @return a panel.
   */
  private JPanel createAboutPanel( final ProjectInformation info ) {

    final JPanel about = new JPanel( new BorderLayout() );

    final JPanel details = createAboutTab();

    boolean includetabs = false;
    final JTabbedPane tabs = new JTabbedPane();

    if ( this.licence != null ) {
      final JPanel licencePanel = createLicencePanel();
      licencePanel.setBorder( AboutDialog.STANDARD_BORDER );
      final String licenceTab = this.resources.getString( "about-frame.tab.licence" ); // NON-NLS
      tabs.add( licenceTab, licencePanel );
      includetabs = true;
    }

    if ( info != null ) {
      final JPanel librariesPanel = new LibraryPanel( info );
      librariesPanel.setBorder( AboutDialog.STANDARD_BORDER );
      final String librariesTab = this.resources.getString( "about-frame.tab.libraries" ); // NON-NLS
      tabs.add( librariesTab, librariesPanel );
      includetabs = true;
    }

    about.add( details, BorderLayout.NORTH );
    if ( includetabs ) {
      about.add( tabs );
    }

    return about;

  }

  private JPanel createAboutTab() {
    final JPanel textPanel = new JPanel( new GridLayout( 4, 1, 0, 4 ) );

    final JPanel appPanel = new JPanel();
    final JLabel appLabel = new JLabel( application );
    appLabel.setHorizontalTextPosition( SwingConstants.CENTER );
    appPanel.add( appLabel );

    final JPanel verPanel = new JPanel();
    final JLabel verLabel = new JLabel( version );
    verLabel.setHorizontalTextPosition( SwingConstants.CENTER );
    verPanel.add( verLabel );

    final JPanel copyrightPanel = new JPanel();
    final JLabel copyrightLabel = new JLabel( copyright );
    copyrightLabel.setHorizontalTextPosition( SwingConstants.CENTER );
    copyrightPanel.add( copyrightLabel );

    final JPanel infoPanel = new JPanel();
    final JLabel infoLabel = new JLabel( info );
    infoLabel.setHorizontalTextPosition( SwingConstants.CENTER );
    infoPanel.add( infoLabel );

    textPanel.add( appPanel );
    textPanel.add( verPanel );
    textPanel.add( copyrightPanel );
    textPanel.add( infoPanel );

    return textPanel;
  }

  /**
   * Creates a panel showing the licence.
   *
   * @return a panel.
   */
  private JPanel createLicencePanel() {

    final JPanel licencePanel = new JPanel( new BorderLayout() );
    final JTextArea area = new JTextArea( this.licence );
    area.setLineWrap( true );
    area.setWrapStyleWord( true );
    area.setCaretPosition( 0 );
    area.setEditable( false );
    licencePanel.add( new JScrollPane( area ) );
    return licencePanel;

  }

}
