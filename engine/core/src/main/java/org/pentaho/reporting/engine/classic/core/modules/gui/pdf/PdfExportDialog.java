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

package org.pentaho.reporting.engine.classic.core.modules.gui.pdf;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.AbstractExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.GuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.EncodingComboBoxModel;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * Creation-Date: 02.12.2006, 15:27:30
 *
 * @author Thomas Morgner
 */
public class PdfExportDialog extends AbstractExportDialog {
  private static final Log logger = LogFactory.getLog( PdfExportDialog.class );

  /**
   * Useful constant.
   */
  private static final int CBMODEL_NOPRINTING = 0;

  /**
   * Useful constant.
   */
  private static final int CBMODEL_DEGRADED = 1;

  /**
   * Useful constant.
   */
  private static final int CBMODEL_FULL = 2;

  /**
   * Internal action class to enable/disable the Security-Settings panel. Without encryption a pdf file cannot have any
   * security settings enabled.
   */
  private class ActionSecuritySelection extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ActionSecuritySelection() {
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      updateSecurityPanelEnabled();
    }
  }

  /**
   * Internal action class to select a target file.
   */
  private class ActionSelectFile extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ActionSelectFile( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "pdfsavedialog.selectFile" ) ); //$NON-NLS-1$
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      performSelectFile();
    }
  }

  /**
   * Security (none) radio button.
   */
  private JRadioButton rbSecurityNone;

  /**
   * Security (40 bit) radio button.
   */
  private JRadioButton rbSecurity40Bit;

  /**
   * Security (128 bit) radio button.
   */
  private JRadioButton rbSecurity128Bit;

  /**
   * User password text field.
   */
  private JTextField txUserPassword;

  /**
   * Owner password text field.
   */
  private JTextField txOwnerPassword;

  /**
   * Confirm user password text field.
   */
  private JTextField txConfUserPassword;

  /**
   * Confirm ownder password text field.
   */
  private JTextField txConfOwnerPassword;

  /**
   * Allow copy check box.
   */
  private JCheckBox cxAllowCopy;

  /**
   * Allow screen readers check box.
   */
  private JCheckBox cxAllowScreenReaders;

  /**
   * Allow printing check box.
   */
  private JComboBox cbAllowPrinting;

  /**
   * Allow assembly check box.
   */
  private JCheckBox cxAllowAssembly;

  /**
   * Allow modify contents check box.
   */
  private JCheckBox cxAllowModifyContents;

  /**
   * Allow modify annotations check box.
   */
  private JCheckBox cxAllowModifyAnnotations;

  /**
   * Allow fill in check box.
   */
  private JCheckBox cxAllowFillIn;

  /**
   * A model for the available encodings.
   */
  private EncodingComboBoxModel encodingModel;

  /**
   * A file chooser.
   */
  private JFileChooser fileChooser;

  /**
   * Title text field.
   */
  private JTextField txTitle;

  /**
   * Author text field.
   */
  private JTextField txAuthor;

  private static final String PDF_FILE_EXTENSION = ".pdf"; //$NON-NLS-1$
  private JStatusBar statusBar;
  private JTextField txFilename;
  private DefaultComboBoxModel printingModel;
  private JComboBox cbEncoding;
  private JCheckBox cxEmbedded;
  private JTextField txKeywords;
  private JTextField txDescription;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner. A shared, hidden frame
   * will be set as the owner of the dialog.
   */
  public PdfExportDialog() {
    initializeComponents();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   */
  public PdfExportDialog( final Frame owner ) {
    super( owner );
    initializeComponents();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   */
  public PdfExportDialog( final Dialog owner ) {
    super( owner );
    initializeComponents();
  }

  private JPanel createMetaDataPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    final JLabel lblAuthor = new JLabel( getResources().getString( "pdfsavedialog.author" ) ); //$NON-NLS-1$
    final JLabel lblTitel = new JLabel( getResources().getString( "pdfsavedialog.title" ) ); //$NON-NLS-1$
    final JLabel lblKeywords = new JLabel( getResources().getString( "pdfsavedialog.keywords" ) ); //$NON-NLS-1$
    final JLabel lblDescription = new JLabel( getResources().getString( "pdfsavedialog.description" ) ); //$NON-NLS-1$

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( lblTitel, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblAuthor, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblKeywords, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblDescription, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( txTitle, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txAuthor, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txKeywords, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txDescription, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;
  }

  private void initializeComponents() {
    setTitle( getResources().getString( "pdfsavedialog.dialogtitle" ) ); //$NON-NLS-1$

    txAuthor = new JTextField();
    txAuthor.setColumns( 40 );
    txTitle = new JTextField();
    txTitle.setColumns( 40 );
    txKeywords = new JTextField();
    txKeywords.setColumns( 40 );
    txDescription = new JTextField();
    txDescription.setColumns( 40 );

    rbSecurityNone = new JRadioButton( getResources().getString( "pdfsavedialog.securityNone" ) ); //$NON-NLS-1$
    rbSecurity40Bit = new JRadioButton( getResources().getString( "pdfsavedialog.security40bit" ) ); //$NON-NLS-1$
    rbSecurity128Bit = new JRadioButton( getResources().getString( "pdfsavedialog.security128bit" ) ); //$NON-NLS-1$

    final Action securitySelectAction = new ActionSecuritySelection();
    rbSecurityNone.addActionListener( securitySelectAction );
    rbSecurity40Bit.addActionListener( securitySelectAction );
    rbSecurity128Bit.addActionListener( securitySelectAction );

    rbSecurity128Bit.setSelected( true );

    txUserPassword = new JPasswordField();
    txConfUserPassword = new JPasswordField();
    txOwnerPassword = new JPasswordField();
    txConfOwnerPassword = new JPasswordField();

    cxAllowCopy = new JCheckBox( getResources().getString( "pdfsavedialog.allowCopy" ) ); //$NON-NLS-1$
    cbAllowPrinting = new JComboBox( getPrintingComboBoxModel() );
    cxAllowScreenReaders = new JCheckBox( getResources().getString( "pdfsavedialog.allowScreenreader" ) ); //$NON-NLS-1$

    cxAllowAssembly = new JCheckBox( getResources().getString( "pdfsavedialog.allowAssembly" ) ); //$NON-NLS-1$
    cxAllowModifyContents = new JCheckBox( getResources().getString( "pdfsavedialog.allowModifyContents" ) ); //$NON-NLS-1$
    cxAllowModifyAnnotations = new JCheckBox( getResources().getString( "pdfsavedialog.allowModifyAnnotations" ) ); //$NON-NLS-1$
    cxAllowFillIn = new JCheckBox( getResources().getString( "pdfsavedialog.allowFillIn" ) ); //$NON-NLS-1$

    txFilename = new JTextField();
    txFilename.setColumns( 40 );
    statusBar = new JStatusBar();

    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault() );
    encodingModel.sort();

    cbEncoding = new JComboBox( encodingModel );
    cxEmbedded = new JCheckBox( getResources().getString( "pdfsavedialog.embedfonts" ) );

    getFormValidator().registerTextField( txFilename );
    getFormValidator().registerTextField( txConfOwnerPassword );
    getFormValidator().registerTextField( txConfUserPassword );
    getFormValidator().registerTextField( txUserPassword );
    getFormValidator().registerTextField( txOwnerPassword );

    final JPanel exportPane = createExportPanel();
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final boolean advancedSettingsTabAvail =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.AdvancedSettingsAvailable" ) );
    final boolean metaDataSettingsTabAvail =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.MetaDataSettingsAvailable" ) );
    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( getResources().getString( "pdfsavedialog.export-settings" ), exportPane ); //$NON-NLS-1$
    tabbedPane.add( getResources().getString( "pdfsavedialog.parameters" ), getParametersPanel() );

    if ( metaDataSettingsTabAvail ) {
      tabbedPane.add( getResources().getString( "pdfsavedialog.metadata-settings" ), createMetaDataPanel() ); //$NON-NLS-1$
    }
    if ( advancedSettingsTabAvail ) {
      tabbedPane.add( getResources().getString( "pdfsavedialog.advanced-settings" ), createAdvancedPanel() ); //$NON-NLS-1$
    }
    setContentPane( createContentPane( tabbedPane ) );

  }

  private JPanel createExportPanel() {
    final JButton btnSelect = new JButton( new ActionSelectFile( getResources() ) );
    final JLabel lblFileName = new JLabel( getResources().getString( "pdfsavedialog.filename" ) ); //$NON-NLS-1$
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout( new GridBagLayout() );
    mainPanel.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 3, 1, 1 );
    mainPanel.add( lblFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.ipadx = 120;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    mainPanel.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 2;
    gbc.gridy = 0;
    mainPanel.add( btnSelect, gbc );

    final JPanel advancedPaneCarrier = new JPanel();
    advancedPaneCarrier.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
    advancedPaneCarrier.setLayout( new BorderLayout() );
    advancedPaneCarrier.add( mainPanel, BorderLayout.NORTH );
    return advancedPaneCarrier;
  }

  public JStatusBar getStatusBar() {
    return statusBar;
  }

  protected boolean performConfirm() {
    final String filename = txFilename.getText();
    final File f = new File( filename );
    if ( f.exists() ) {
      final String key1 = "pdfsavedialog.targetOverwriteConfirmation"; //$NON-NLS-1$
      final String key2 = "pdfsavedialog.targetOverwriteTitle"; //$NON-NLS-1$
      if ( JOptionPane.showConfirmDialog( this, MessageFormat.format( getResources().getString( key1 ),
          new Object[] { txFilename.getText() } ), getResources().getString( key2 ), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE ) == JOptionPane.NO_OPTION ) {
        return false;
      }
    }

    if ( getEncryptionValue().equals( PdfExportGUIModule.SECURITY_ENCRYPTION_128BIT )
        || getEncryptionValue().equals( PdfExportGUIModule.SECURITY_ENCRYPTION_40BIT ) ) {
      if ( txOwnerPassword.getText().trim().length() == 0 ) {
        if ( JOptionPane.showConfirmDialog( this, getResources().getString( "pdfsavedialog.ownerpasswordEmpty" ), //$NON-NLS-1$
            getResources().getString( "pdfsavedialog.warningTitle" ), //$NON-NLS-1$
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.NO_OPTION ) {
          return false;
        }
      }
    }
    return true;
  }

  protected boolean performValidate() {
    getStatusBar().clear();

    final String filename = txFilename.getText();
    if ( filename.trim().length() == 0 ) {
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "pdfsavedialog.targetIsEmpty" ) ); //$NON-NLS-1$
      return false;
    }
    final File f = new File( filename );
    if ( f.exists() ) {
      if ( f.isFile() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "pdfsavedialog.targetIsNoFile" ) ); //$NON-NLS-1$
        return false;
      }
      if ( f.canWrite() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "pdfsavedialog.targetIsNotWritable" ) ); //$NON-NLS-1$
        return false;
      }

      final String message = MessageFormat.format( getResources().getString( "pdfsavedialog.targetOverwriteWarning" ), //$NON-NLS-1$
          new Object[] { filename } );
      getStatusBar().setStatus( StatusType.WARNING, message );
    }

    if ( getEncryptionValue().equals( PdfExportGUIModule.SECURITY_ENCRYPTION_128BIT )
        || getEncryptionValue().equals( PdfExportGUIModule.SECURITY_ENCRYPTION_40BIT ) ) {
      if ( txUserPassword.getText().equals( txConfUserPassword.getText() ) == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "pdfsavedialog.userpasswordNoMatch" ) ); //$NON-NLS-1$
        return false;
      }
      if ( txOwnerPassword.getText().equals( txConfOwnerPassword.getText() ) == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "pdfsavedialog.ownerpasswordNoMatch" ) ); //$NON-NLS-1$
        return false;
      }
    }

    return true;
  }

  protected void initializeFromJob( final MasterReport job, final GuiContext guiContext ) {
    statusBar.setIconTheme( guiContext.getIconTheme() );

    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault() );
    encodingModel.sort();
    cbEncoding.setModel( encodingModel );
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.pdf."; //$NON-NLS-1$
  }

  /**
   * Returns a new (and not connected to the default config from the job) configuration containing all properties from
   * the dialog.
   *
   * @param full
   */
  protected Configuration grabDialogContents( final boolean full ) {
    final DefaultConfiguration config = new DefaultConfiguration();

    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Keywords", //$NON-NLS-1$
        txKeywords.getText() );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Description", //$NON-NLS-1$
        txDescription.getText() );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Author", //$NON-NLS-1$
        txAuthor.getText() );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Title", //$NON-NLS-1$
        txTitle.getText() );

    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.TargetFileName", //$NON-NLS-1$
        txFilename.getText() );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encoding", //$NON-NLS-1$
        encodingModel.getSelectedEncoding() );

    config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PrintLevel", //$NON-NLS-1$
        getPrintLevel() );
    config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encryption", //$NON-NLS-1$
        getEncryptionValue() );
    config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.UserPassword", //$NON-NLS-1$
        txUserPassword.getText() );
    config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.OwnerPassword", //$NON-NLS-1$
        txOwnerPassword.getText() );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowAssembly", //$NON-NLS-1$
        String.valueOf( cxAllowAssembly.isSelected() ) );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowCopy", //$NON-NLS-1$
        String.valueOf( cxAllowCopy.isSelected() ) );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowFillIn", //$NON-NLS-1$
        String.valueOf( cxAllowFillIn.isSelected() ) );
    config.setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyAnnotations", //$NON-NLS-1$
        String.valueOf( cxAllowModifyAnnotations.isSelected() ) );
    config.setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyContents", //$NON-NLS-1$
        String.valueOf( cxAllowModifyContents.isSelected() ) );
    config.setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowScreenReaders", //$NON-NLS-1$
        String.valueOf( cxAllowScreenReaders.isSelected() ) );
    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.EmbedFonts", //$NON-NLS-1$
        String.valueOf( cxEmbedded.isSelected() ) );
    return config;
  }

  protected void setDialogContents( final Configuration config ) {
    txFilename.setText( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.TargetFileName" ) ); //$NON-NLS-1$
    final String encoding =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encoding" ); //$NON-NLS-1$
    if ( encoding != null && encoding.length() > 0 ) {
      encodingModel.setSelectedEncoding( encoding );
    }
    setPrintLevel( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.security.PrintLevel" ) ); //$NON-NLS-1$
    setEncryptionValue( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.security.Encryption" ) ); //$NON-NLS-1$

    txUserPassword.setText( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.UserPassword" ) ); //$NON-NLS-1$
    txOwnerPassword.setText( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.OwnerPassword" ) ); //$NON-NLS-1$
    txConfUserPassword.setText( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.UserPassword" ) ); //$NON-NLS-1$
    txConfOwnerPassword.setText( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.OwnerPassword" ) ); //$NON-NLS-1$

    cxAllowAssembly
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowAssembly" ) ) ); //$NON-NLS-1$
    cxAllowCopy
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowCopy" ) ) ); //$NON-NLS-1$
    cxAllowFillIn
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowFillIn" ) ) ); //$NON-NLS-1$
    cxAllowModifyAnnotations
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyAnnotations" ) ) ); //$NON-NLS-1$
    cxAllowModifyContents
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyContents" ) ) ); //$NON-NLS-1$
    cxAllowScreenReaders
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowScreenReaders" ) ) ); //$NON-NLS-1$
    cxEmbedded
        .setSelected( "true".equals( //$NON-NLS-1$
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.EmbedFonts" ) ) ); //$NON-NLS-1$

    txTitle.setText( config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Title", //$NON-NLS-1$
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Title" ) ) ); //$NON-NLS-1$
    txAuthor.setText( config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Author", //$NON-NLS-1$
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Author" ) ) ); //$NON-NLS-1$
    txKeywords.setText( config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Keywords", //$NON-NLS-1$
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Keywords" ) ) ); //$NON-NLS-1$
    txDescription.setText( config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Description", //$NON-NLS-1$
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Description" ) ) ); //$NON-NLS-1$
  }

  protected String getConfigurationSuffix() {
    return "_pdf_export"; //$NON-NLS-1$
  }

  public void clear() {
    txConfOwnerPassword.setText( "" ); //$NON-NLS-1$
    txConfUserPassword.setText( "" ); //$NON-NLS-1$
    txFilename.setText( "" ); //$NON-NLS-1$
    txOwnerPassword.setText( "" ); //$NON-NLS-1$
    txUserPassword.setText( "" ); //$NON-NLS-1$

    cxAllowAssembly.setSelected( false );
    cxAllowCopy.setSelected( false );
    cbAllowPrinting.setSelectedIndex( PdfExportDialog.CBMODEL_NOPRINTING );
    cxAllowFillIn.setSelected( false );
    cxAllowModifyAnnotations.setSelected( false );
    cxAllowModifyContents.setSelected( false );
    cxAllowScreenReaders.setSelected( false );
    cxEmbedded.setSelected( false );
    rbSecurityNone.setSelected( true );
    updateSecurityPanelEnabled();

    final String plattformDefaultEncoding = EncodingRegistry.getPlatformDefaultEncoding();
    encodingModel.setSelectedEncoding( plattformDefaultEncoding );

    txAuthor.setText( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( "user.name", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    txFilename.setText( "" ); //$NON-NLS-1$
    txTitle.setText( "" ); //$NON-NLS-1$
    txDescription.setText( "" ); //$NON-NLS-1$
    txKeywords.setText( "" ); //$NON-NLS-1$
  }

  protected String getResourceBaseName() {
    return PdfExportPlugin.BASE_RESOURCE_CLASS;
  }

  /**
   * Updates the security panel state. If no encryption is selected, all security setting components will be disabled.
   */
  protected void updateSecurityPanelEnabled() {
    final boolean b = ( rbSecurityNone.isSelected() == false );
    txUserPassword.setEnabled( b );
    txOwnerPassword.setEnabled( b );
    txConfOwnerPassword.setEnabled( b );
    txConfUserPassword.setEnabled( b );
    cxAllowAssembly.setEnabled( b );
    cxAllowCopy.setEnabled( b );
    cbAllowPrinting.setEnabled( b );
    cxAllowFillIn.setEnabled( b );
    cxAllowModifyAnnotations.setEnabled( b );
    cxAllowModifyContents.setEnabled( b );
    cxAllowScreenReaders.setEnabled( b );
  }

  /**
   * Creates a panel for the security settings.
   *
   * @return The panel.
   */
  private JPanel createAdvancedPanel() {
    final JLabel lblEncoding = new JLabel( getResources().getString( "pdfsavedialog.encoding" ) ); //$NON-NLS-1$
    final JPanel encodingPanel = new JPanel();
    encodingPanel.setBorder( BorderFactory.createTitledBorder( getResources()
        .getString( "pdfsavedialog.export-options" ) ) );
    encodingPanel.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    encodingPanel.add( lblEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.ipadx = 80;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    encodingPanel.add( cbEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 80;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    encodingPanel.add( cxEmbedded, gbc );

    final JPanel securityPanel = new JPanel();
    securityPanel.setLayout( new GridBagLayout() );
    securityPanel.setBorder( BorderFactory.createTitledBorder( getResources().getString( "pdfsavedialog.security" ) ) );

    final JLabel lblUserPass = new JLabel( getResources().getString( "pdfsavedialog.userpassword" ) ); //$NON-NLS-1$
    final JLabel lblUserPassConfirm = new JLabel( getResources().getString( "pdfsavedialog.userpasswordconfirm" ) ); //$NON-NLS-1$
    final JLabel lblOwnerPass = new JLabel( getResources().getString( "pdfsavedialog.ownerpassword" ) ); //$NON-NLS-1$
    final JLabel lblOwnerPassConfirm = new JLabel( getResources().getString( "pdfsavedialog.ownerpasswordconfirm" ) ); //$NON-NLS-1$
    final JLabel lbAllowPrinting = new JLabel( getResources().getString( "pdfsavedialog.allowPrinting" ) ); //$NON-NLS-1$

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 4;
    gbc.gridy = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( createSecurityConfigPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( lblUserPass, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.ipadx = 120;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( txUserPassword, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( lblOwnerPass, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.ipadx = 80;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( txOwnerPassword, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( lblUserPassConfirm, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.ipadx = 80;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( txConfUserPassword, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( lblOwnerPassConfirm, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.ipadx = 80;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    securityPanel.add( txConfOwnerPassword, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowCopy, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowScreenReaders, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowFillIn, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowAssembly, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowModifyContents, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cxAllowModifyAnnotations, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( lbAllowPrinting, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add( cbAllowPrinting, gbc );

    final JPanel advancedCarrier2 = new JPanel();
    advancedCarrier2.setLayout( new GridBagLayout() );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 1, 1, 5, 1 );
    advancedCarrier2.add( encodingPanel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    advancedCarrier2.add( securityPanel, gbc );

    final JPanel advancedPaneCarrier = new JPanel();
    advancedPaneCarrier.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
    advancedPaneCarrier.setLayout( new BorderLayout() );
    advancedPaneCarrier.add( advancedCarrier2, BorderLayout.NORTH );
    return advancedPaneCarrier;
  }

  /**
   * Creates the security config panel. This panel is used to select the level of the PDF security.
   *
   * @return the created security config panel.
   */
  private JPanel createSecurityConfigPanel() {
    final JPanel pnlSecurityConfig = new JPanel();
    pnlSecurityConfig.setLayout( new GridLayout() );
    pnlSecurityConfig.add( rbSecurityNone );
    pnlSecurityConfig.add( rbSecurity40Bit );
    pnlSecurityConfig.add( rbSecurity128Bit );

    final ButtonGroup btGrpSecurity = new ButtonGroup();
    btGrpSecurity.add( rbSecurity128Bit );
    btGrpSecurity.add( rbSecurity40Bit );
    btGrpSecurity.add( rbSecurityNone );

    return pnlSecurityConfig;
  }

  /**
   * Gets and initializes the the combobox model for the security setting "allowPrinting".
   *
   * @return the combobox model containing the different values for the allowPrinting option.
   */
  private DefaultComboBoxModel getPrintingComboBoxModel() {
    if ( printingModel == null ) {
      final Object[] data = { getResources().getString( "pdfsavedialog.option.noprinting" ), //$NON-NLS-1$
        getResources().getString( "pdfsavedialog.option.degradedprinting" ), //$NON-NLS-1$
        getResources().getString( "pdfsavedialog.option.fullprinting" ) }; //$NON-NLS-1$
      printingModel = new DefaultComboBoxModel( data );
    }
    return printingModel;
  }

  /**
   * selects a file to use as target for the report processing.
   */
  protected void performSelectFile() {
    // lazy initialize ... the file chooser is one of the hot spots here ...
    if ( fileChooser == null ) {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter =
          new FilesystemFilter( PdfExportDialog.PDF_FILE_EXTENSION, getResources().getString(
              "file.save.pdfdescription" ) ); //$NON-NLS-1$
      fileChooser.addChoosableFileFilter( filter );
      fileChooser.setMultiSelectionEnabled( false );
    }

    final File file = new File( txFilename.getText() );
    fileChooser.setCurrentDirectory( file );
    fileChooser.setSelectedFile( file );
    final int option = fileChooser.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends of pdf
      if ( selFileName.toLowerCase().endsWith( PdfExportDialog.PDF_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + PdfExportDialog.PDF_FILE_EXTENSION;
      }
      txFilename.setText( selFileName );
    }
  }

  /**
   * Defines whether the user is allowed to print the file. If this right is granted, the user is also able to print a
   * degraded version of the file, regardless of the <code>allowDegradedPrinting</code< property. If you disabled
   * printing but enabled degraded printing, then the user is able to print a low-quality version of the document.
   */
  public void setPrintLevel( final String printLevel ) {
    if ( "full".equals( printLevel ) ) { //$NON-NLS-1$
      this.cbAllowPrinting.setSelectedIndex( PdfExportDialog.CBMODEL_FULL );
    } else if ( "degraded".equals( printLevel ) ) { //$NON-NLS-1$
      this.cbAllowPrinting.setSelectedIndex( PdfExportDialog.CBMODEL_DEGRADED );
    } else {
      this.cbAllowPrinting.setSelectedIndex( PdfExportDialog.CBMODEL_NOPRINTING );
    }
  }

  public String getPrintLevel() {
    if ( cbAllowPrinting.getSelectedIndex() == PdfExportDialog.CBMODEL_FULL ) {
      return "full"; //$NON-NLS-1$
    }
    if ( cbAllowPrinting.getSelectedIndex() == PdfExportDialog.CBMODEL_DEGRADED ) {
      return "degraded"; //$NON-NLS-1$
    }
    return "none"; //$NON-NLS-1$
  }

  /**
   * Queries the currently selected encryption. If an encryption is selected this method returns either Boolean.TRUE or
   * Boolean.FALSE, when no encryption is set, <code>null</code> is returned. If no encryption is set, the security
   * properties have no defined state.
   *
   * @return the selection state for the encryption. If no encryption is set, this method returns null, if 40-bit
   *         encryption is set, the method returns Boolean.FALSE and on 128-Bit-encryption, Boolean.TRUE is returned.
   */
  public String getEncryptionValue() {
    if ( rbSecurity40Bit.isSelected() ) {
      return PdfExportGUIModule.SECURITY_ENCRYPTION_40BIT;
    }
    if ( rbSecurity128Bit.isSelected() ) {
      return PdfExportGUIModule.SECURITY_ENCRYPTION_128BIT;
    }
    return PdfExportGUIModule.SECURITY_ENCRYPTION_NONE;
  }

  /**
   * Defines the currently selected encryption.
   *
   * @param b
   *          the new encryption state, one of null, Boolean.TRUE or Boolean.FALSE
   */
  public void setEncryptionValue( final String b ) {
    if ( b != null ) {
      if ( b.equals( PdfExportGUIModule.SECURITY_ENCRYPTION_128BIT ) ) {
        rbSecurity128Bit.setSelected( true );
        updateSecurityPanelEnabled();
        return;
      } else if ( b.equals( PdfExportGUIModule.SECURITY_ENCRYPTION_40BIT ) ) {
        rbSecurity40Bit.setSelected( true );
        updateSecurityPanelEnabled();
        return;
      } else if ( b.equals( PdfExportGUIModule.SECURITY_ENCRYPTION_NONE ) == false ) {
        PdfExportDialog.logger.warn( "Invalid encryption value entered. " + b ); //$NON-NLS-1$
      }
    }
    rbSecurityNone.setSelected( true );
    updateSecurityPanelEnabled();
  }
}
