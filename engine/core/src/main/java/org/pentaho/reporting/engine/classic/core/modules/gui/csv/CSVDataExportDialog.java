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
public class CSVDataExportDialog extends AbstractExportDialog {
  // /**
  // * The 'CSV encoding' property key.
  // */
  // public static final String CSV_OUTPUT_ENCODING
  //      = "org.pentaho.reporting.engine.classic.core.modules.gui.csv.Encoding"; //$NON-NLS-1$
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

  private JCheckBox cbxEnableReportHeader;
  private JCheckBox cbxEnableReportFooter;
  private JCheckBox cbxEnableGroupHeader;
  private JCheckBox cbxEnableGroupFooter;
  private JCheckBox cbxEnableItemband;
  private JCheckBox cbxWriteStateColumns;

  /**
   * The columnnames-as-first-row layout check-box.
   */
  private JCheckBox cbxColumnNamesAsFirstRow;

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
  public CSVDataExportDialog( final Frame owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new CSV export dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public CSVDataExportDialog( final Dialog owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new CSV export dialog. The created dialog is modal.
   */
  public CSVDataExportDialog() {
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
    cbxWriteStateColumns = new JCheckBox( getResources().getString( "csvexportdialog.write-state-columns" ) ); //$NON-NLS-1$
    cbxColumnNamesAsFirstRow = new JCheckBox( getResources().getString( "cvsexportdialog.export.columnnames" ) ); //$NON-NLS-1$

    getFormValidator().registerButton( cbxColumnNamesAsFirstRow );
    cbxEnableReportHeader = new JCheckBox( getResources().getString( "csvexportdialog.enable-report-header" ) ); //$NON-NLS-1$
    cbxEnableReportFooter = new JCheckBox( getResources().getString( "csvexportdialog.enable-report-footer" ) ); //$NON-NLS-1$
    cbxEnableItemband = new JCheckBox( getResources().getString( "csvexportdialog.enable-itemband" ) ); //$NON-NLS-1$
    cbxEnableGroupHeader = new JCheckBox( getResources().getString( "csvexportdialog.enable-group-header" ) ); //$NON-NLS-1$
    cbxEnableGroupFooter = new JCheckBox( getResources().getString( "csvexportdialog.enable-group-footer" ) ); //$NON-NLS-1$

    getFormValidator().registerButton( cbxEnableGroupFooter );
    getFormValidator().registerButton( cbxEnableGroupHeader );
    getFormValidator().registerButton( cbxEnableItemband );
    getFormValidator().registerButton( cbxEnableReportFooter );
    getFormValidator().registerButton( cbxEnableReportHeader );

    txFilename = new JTextField();
    txFilename.setColumns( 30 );
    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault() );
    encodingModel.sort();
    cbEncoding = new JComboBox( encodingModel );

    final JPanel exportPane = createExportPane();

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( getResources().getString( "csvexportdialog.export-settings" ), exportPane ); //$NON-NLS-1$
    tabbedPane.add( getResources().getString( "csvexportdialog.parameters" ), getParametersPanel() ); //$NON-NLS-1$
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "true"
        .equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.csv.data.AdvancedSettingsAvailable" ) ) ) {
      tabbedPane.add( getResources().getString( "csvexportdialog.advanced-settings" ), createAdvancedOptionsPanel() ); //$NON-NLS-1$
    }
    setContentPane( createContentPane( tabbedPane ) );

    getFormValidator().registerTextField( txFilename );
    getFormValidator().registerComboBox( cbEncoding );
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

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 3;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    advancedOptionsPane.add( createExportTypePanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    advancedOptionsPane.add( new JPanel(), gbc );
    return advancedOptionsPane;
  }

  private JPanel createExportPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

    final JLabel lblFileName = new JLabel( getResources().getString( "csvexportdialog.filename" ) ); //$NON-NLS-1$
    final JButton btnSelect = new JButton( new ActionSelectFile( getResources() ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 1, 5 );
    contentPane.add( lblFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 3, 1, 5, 1 );
    contentPane.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.insets = new Insets( 1, 5, 5, 1 );
    contentPane.add( btnSelect, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets( 10, 1, 1, 1 );
    contentPane.add( new JPanel(), gbc );

    return contentPane;
  }

  /**
   * Creates a panel for the export type.
   *
   * @return The panel.
   */
  private JPanel createExportTypePanel() {
    // separator panel
    final JPanel exportTypePanel = new JPanel();
    exportTypePanel.setLayout( new BorderLayout() );

    final TitledBorder tb = new TitledBorder( getResources().getString( "csvexportdialog.exported-bands" ) ); //$NON-NLS-1$
    exportTypePanel.setBorder( tb );

    final JPanel rowTypePanel = new JPanel();
    rowTypePanel.setLayout( new GridLayout( 2, 3, 5, 2 ) );
    rowTypePanel.add( cbxEnableReportHeader );
    rowTypePanel.add( cbxEnableGroupHeader );
    rowTypePanel.add( cbxEnableItemband );
    rowTypePanel.add( cbxEnableReportFooter );
    rowTypePanel.add( cbxEnableGroupFooter );
    exportTypePanel.add( rowTypePanel, BorderLayout.WEST );
    return exportTypePanel;
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
    exportTypePanel.add( cbxColumnNamesAsFirstRow, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    exportTypePanel.add( cbxWriteStateColumns, gbc );

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

    final Action selectAction = new ActionSelectSeparator();
    rbSeparatorTab.addActionListener( selectAction );
    rbSeparatorColon.addActionListener( selectAction );
    rbSeparatorSemicolon.addActionListener( selectAction );
    rbSeparatorOther.addActionListener( selectAction );

    final LengthLimitingDocument ldoc = new LengthLimitingDocument( 1 );
    txSeparatorOther = new JTextField();
    txSeparatorOther.setDocument( ldoc );
    txSeparatorOther.setColumns( 5 );
    getFormValidator().registerTextField( txSeparatorOther );

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
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    separatorPanel.add( txSeparatorOther, gbc );

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
    cbxColumnNamesAsFirstRow.setSelected( false );
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
    config.setConfigProperty( CSVProcessor.CSV_DATAROWNAME, String.valueOf( isColumnNamesAsFirstRow() ) );
    config.setConfigProperty( CSVProcessor.CSV_ENCODING, getEncoding() );

    config.setConfigProperty( CSVProcessor.CSV_ENABLE_GROUPFOOTERS, String.valueOf( isEnableGroupFooter() ) );
    config.setConfigProperty( CSVProcessor.CSV_ENABLE_GROUPHEADERS, String.valueOf( isEnableGroupHeader() ) );
    config.setConfigProperty( CSVProcessor.CSV_ENABLE_ITEMBANDS, String.valueOf( isEnableItembands() ) );
    config.setConfigProperty( CSVProcessor.CSV_ENABLE_REPORTFOOTER, String.valueOf( isEnableReportFooter() ) );
    config.setConfigProperty( CSVProcessor.CSV_ENABLE_REPORTHEADER, String.valueOf( isEnableReportHeader() ) );

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
        config.getConfigProperty( CSVTableModule.SEPARATOR, CSVDataExportDialog.COMMA_SEPARATOR );
    setSeparatorString( config.getConfigProperty( CSVProcessor.CSV_SEPARATOR, tableCSVSeparator ) );

    final String colNames = config.getConfigProperty( CSVProcessor.CSV_DATAROWNAME, "false" ); //$NON-NLS-1$
    setColumnNamesAsFirstRow( "true".equals( colNames ) ); //$NON-NLS-1$

    final String encoding =
        config.getConfigProperty( CSVProcessor.CSV_ENCODING, CSVDataExportDialog.CSV_OUTPUT_ENCODING_DEFAULT );
    encodingModel.ensureEncodingAvailable( encoding );
    setEncoding( encoding );

    final String stateCols = config.getConfigProperty( CSVProcessor.CSV_WRITE_STATECOLUMNS, "false" ); //$NON-NLS-1$
    setWriteStateColumns( "true".equals( stateCols ) ); //$NON-NLS-1$

    final String enableReportHeader = config.getConfigProperty( CSVProcessor.CSV_ENABLE_REPORTHEADER, "false" ); //$NON-NLS-1$
    setEnableReportHeader( "true".equals( enableReportHeader ) ); //$NON-NLS-1$

    final String enableReportFooter = config.getConfigProperty( CSVProcessor.CSV_ENABLE_REPORTFOOTER, "false" ); //$NON-NLS-1$
    setEnableReportFooter( "true".equals( enableReportFooter ) ); //$NON-NLS-1$

    final String enableGroupHeader = config.getConfigProperty( CSVProcessor.CSV_ENABLE_GROUPHEADERS, "false" ); //$NON-NLS-1$
    setEnableGroupHeader( "true".equals( enableGroupHeader ) ); //$NON-NLS-1$

    final String enableGroupFooter = config.getConfigProperty( CSVProcessor.CSV_ENABLE_GROUPFOOTERS, "false" ); //$NON-NLS-1$
    setEnableGroupFooter( "true".equals( enableGroupFooter ) ); //$NON-NLS-1$

    final String enableItemBand = config.getConfigProperty( CSVProcessor.CSV_ENABLE_ITEMBANDS, "false" ); //$NON-NLS-1$
    setEnableItembands( "true".equals( enableItemBand ) ); //$NON-NLS-1$

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
      return CSVDataExportDialog.COMMA_SEPARATOR;
    }
    if ( rbSeparatorSemicolon.isSelected() ) {
      return CSVDataExportDialog.SEMICOLON_SEPARATOR;
    }
    if ( rbSeparatorTab.isSelected() ) {
      return CSVDataExportDialog.TAB_SEPARATOR;
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
    } else if ( s.equals( CSVDataExportDialog.COMMA_SEPARATOR ) ) {
      rbSeparatorColon.setSelected( true );
    } else if ( s.equals( CSVDataExportDialog.SEMICOLON_SEPARATOR ) ) {
      rbSeparatorSemicolon.setSelected( true );
    } else if ( s.equals( CSVDataExportDialog.TAB_SEPARATOR ) ) {
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
          new FilesystemFilter( CSVDataExportDialog.CSV_FILE_EXTENSION, getResources().getString(
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
      if ( StringUtils.endsWithIgnoreCase( selFileName, CSVDataExportDialog.CSV_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + CSVDataExportDialog.CSV_FILE_EXTENSION;
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

    if ( cbxEnableGroupFooter.isSelected() == false && cbxEnableGroupHeader.isSelected() == false
        && cbxEnableReportFooter.isSelected() == false && cbxEnableReportHeader.isSelected() == false
        && cbxEnableItemband.isSelected() == false ) {
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "csvexportdialog.noContentForExport" ) ); //$NON-NLS-1$
      return false;
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

  public boolean isColumnNamesAsFirstRow() {
    return cbxColumnNamesAsFirstRow.isSelected();
  }

  public void setColumnNamesAsFirstRow( final boolean colsAsFirstRow ) {
    cbxColumnNamesAsFirstRow.setSelected( colsAsFirstRow );
  }

  public boolean isWriteStateColumns() {
    return cbxWriteStateColumns.isSelected();
  }

  public void setWriteStateColumns( final boolean writeStateColumns ) {
    this.cbxWriteStateColumns.setSelected( writeStateColumns );
  }

  public boolean isEnableGroupFooter() {
    return cbxEnableGroupFooter.isSelected();
  }

  public void setEnableGroupFooter( final boolean enableGroupFooter ) {
    this.cbxEnableGroupFooter.setSelected( enableGroupFooter );
  }

  public boolean isEnableGroupHeader() {
    return cbxEnableGroupHeader.isSelected();
  }

  public void setEnableGroupHeader( final boolean enableGroupHeader ) {
    this.cbxEnableGroupHeader.setSelected( enableGroupHeader );
  }

  public boolean isEnableItembands() {
    return cbxEnableItemband.isSelected();
  }

  public void setEnableItembands( final boolean enableItembands ) {
    this.cbxEnableItemband.setSelected( enableItembands );
  }

  public boolean isEnableReportFooter() {
    return cbxEnableReportFooter.isSelected();
  }

  public void setEnableReportFooter( final boolean enableReportFooter ) {
    this.cbxEnableReportFooter.setSelected( enableReportFooter );
  }

  public boolean isEnableReportHeader() {
    return cbxEnableReportHeader.isSelected();
  }

  public void setEnableReportHeader( final boolean enableReportHeader ) {
    this.cbxEnableReportHeader.setSelected( enableReportHeader );
  }

  protected String getConfigurationSuffix() {
    return "_csvexport"; //$NON-NLS-1$
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.csv."; //$NON-NLS-1$
  }
}
