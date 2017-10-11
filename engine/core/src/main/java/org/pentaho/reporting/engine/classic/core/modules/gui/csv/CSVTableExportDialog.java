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

package org.pentaho.reporting.engine.classic.core.modules.gui.csv;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.AbstractExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.EncodingComboBoxModel;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.LengthLimitingDocument;
import org.pentaho.reporting.engine.classic.core.modules.output.csv.CSVProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVTableModule;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * A dialog for exporting a report to CSV format.
 *
 * @author Thomas Morgner.
 */
public class CSVTableExportDialog extends AbstractExportDialog {
  /**
   * A default value of the 'CSV encoding' property key.
   */
  public static final String CSV_OUTPUT_ENCODING_DEFAULT = EncodingRegistry.getPlatformDefaultEncoding();

  /**
   * Internal action class to confirm the dialog and to validate the input.
   */
  private class ActionSelectSeparator extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ActionSelectSeparator() {
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      performSeparatorSelection();
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
      putValue( Action.NAME, resources.getString( "csvexportdialog.selectFile" ) ); //$NON-NLS-1$
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
   * Filename text field.
   */
  private JTextField txFilename;

  /**
   * The encoding combo-box.
   */
  private JComboBox cbEncoding;

  /**
   * The encoding model.
   */
  private EncodingComboBoxModel encodingModel;

  /**
   * The strict layout check-box.
   */
  private JCheckBox cbxStrictLayout;

  /**
   * A radio button for tab separators.
   */
  private JRadioButton rbSeparatorTab;

  /**
   * A radio button for colon separators.
   */
  private JRadioButton rbSeparatorColon;

  /**
   * A radio button for semi-colon separators.
   */
  private JRadioButton rbSeparatorSemicolon;

  /**
   * A radio button for other separators.
   */
  private JRadioButton rbSeparatorOther;

  /**
   * A text field for the 'other' separator.
   */
  private JTextField txSeparatorOther;

  private JStatusBar statusBar;

  /**
   * A file chooser.
   */
  private JFileChooser fileChooser;

  private static final String COMMA_SEPARATOR = ","; //$NON-NLS-1$
  private static final String SEMICOLON_SEPARATOR = ";"; //$NON-NLS-1$
  private static final String TAB_SEPARATOR = "\t"; //$NON-NLS-1$
  private static final String CSV_FILE_EXTENSION = ".csv"; //$NON-NLS-1$

  /**
   * Creates a new CSV export dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public CSVTableExportDialog( final Frame owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new CSV export dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public CSVTableExportDialog( final Dialog owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new CSV export dialog. The created dialog is modal.
   */
  public CSVTableExportDialog() {
    initConstructor();
  }

  /**
   * Initialisation.
   */
  private void initConstructor() {
    statusBar = new JStatusBar();
    setTitle( getResources().getString( "csvexportdialog.dialogtitle" ) ); //$NON-NLS-1$
    initialize();
    clear();
    getFormValidator().setEnabled( true );
  }

  public JStatusBar getStatusBar() {
    return statusBar;
  }

  protected String getResourceBaseName() {
    return CSVDataExportPlugin.BASE_RESOURCE_CLASS;
  }

  /**
   * Initializes the Swing components of this dialog.
   */
  private void initialize() {

    rbSeparatorTab = new JRadioButton( getResources().getString( "csvexportdialog.separator.tab" ) ); //$NON-NLS-1$
    rbSeparatorColon = new JRadioButton( getResources().getString( "csvexportdialog.separator.colon" ) ); //$NON-NLS-1$
    rbSeparatorSemicolon = new JRadioButton( getResources().getString( "csvexportdialog.separator.semicolon" ) ); //$NON-NLS-1$
    rbSeparatorOther = new JRadioButton( getResources().getString( "csvexportdialog.separator.other" ) ); //$NON-NLS-1$

    getFormValidator().registerButton( rbSeparatorColon );
    getFormValidator().registerButton( rbSeparatorOther );
    getFormValidator().registerButton( rbSeparatorSemicolon );
    getFormValidator().registerButton( rbSeparatorTab );

    final ButtonGroup btg = new ButtonGroup();
    btg.add( rbSeparatorTab );
    btg.add( rbSeparatorColon );
    btg.add( rbSeparatorSemicolon );
    btg.add( rbSeparatorOther );

    final Action selectAction = new CSVTableExportDialog.ActionSelectSeparator();
    rbSeparatorTab.addActionListener( selectAction );
    rbSeparatorColon.addActionListener( selectAction );
    rbSeparatorSemicolon.addActionListener( selectAction );
    rbSeparatorOther.addActionListener( selectAction );

    txSeparatorOther = new JTextField();
    txSeparatorOther.setDocument( new LengthLimitingDocument( 1 ) );
    txSeparatorOther.setColumns( 5 );
    getFormValidator().registerTextField( txSeparatorOther );

    cbxStrictLayout = new JCheckBox( getResources().getString( "csvexportdialog.strict-layout" ) ); //$NON-NLS-1$
    getFormValidator().registerButton( cbxStrictLayout );

    txFilename = new JTextField();
    txFilename.setColumns( 30 );
    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault() );
    encodingModel.sort();
    cbEncoding = new JComboBox( encodingModel );

