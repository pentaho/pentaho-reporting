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

package org.pentaho.reporting.designer.core.welcome;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.global.NewReportAction;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.HyperLink;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.widgets.HyperlinkHandler;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.AlphaComposite;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class WelcomePane extends JDialog {
  private class TriggerShowWelcomePaneAction implements ActionListener {
    private TriggerShowWelcomePaneAction() {
    }

    public void actionPerformed( final ActionEvent evt ) {
      WorkspaceSettings.getInstance().setShowLauncher( showOnStartupCheckbox.isSelected() );
    }
  }


  /**
   * @author wseyler
   */
  public class CloseActionListener extends AbstractAction {
    public CloseActionListener() {
    }

    /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }

  }

  private class TransparentButton extends JButton {
    public TransparentButton() {
      this( null );
    }

    public TransparentButton( final String text ) {
      super( text );
      setOpaque( false );
      setBackground( new Color( 0, 0, 0, 0 ) );
      setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    }

    public void paint( final Graphics g ) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.05f ) );
      super.paint( g2 );
      g2.dispose();
    }
  }

  private JCheckBox showOnStartupCheckbox;
  private ReportDesignerContext reportDesignerContext;
  private NewReportAction newReportAction;
  private CloseActionListener closeActionListener;
  private Image backgroundImage;


  public WelcomePane( final JFrame frame, final ReportDesignerContext reportDesignerContext ) {
    super( frame );
    init( reportDesignerContext );
  }

  public WelcomePane( final JDialog dialog, final ReportDesignerContext reportDesignerContext ) {
    super( dialog );
    init( reportDesignerContext );
  }

  public WelcomePane( final ReportDesignerContext reportDesignerContext ) {
    init( reportDesignerContext );
  }

  private void init( final ReportDesignerContext reportDesignerContext ) {
    if ( reportDesignerContext == null ) {
      throw new NullPointerException();
    }

    setTitle( Messages.getString( "WelcomePane.title" ) ); // NON-NLS
    this.reportDesignerContext = reportDesignerContext;

    this.newReportAction = new NewReportAction();
    this.newReportAction.setReportDesignerContext( reportDesignerContext );

    this.closeActionListener = new CloseActionListener();

    showOnStartupCheckbox = new JCheckBox(
      Messages.getString( "WelcomePane.showAtStartup" ), WorkspaceSettings.getInstance().isShowLauncher() ); // NON-NLS
    showOnStartupCheckbox.addActionListener( new TriggerShowWelcomePaneAction() );

    backgroundImage = Toolkit.getDefaultToolkit().createImage(
      IconLoader.class.getResource( "/org/pentaho/reporting/designer/core/icons/WelcomeBackground.png" ) ); // NON-NLS

    final WaitingImageObserver obs = new WaitingImageObserver( backgroundImage );
    obs.waitImageLoaded();

    setResizable( false );

    initGUI();

    pack();


    final JComponent contentPane = (JComponent) getContentPane();
    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "cancel", new CloseActionListener() ); // NON-NLS

  }

  protected ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  private void initGUI() {
    final JPanel buttonPane = createButtonsPane();
    final JPanel sidePane = createSidePane();

    final JPanel contentPane = new ImagePanel( backgroundImage, false, false );
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( sidePane, BorderLayout.CENTER );
    contentPane.add( buttonPane, BorderLayout.WEST );
    setContentPane( contentPane );
  }

  private JPanel createSidePane() {
    final int buttonPaneWidth = backgroundImage.getWidth( null );
    final int buttonPaneHeight = backgroundImage.getHeight( null );

    final TreeModel sampleTreeModel = SamplesTreeBuilder.getSampleTreeModel();
    final FilesTree tree = new FilesTree( sampleTreeModel, reportDesignerContext, this );
    final JScrollPane scrollPane = new JScrollPane( tree );
    scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
    scrollPane.getViewport().setBackground( Color.white );
    scrollPane.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );

    final JPanel sidePane = new JPanel();
    sidePane.setOpaque( false );
    sidePane.setBackground( new Color( 0, 0, 0, 0 ) );
    sidePane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    sidePane.setLayout( new GridBagLayout() );
    sidePane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    sidePane.setMinimumSize( new Dimension( buttonPaneWidth - 514, buttonPaneHeight ) );
    sidePane.setPreferredSize( new Dimension( buttonPaneWidth - 514, buttonPaneHeight ) );
    sidePane.setMaximumSize( new Dimension( buttonPaneWidth - 514, buttonPaneHeight ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sidePane.add( new JLabel( Messages.getString( "WelcomePane.samples" ) ), gbc );  // Add the Label

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    sidePane.add( scrollPane, gbc );

    // Add the resources panel
    final JPanel onlineResourcesList = new JPanel( new GridLayout( 4, 1 ) );
    onlineResourcesList.setOpaque( false );
    onlineResourcesList.setBackground( new Color( 0, 0, 0, 0 ) );
    onlineResourcesList.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    onlineResourcesList.add( new JLabel( Messages.getString( "WelcomePane.resources" ) ) );
    onlineResourcesList
      .add( createLink( Messages.getString( "WelcomePane.forums" ), Messages.getString( "WelcomePane.url.forums" ) ) );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.insets = new Insets( 20, 0, 20, 0 );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sidePane.add( onlineResourcesList, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sidePane.add( showOnStartupCheckbox, gbc );

    return sidePane;
  }

  private JPanel createButtonsPane() {
    final int buttonPaneHeight = backgroundImage.getHeight( null );
    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout( null );
    buttonPane.setOpaque( false );
    buttonPane.setBackground( new Color( 0, 0, 0, 0 ) );
    buttonPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    buttonPane.setMinimumSize( new Dimension( 514, buttonPaneHeight ) );
    buttonPane.setMaximumSize( new Dimension( 514, buttonPaneHeight ) );
    buttonPane.setPreferredSize( new Dimension( 514, buttonPaneHeight ) );

    try {
      final Class wizardClass =
        Class.forName( "org.pentaho.reporting.designer.extensions.wizard.NewWizardReportAction" );
      final AbstractDesignerContextAction newWizardActionListener =
        (AbstractDesignerContextAction) wizardClass.newInstance();
      newWizardActionListener.setReportDesignerContext( reportDesignerContext );
      final JButton wizardBtn = new TransparentButton();
      wizardBtn.addActionListener( newWizardActionListener );
      wizardBtn.addActionListener( closeActionListener );
      wizardBtn.setBorderPainted( true );
      wizardBtn.setBounds( 117, 137, 100, 118 );
      buttonPane.add( wizardBtn );

      final JLabel wizardLabel =
        new JLabel( newWizardActionListener.getValue( "WIZARD.BUTTON.TEXT" ).toString(), JLabel.CENTER ); //NON-NLS
      wizardLabel.setBounds( 80, 273, 165, 56 );
      buttonPane.add( wizardLabel );

      final JButton wizardLabelBtn = new TransparentButton();
      wizardLabelBtn.addActionListener( newWizardActionListener );
      wizardLabelBtn.addActionListener( closeActionListener );
      wizardLabelBtn.setBorderPainted( true );
      wizardLabelBtn.setBounds( 80, 273, 165, 56 );
      buttonPane.add( wizardLabelBtn );
    } catch ( Exception e ) {
      // todo: Remove me. Replace the code with a real extension mechanism
    }

    // Adds the new (blank) report button
    final JButton newReportBtn = new TransparentButton();
    newReportBtn.addActionListener( newReportAction );
    newReportBtn.addActionListener( closeActionListener );
    newReportBtn.setBorderPainted( true );
    newReportBtn.setBounds( 323, 137, 100, 118 );
    buttonPane.add( newReportBtn );

    final JLabel newReportLabel = new JLabel( Messages.getString( "WelcomePane.newReportLabel" ), JLabel.CENTER );
    newReportLabel.setBounds( 285, 273, 165, 56 );
    buttonPane.add( newReportLabel );

    final JButton newReportLabelBtn = new TransparentButton();
    newReportLabelBtn.addActionListener( newReportAction );
    newReportLabelBtn.addActionListener( closeActionListener );
    newReportLabelBtn.setBorderPainted( true );
    newReportLabelBtn.setBounds( 285, 273, 165, 56 );
    buttonPane.add( newReportLabelBtn );
    return buttonPane;
  }

  /**
   * Creates a HyperLink label and attaches a URL click event.
   *
   * @param lbl  the link text presented to the user
   * @param link the URL to which the hyperlink points
   * @return the created hyperlink object.
   */
  public HyperLink createLink( final String lbl, final String link ) {
    final HyperLink linkLbl = new HyperLink( lbl );
    linkLbl.addMouseListener( new HyperlinkHandler( link, this ) );
    return linkLbl;
  }

}