    final JPanel exportPane = createExportPane();

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( getResources().getString( "csvexportdialog.export-settings" ), exportPane ); //$NON-NLS-1$
    tabbedPane.add( getResources().getString( "csvexportdialog.parameters" ), getParametersPanel() );

    // button panel
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "true"
        .equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.csv.table.AdvancedSettingsAvailable" ) ) ) {
      final JPanel advancedOptionsPane = createAdvancedOptionsPanel();
      tabbedPane.add( getResources().getString( "csvexportdialog.advanced-settings" ), advancedOptionsPane ); //$NON-NLS-1$

    }
    setContentPane( createContentPane( tabbedPane ) );

    getFormValidator().registerTextField( txFilename );
    getFormValidator().registerComboBox( cbEncoding );
  }

  private JPanel createExportPane() {
    final JLabel lblFileName = new JLabel( getResources().getString( "csvexportdialog.filename" ) ); //$NON-NLS-1$
    final JButton btnSelect = new JButton( new CSVTableExportDialog.ActionSelectFile( getResources() ) );
    final JPanel exportPane = new JPanel();
    exportPane.setLayout( new GridBagLayout() );
    exportPane.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 1, 5 );
    exportPane.add( lblFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 3, 1, 5, 1 );
    exportPane.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.insets = new Insets( 1, 5, 5, 1 );
    exportPane.add( btnSelect, gbc );

    gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    exportPane.add( new JPanel(), gbc );

    return exportPane;
  }

  private JPanel createAdvancedOptionsPanel() {
    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    advancedOptionsPane.add( createExportOptionsPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    advancedOptionsPane.add( createSeparatorPanel(), gbc );
    return advancedOptionsPane;
  }

  /**
   * Creates a panel for the export type.
   *
   * @return The panel.
   */
  private JPanel createExportOptionsPanel() {
    // separator panel
    final JPanel exportTypePanel = new JPanel();
    exportTypePanel.setLayout( new GridBagLayout() );
    final JLabel lblEncoding = new JLabel( getResources().getString( "csvexportdialog.encoding" ) ); //$NON-NLS-1$

    final TitledBorder tb = new TitledBorder( getResources().getString( "csvexportdialog.export-options" ) ); //$NON-NLS-1$
    exportTypePanel.setBorder( tb );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 1, 1, 1, 5 );
    exportTypePanel.add( lblEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 5, 1, 1, 1 );
    exportTypePanel.add( cbEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 5, 1, 1, 1 );
    exportTypePanel.add( cbxStrictLayout, gbc );

    return exportTypePanel;
  }

  /**
   * Creates a separator panel.
   *
   * @return The panel.
   */
  private JPanel createSeparatorPanel() {
    // separator panel
    final JPanel separatorPanel = new JPanel();
    separatorPanel.setLayout( new GridBagLayout() );

    final TitledBorder tb = new TitledBorder( getResources().getString( "csvexportdialog.separatorchar" ) ); //$NON-NLS-1$
    separatorPanel.setBorder( tb );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    separatorPanel.add( rbSeparatorTab, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    separatorPanel.add( rbSeparatorColon, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    separatorPanel.add( rbSeparatorSemicolon, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    separatorPanel.add( rbSeparatorOther, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weighty = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    gbc.ipadx = 20;
    separatorPanel.add( txSeparatorOther, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    separatorPanel.add( new JPanel(), gbc );

    return separatorPanel;
  }

  /**
   * Returns the export file name.
   *
   * @return The file name.
   */
  public String getFilename() {
    return txFilename.getText();
  }

  /**
   * Sets the export file name.
   *
   * @param filename
   *          the file name.
   */
  public void setFilename( final String filename ) {
    this.txFilename.setText( filename );
  }

  /**
   * Clears all selections, input fields and sets the selected encryption level to none.
   */
  public void clear() {
    txFilename.setText( "" ); //$NON-NLS-1$
    cbEncoding.setSelectedIndex( encodingModel.indexOf( EncodingRegistry.getPlatformDefaultEncoding() ) );
    rbSeparatorColon.setSelected( true );
    cbxStrictLayout.setSelected( false );
    performSeparatorSelection();
  }

  /**
   * Returns a new (and not connected to the default config from the job) configuration containing all properties from
   * the dialog.
   *
   * @param full
   * @return
   */
  protected Configuration grabDialogContents( final boolean full ) {
    final ModifiableConfiguration config = new DefaultConfiguration();
    config.setConfigProperty( CSVProcessor.CSV_SEPARATOR, getSeparatorString() );
    config.setConfigProperty( CSVTableModule.SEPARATOR, getSeparatorString() );
    config.setConfigProperty( CSVTableModule.STRICT_LAYOUT + ".StrictLayout", //$NON-NLS-1$
        String.valueOf( isStrictLayout() ) );
    config.setConfigProperty( CSVTableModule.ENCODING, getEncoding() );

    config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.csv.FileName", getFilename() ); //$NON-NLS-1$
    config.setConfigProperty( CSVProcessor.CSV_WRITE_STATECOLUMNS, "false" ); //$NON-NLS-1$

    return config;
  }

  /**
   * Initialises the CSV export dialog from the settings in the report configuration.
   *
   * @param config
   *          the report configuration.
   */
  protected void setDialogContents( final Configuration config ) {
    // the CSV separator has two sources, either the data CSV or the
    // table CSV. As we have only one input field for that property,
    // we use a cascading schema to resolve this. The data oriented
    // separator is preferred ...
    final String tableCSVSeparator =
        config.getConfigProperty( CSVProcessor.CSV_SEPARATOR, CSVTableExportDialog.COMMA_SEPARATOR );
    setSeparatorString( config.getConfigProperty( CSVTableModule.SEPARATOR, tableCSVSeparator ) );

    final String baseStrict =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout" );
    final String strict = config.getConfigProperty( CSVTableModule.STRICT_LAYOUT, baseStrict );
    setStrictLayout( "true".equals( strict ) ); //$NON-NLS-1$

    final String encoding =
        config.getConfigProperty( CSVTableModule.ENCODING, CSVTableExportDialog.CSV_OUTPUT_ENCODING_DEFAULT );
    encodingModel.ensureEncodingAvailable( encoding );
    setEncoding( encoding );

    final String defaultFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.csv.FileName" ); //$NON-NLS-1$
    if ( defaultFileName != null ) {
      setFilename( resolvePath( defaultFileName ).getAbsolutePath() );
    } else {
      setFilename( "" ); //$NON-NLS-1$
    }
  }

  /**
   * Returns the separator string, which is controlled by the selection of radio buttons.
   *
   * @return The separator string.
   */
  public String getSeparatorString() {
    if ( rbSeparatorColon.isSelected() ) {
      return CSVTableExportDialog.COMMA_SEPARATOR;
    }
    if ( rbSeparatorSemicolon.isSelected() ) {
      return CSVTableExportDialog.SEMICOLON_SEPARATOR;
    }
    if ( rbSeparatorTab.isSelected() ) {
      return CSVTableExportDialog.TAB_SEPARATOR;
    }
    if ( rbSeparatorOther.isSelected() ) {
      return txSeparatorOther.getText();
    }
    return ""; //$NON-NLS-1$
  }

  /**
   * Sets the separator string.
   *
   * @param s
   *          the separator.
   */
  public void setSeparatorString( final String s ) {
    if ( s == null ) {
      rbSeparatorOther.setSelected( true );
      txSeparatorOther.setText( "" ); //$NON-NLS-1$
    } else if ( s.equals( CSVTableExportDialog.COMMA_SEPARATOR ) ) {
      rbSeparatorColon.setSelected( true );
    } else if ( s.equals( CSVTableExportDialog.SEMICOLON_SEPARATOR ) ) {
      rbSeparatorSemicolon.setSelected( true );
    } else if ( s.equals( CSVTableExportDialog.TAB_SEPARATOR ) ) {
      rbSeparatorTab.setSelected( true );
    } else {
      rbSeparatorOther.setSelected( true );
      txSeparatorOther.setText( s );
    }
    performSeparatorSelection();
  }

  /**
   * Returns the encoding.
   *
   * @return The encoding.
   */
  public String getEncoding() {
    if ( cbEncoding.getSelectedIndex() == -1 ) {
      return EncodingRegistry.getPlatformDefaultEncoding();
    } else {
      return encodingModel.getEncoding( cbEncoding.getSelectedIndex() );
    }
  }

  /**
   * Sets the encoding.
   *
   * @param encoding
   *          the encoding.
   */
  public void setEncoding( final String encoding ) {
    cbEncoding.setSelectedIndex( encodingModel.indexOf( encoding ) );
  }

  /**
   * Selects a file to use as target for the report processing.
   */
  protected void performSelectFile() {
    if ( fileChooser == null ) {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter =
          new FilesystemFilter( CSVTableExportDialog.CSV_FILE_EXTENSION, getResources().getString(
              "csvexportdialog.csv-file-description" ) ); //$NON-NLS-1$
      fileChooser.addChoosableFileFilter( filter );
      fileChooser.setMultiSelectionEnabled( false );
    }

    fileChooser.setSelectedFile( new File( getFilename() ) );
    final int option = fileChooser.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on csv
      if ( StringUtils.endsWithIgnoreCase( selFileName, CSVTableExportDialog.CSV_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + CSVTableExportDialog.CSV_FILE_EXTENSION;
      }
      setFilename( selFileName );
    }
  }

  /**
   * Validates the contents of the dialog's input fields. If the selected file exists, it is also checked for validity.
   *
   * @return <code>true</code> if the input is valid, <code>false</code> otherwise
   */
  protected boolean performValidate() {
    getStatusBar().clear();

    final String filename = getFilename();
    if ( filename.trim().length() == 0 ) {
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "csvexportdialog.targetIsEmpty" ) ); //$NON-NLS-1$
      return false;
    }
    final File f = new File( filename );
    if ( f.exists() ) {
      if ( f.isFile() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "csvexportdialog.targetIsNoFile" ) ); //$NON-NLS-1$
        return false;
      }
      if ( f.canWrite() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "csvexportdialog.targetIsNotWritable" ) ); //$NON-NLS-1$
        return false;
      }

      final String message = MessageFormat.format( getResources().getString( "csvexportdialog.targetExistsWarning" ), //$NON-NLS-1$
          new Object[] { filename } );
      getStatusBar().setStatus( StatusType.WARNING, message );

    }
    return true;
  }

  protected boolean performConfirm() {
    final File f = new File( getFilename() );
    if ( f.exists() ) {
      final String key1 = "csvexportdialog.targetOverwriteConfirmation"; //$NON-NLS-1$
      final String key2 = "csvexportdialog.targetOverwriteTitle"; //$NON-NLS-1$
      if ( JOptionPane.showConfirmDialog( this, MessageFormat.format( getResources().getString( key1 ),
          new Object[] { getFilename() } ), getResources().getString( key2 ), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE ) == JOptionPane.NO_OPTION ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Enables or disables the 'other' separator text field.
   */
  protected void performSeparatorSelection() {
    if ( rbSeparatorOther.isSelected() ) {
      txSeparatorOther.setEnabled( true );
    } else {
      txSeparatorOther.setEnabled( false );
    }
  }

  /**
   * Returns the current setting of the 'strict layout' combo-box.
   *
   * @return A boolean.
   */
  public boolean isStrictLayout() {
    return cbxStrictLayout.isSelected();
  }

  /**
   * Sets the 'strict layout' combo-box setting.
   *
   * @param strictLayout
   *          the new setting.
   */
  public void setStrictLayout( final boolean strictLayout ) {
    cbxStrictLayout.setSelected( strictLayout );
  }

  protected String getConfigurationSuffix() {
    return "_csvexport"; //$NON-NLS-1$
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.csv."; //$NON-NLS-1$
  }
}
