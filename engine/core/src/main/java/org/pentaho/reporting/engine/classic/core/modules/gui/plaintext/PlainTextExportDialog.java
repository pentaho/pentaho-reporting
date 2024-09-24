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

package org.pentaho.reporting.engine.classic.core.modules.gui.plaintext;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.AbstractExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.action.AbstractFileSelectionAction;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextPageableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.Epson24PinPrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.Epson9PinPrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.IBMCompatiblePrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterSpecification;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterSpecificationManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * A dialog that is used to export reports to plain text.
 *
 * @author Thomas Morgner.
 */
public class PlainTextExportDialog extends AbstractExportDialog {
  private Messages messages;

  /**
   * An action to select a file.
   */
  private class ActionSelectFile extends AbstractFileSelectionAction {
    private final ResourceBundle resources;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    protected ActionSelectFile( final ResourceBundle resources ) {
      super( PlainTextExportDialog.this );
      this.resources = resources;
      putValue( Action.NAME, resources.getString( "plain-text-exportdialog.selectFile" ) ); //$NON-NLS-1$
    }

    /**
     * Returns a descriptive text describing the file extension.
     *
     * @return the file description.
     */
    protected String getFileDescription() {
      return resources.getString( "plain-text-exportdialog.fileDescription" ); //$NON-NLS-1$
    }

    /**
     * Returns the file extension that should be used for the operation.
     *
     * @return the file extension.
     */
    protected String getFileExtension() {
      return PlainTextExportDialog.TXT_FILE_EXTENSION;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      final File selectedFile = performSelectFile( new File( getFilename() ), JFileChooser.SAVE_DIALOG, true );
      if ( selectedFile != null ) {
        setFilename( selectedFile.getPath() );
      }
    }
  }

  /**
   * An action to select a plain printer.
   */
  private class ActionSelectPrinter extends AbstractAction {
    private int printer;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    protected ActionSelectPrinter( final String printerName, final int printer ) {
      putValue( Action.NAME, printerName );
      this.printer = printer;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      setSelectedPrinter( printer );
    }
  }

  private class SelectEpsonModelAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    protected SelectEpsonModelAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( getSelectedPrinter() == PlainTextExportDialog.TYPE_EPSON9_OUTPUT ) {
        updateEpson9Encoding();
      } else if ( getSelectedPrinter() == PlainTextExportDialog.TYPE_EPSON24_OUTPUT ) {
        updateEpson24Encoding();
      }
    }
  }

  private static final String TXT_FILE_EXTENSION = ".txt"; //$NON-NLS-1$

  /**
   * Plain text output.
   */
  public static final int TYPE_PLAIN_OUTPUT = 0;

  /**
   * Epson printer output.
   */
  public static final int TYPE_EPSON9_OUTPUT = 1;

  /**
   * IBM printer output.
   */
  public static final int TYPE_IBM_OUTPUT = 2;

  /**
   * Epson printer output.
   */
  public static final int TYPE_EPSON24_OUTPUT = 3;

  private static final String[] PRINTER_NAMES = new String[] { "plain-text-exportdialog.printer.plain", //$NON-NLS-1$
    "plain-text-exportdialog.printer.epson9", //$NON-NLS-1$
    "plain-text-exportdialog.printer.ibm", //$NON-NLS-1$
    "plain-text-exportdialog.printer.epson24", //$NON-NLS-1$
  };

  /**
   * 6 lines per inch.
   */
  public static final Float LPI_6 = new Float( 6 );

  /**
   * 10 lines per inch.
   */
  public static final Float LPI_10 = new Float( 10 );

  /**
   * 10 characters per inch.
   */
  public static final Float CPI_10 = new Float( 10 );

  /**
   * 12 characters per inch.
   */
  public static final Float CPI_12 = new Float( 12 );

  /**
   * 15 characters per inch.
   */
  public static final Float CPI_15 = new Float( 15 );

  /**
   * 17 characters per inch.
   */
  public static final Float CPI_17 = new Float( 17.14f );

  /**
   * 20 characters per inch.
   */
  public static final Float CPI_20 = new Float( 20 );

  /**
   * A combo-box for selecting the encoding.
   */
  private EncodingSelector encodingSelector;

  /**
   * A radio button for selecting plain printer commands.
   */
  private JRadioButton rbPlainPrinterCommandSet;

  /**
   * A radio button for selecting Epson 9-pin printer commands.
   */
  private JRadioButton rbEpson9PrinterCommandSet;

  /**
   * A radio button for selecting Epson 24-pin printer commands.
   */
  private JRadioButton rbEpson24PrinterCommandSet;

  /**
   * A radio button for selecting IBM printer commands.
   */
  private JRadioButton rbIBMPrinterCommandSet;

  /**
   * The filename text field.
   */
  private JTextField txFilename;

  /**
   * A combo-box for selecting lines per inch.
   */
  private JComboBox cbLinesPerInch;

  /**
   * A combo-box for selecting characters per inch.
   */
  private JComboBox cbCharsPerInch;

  private JComboBox cbEpson9PrinterType;
  private JComboBox cbEpson24PrinterType;

  private KeyedComboBoxModel epson9Printers;
  private KeyedComboBoxModel epson24Printers;

  private JStatusBar statusBar;
  private static final String EXPORT_TYPE_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.ExportType";

  /**
   * Creates a non-modal dialog without a title and without a specified Frame owner. A shared, hidden frame will be set
   * as the owner of the Dialog.
   */
  public PlainTextExportDialog() {
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified Frame as its owner.
   *
   * @param owner
   *          the Frame from which the dialog is displayed
   */
  public PlainTextExportDialog( final Frame owner ) {
    super( owner );
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified Dialog as its owner.
   *
   * @param owner
   *          the Dialog from which the dialog is displayed
   */
  public PlainTextExportDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  /**
   * Initialize the dialog.
   */
  private void init() {
    setTitle( getResources().getString( "plain-text-exportdialog.dialogtitle" ) ); //$NON-NLS-1$
    messages =
        new Messages( Locale.getDefault(), PlainTextExportGUIModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( PlainTextExportGUIModule.class ) );
    epson9Printers = loadEpson9Printers();
    epson24Printers = loadEpson24Printers();

    cbEpson9PrinterType = new JComboBox( epson9Printers );
    cbEpson9PrinterType.addActionListener( new SelectEpsonModelAction() );

    cbEpson24PrinterType = new JComboBox( epson24Printers );
    cbEpson24PrinterType.addActionListener( new SelectEpsonModelAction() );

    statusBar = new JStatusBar();

    final Float[] lpiModel = { PlainTextExportDialog.LPI_6, PlainTextExportDialog.LPI_10 };

    final Float[] cpiModel =
    { PlainTextExportDialog.CPI_10, PlainTextExportDialog.CPI_12, PlainTextExportDialog.CPI_15,
      PlainTextExportDialog.CPI_17, PlainTextExportDialog.CPI_20 };

    cbLinesPerInch = new JComboBox( new DefaultComboBoxModel( lpiModel ) );
    cbCharsPerInch = new JComboBox( new DefaultComboBoxModel( cpiModel ) );

    final String plainPrinterName =
        getResources().getString( PlainTextExportDialog.PRINTER_NAMES[PlainTextExportDialog.TYPE_PLAIN_OUTPUT] );
    final String epson9PrinterName =
        getResources().getString( PlainTextExportDialog.PRINTER_NAMES[PlainTextExportDialog.TYPE_EPSON9_OUTPUT] );
    final String epson24PrinterName =
        getResources().getString( PlainTextExportDialog.PRINTER_NAMES[PlainTextExportDialog.TYPE_EPSON24_OUTPUT] );
    final String ibmPrinterName =
        getResources().getString( PlainTextExportDialog.PRINTER_NAMES[PlainTextExportDialog.TYPE_IBM_OUTPUT] );

    rbPlainPrinterCommandSet =
        new JRadioButton( new ActionSelectPrinter( plainPrinterName, PlainTextExportDialog.TYPE_PLAIN_OUTPUT ) );
    rbEpson9PrinterCommandSet =
        new JRadioButton( new ActionSelectPrinter( epson9PrinterName, PlainTextExportDialog.TYPE_EPSON9_OUTPUT ) );
    rbEpson24PrinterCommandSet =
        new JRadioButton( new ActionSelectPrinter( epson24PrinterName, PlainTextExportDialog.TYPE_EPSON24_OUTPUT ) );
    rbIBMPrinterCommandSet =
        new JRadioButton( new ActionSelectPrinter( ibmPrinterName, PlainTextExportDialog.TYPE_IBM_OUTPUT ) );

    txFilename = new JTextField();
    encodingSelector = new EncodingSelector();

    final ButtonGroup bg = new ButtonGroup();
    bg.add( rbPlainPrinterCommandSet );
    bg.add( rbIBMPrinterCommandSet );
    bg.add( rbEpson9PrinterCommandSet );
    bg.add( rbEpson24PrinterCommandSet );

    getFormValidator().registerTextField( txFilename );
    getFormValidator().registerButton( rbEpson24PrinterCommandSet );
    getFormValidator().registerButton( rbEpson9PrinterCommandSet );
    getFormValidator().registerButton( rbIBMPrinterCommandSet );
    getFormValidator().registerButton( rbPlainPrinterCommandSet );

    final JComponent exportPane = createExportPane();

    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final boolean advancedSettingsTabAvail =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.AdvancedSettingsAvailable" ) );
    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( getResources().getString( "plain-text-exportdialog.export-settings" ), exportPane ); //$NON-NLS-1$
    tabbedPane.add( getResources().getString( "plain-text-exportdialog.parameters" ), getParametersPanel() );

    if ( advancedSettingsTabAvail ) {
      tabbedPane.add( getResources().getString( "plain-text-exportdialog.advanced-settings" ), createAdvancedPane() ); //$NON-NLS-1$
    }
    setContentPane( createContentPane( tabbedPane ) );
    clear();
  }

  private KeyedComboBoxModel loadEpson24Printers() {
    final KeyedComboBoxModel epsonPrinters = new KeyedComboBoxModel();
    final PrinterSpecificationManager spec24Manager = Epson24PinPrinterDriver.loadSpecificationManager();
    final String[] printer24Names = spec24Manager.getPrinterNames();
    Arrays.sort( printer24Names );
    for ( int i = 0; i < printer24Names.length; i++ ) {
      final PrinterSpecification pspec = spec24Manager.getPrinter( printer24Names[i] );
      epsonPrinters.add( pspec, pspec.getDisplayName() );
    }
    return epsonPrinters;
  }

  private KeyedComboBoxModel loadEpson9Printers() {
    final KeyedComboBoxModel epsonPrinters = new KeyedComboBoxModel();
    final PrinterSpecificationManager spec9Manager = Epson9PinPrinterDriver.loadSpecificationManager();
    final String[] printer9Names = spec9Manager.getPrinterNames();
    Arrays.sort( printer9Names );
    for ( int i = 0; i < printer9Names.length; i++ ) {
      final PrinterSpecification pspec = spec9Manager.getPrinter( printer9Names[i] );
      epsonPrinters.add( pspec, pspec.getDisplayName() );
    }
    return epsonPrinters;
  }

  public JStatusBar getStatusBar() {
    return statusBar;
  }

  /**
   * Creates the content pane for the export dialog.
   *
   * @return the created content pane.
   */
  private JComponent createExportPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

    final JLabel lblFileName = new JLabel( getResources().getString( "plain-text-exportdialog.filename" ) ); //$NON-NLS-1$
    final JButton btnSelect = new JButton( new ActionSelectFile( getResources() ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( lblFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.ipadx = 120;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 3;
    gbc.gridy = 0;
    contentPane.add( btnSelect, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;
  }

  /**
   * Creates the content pane for the export dialog.
   *
   * @return the created content pane.
   */
  private JComponent createAdvancedPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

    final JLabel lblPrinterSelect = new JLabel( getResources().getString( "plain-text-exportdialog.printer" ) ); //$NON-NLS-1$
    final JLabel lblEncoding = new JLabel( getResources().getString( "plain-text-exportdialog.encoding" ) ); //$NON-NLS-1$

    final JLabel lblCharsPerInch = new JLabel( getResources().getString( "plain-text-exportdialog.chars-per-inch" ) ); //$NON-NLS-1$
    final JLabel lblLinesPerInch = new JLabel( getResources().getString( "plain-text-exportdialog.lines-per-inch" ) ); //$NON-NLS-1$
    final JLabel lblFontSettings = new JLabel( getResources().getString( "plain-text-exportdialog.font-settings" ) ); //$NON-NLS-1$

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( encodingSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblPrinterSelect, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( rbPlainPrinterCommandSet, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( rbIBMPrinterCommandSet, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( rbEpson9PrinterCommandSet, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( cbEpson9PrinterType, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( rbEpson24PrinterCommandSet, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 3;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( cbEpson24PrinterType, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblFontSettings, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 2;
    gbc.gridy = 6;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblCharsPerInch, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbCharsPerInch, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 2;
    gbc.gridy = 7;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblLinesPerInch, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbLinesPerInch, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;

  }

  protected void updateEpson9Encoding() {
    final PrinterSpecification spec = (PrinterSpecification) epson9Printers.getSelectedKey();
    if ( spec == null ) {
      encodingSelector.setEncodings( PrinterSpecificationManager.getGenericPrinter(), getGuiContext().getLocale() );
    } else {
      encodingSelector.setEncodings( spec, getGuiContext().getLocale() );
    }
  }

  protected void updateEpson24Encoding() {
    final PrinterSpecification spec = (PrinterSpecification) epson9Printers.getSelectedKey();
    if ( spec == null ) {
      encodingSelector.setEncodings( PrinterSpecificationManager.getGenericPrinter(), getGuiContext().getLocale() );
    } else {
      encodingSelector.setEncodings( spec, getGuiContext().getLocale() );
    }
  }

  /**
   * Sets the selected printer.
   *
   * @param type
   *          the type.
   */
  public void setSelectedPrinter( final int type ) {
    final Locale locale = getGuiContext().getLocale();

    final String oldEncoding = getEncoding();
    if ( type == PlainTextExportDialog.TYPE_EPSON9_OUTPUT ) {
      rbEpson9PrinterCommandSet.setSelected( true );
      cbEpson9PrinterType.setEnabled( true );
      cbEpson24PrinterType.setEnabled( false );
      updateEpson9Encoding();
    } else if ( type == PlainTextExportDialog.TYPE_EPSON24_OUTPUT ) {
      rbEpson24PrinterCommandSet.setSelected( true );
      cbEpson24PrinterType.setEnabled( true );
      cbEpson9PrinterType.setEnabled( false );
      updateEpson24Encoding();
    } else if ( type == PlainTextExportDialog.TYPE_IBM_OUTPUT ) {
      rbIBMPrinterCommandSet.setSelected( true );
      cbEpson9PrinterType.setEnabled( false );
      cbEpson24PrinterType.setEnabled( false );
      encodingSelector.setEncodings( new IBMCompatiblePrinterDriver.GenericIBMPrinterSpecification(), locale );
    } else if ( type == PlainTextExportDialog.TYPE_PLAIN_OUTPUT ) {
      rbPlainPrinterCommandSet.setSelected( true );
      cbEpson9PrinterType.setEnabled( false );
      cbEpson24PrinterType.setEnabled( false );
      encodingSelector.setEncodings( new EncodingSelector.GenericPrinterSpecification(), locale );
    } else {
      throw new IllegalArgumentException();
    }
    if ( oldEncoding != null ) {
      setEncoding( oldEncoding );
    }
  }

  /**
   * Returns the selected printer.
   *
   * @return The printer type.
   */
  public int getSelectedPrinter() {
    if ( rbPlainPrinterCommandSet.isSelected() ) {
      return PlainTextExportDialog.TYPE_PLAIN_OUTPUT;
    }
    if ( rbEpson9PrinterCommandSet.isSelected() ) {
      return PlainTextExportDialog.TYPE_EPSON9_OUTPUT;
    }
    if ( rbEpson24PrinterCommandSet.isSelected() ) {
      return PlainTextExportDialog.TYPE_EPSON24_OUTPUT;
    }
    return PlainTextExportDialog.TYPE_IBM_OUTPUT;
  }

  /**
   * Returns the filename.
   *
   * @return the name of the file where to save the file.
   */
  public String getFilename() {
    return txFilename.getText();
  }

  /**
   * Defines the filename of the file.
   *
   * @param filename
   *          the filename of the file
   */
  public void setFilename( final String filename ) {
    this.txFilename.setText( filename );
  }

  /**
   * clears all selections, input fields and set the selected encryption level to none.
   */
  public void clear() {
    txFilename.setText( "" ); //$NON-NLS-1$
    setSelectedPrinter( PlainTextExportDialog.TYPE_PLAIN_OUTPUT );
    cbEpson9PrinterType.setEnabled( false );
    cbEpson9PrinterType.setSelectedItem( Epson9PinPrinterDriver.getDefaultPrinter() );
    cbEpson24PrinterType.setEnabled( false );
    cbEpson24PrinterType.setSelectedItem( Epson24PinPrinterDriver.getDefaultPrinter() );
    cbCharsPerInch.setSelectedItem( PlainTextExportDialog.CPI_10 );
    cbLinesPerInch.setSelectedItem( PlainTextExportDialog.LPI_6 );
    setEncoding( EncodingRegistry.getPlatformDefaultEncoding() );
  }

  /**
   * Returns the lines-per-inch setting.
   *
   * @return The lines-per-inch setting.
   */
  public float getLinesPerInch() {
    final Float i = (Float) cbLinesPerInch.getSelectedItem();
    if ( i == null ) {
      return PlainTextExportDialog.LPI_6.floatValue();
    }
    return i.floatValue();
  }

  /**
   * Sets the lines per inch.
   *
   * @param lpi
   *          the lines per inch.
   */
  public void setLinesPerInch( final float lpi ) {
    final Float lpiObj = new Float( lpi );
    final ComboBoxModel model = cbLinesPerInch.getModel();
    for ( int i = 0; i < model.getSize(); i++ ) {
      if ( lpiObj.equals( model.getElementAt( i ) ) ) {
        cbLinesPerInch.setSelectedIndex( i );
        return;
      }
    }
    throw new IllegalArgumentException( messages.getErrorString( "PlainTextExportDialog.ERROR_0001_NO_SUCH_LPI", String
        .valueOf( lpi ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Returns the characters-per-inch setting.
   *
   * @return The characters-per-inch setting.
   */
  public float getCharsPerInch() {
    final Float i = (Float) cbCharsPerInch.getSelectedItem();
    if ( i == null ) {
      return PlainTextExportDialog.CPI_10.floatValue();
    }
    return i.floatValue();
  }

  /**
   * Sets the characters per inch.
   *
   * @param cpi
   *          the characters per inch.
   */
  public void setCharsPerInch( final float cpi ) {
    final Float cpiObj = new Float( cpi );
    final ComboBoxModel model = cbCharsPerInch.getModel();
    for ( int i = 0; i < model.getSize(); i++ ) {
      if ( cpiObj.equals( model.getElementAt( i ) ) ) {
        cbCharsPerInch.setSelectedIndex( i );
        return;
      }
    }
    throw new IllegalArgumentException( messages.getErrorString( "PlainTextExportDialog.ERROR_0002_NO_SUCH_CPI", String
        .valueOf( cpi ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Returns the encoding.
   *
   * @return The encoding.
   */
  public String getEncoding() {
    return encodingSelector.getSelectedEncoding();
  }

  /**
   * Sets the encoding.
   *
   * @param encoding
   *          the encoding.
   */
  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException( getResources().getString( "PlainTextExportDialog.ERROR_0003_NULL_ENCODING" ) ); //$NON-NLS-1$
    }
    encodingSelector.setSelectedEncoding( encoding );
  }

  protected void setDialogContents( final Configuration config ) {
    setEncoding( config.getConfigProperty( PlainTextPageableModule.ENCODING, PlainTextPageableModule.ENCODING_DEFAULT ) );
    setSelected9PinPrinterModel( config.getConfigProperty( Epson9PinPrinterDriver.EPSON_9PIN_PRINTER_TYPE,
        getSelected9PinPrinterModel() ) );
    setSelected24PinPrinterModel( config.getConfigProperty( Epson24PinPrinterDriver.EPSON_24PIN_PRINTER_TYPE,
        getSelected24PinPrinterModel() ) );
    final String mode = config.getConfigProperty( EXPORT_TYPE_CONFIG_KEY ); //$NON-NLS-1$
    if ( "9pin".equals( mode ) ) { //$NON-NLS-1$
      setSelectedPrinter( PlainTextExportDialog.TYPE_EPSON9_OUTPUT );
    } else if ( "24pin".equals( mode ) ) { //$NON-NLS-1$
      setSelectedPrinter( PlainTextExportDialog.TYPE_EPSON24_OUTPUT );
    } else if ( "ibm".equals( mode ) ) { //$NON-NLS-1$
      setSelectedPrinter( PlainTextExportDialog.TYPE_IBM_OUTPUT );
    } else {
      setSelectedPrinter( PlainTextExportDialog.TYPE_PLAIN_OUTPUT );
    }

    try {
      final String lpi = config.getConfigProperty( PlainTextPageableModule.LINES_PER_INCH );
      setLinesPerInch( ParserUtil.parseFloat( lpi, 6 ) );
    } catch ( IllegalArgumentException e ) {
      // ignore
    }

    try {
      final String cpi = config.getConfigProperty( PlainTextPageableModule.CHARS_PER_INCH );
      setCharsPerInch( ParserUtil.parseFloat( cpi, 10 ) );
    } catch ( IllegalArgumentException e ) {
      // ignore
    }

    final String defaultFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.FileName" ); //$NON-NLS-1$
    if ( defaultFileName != null ) {
      setFilename( resolvePath( defaultFileName ).getAbsolutePath() );
    }
  }

  /**
   * Stores the input from the dialog into the report configuration of the report.
   */
  protected Configuration grabDialogContents( final boolean full ) {
    final DefaultConfiguration config = new DefaultConfiguration();

    config.setConfigProperty( PlainTextPageableModule.ENCODING, getEncoding() );
    config.setConfigProperty( PlainTextPageableModule.CHARS_PER_INCH, String.valueOf( getCharsPerInch() ) );
    config.setConfigProperty( PlainTextPageableModule.LINES_PER_INCH, String.valueOf( getLinesPerInch() ) );
    config.setConfigProperty( Epson9PinPrinterDriver.EPSON_9PIN_PRINTER_TYPE, getSelected9PinPrinterModel() );
    config.setConfigProperty( Epson24PinPrinterDriver.EPSON_24PIN_PRINTER_TYPE, getSelected24PinPrinterModel() );

    if ( full ) {
      switch ( getSelectedPrinter() ) {
        case PlainTextExportDialog.TYPE_EPSON24_OUTPUT: {
          config.setConfigProperty( EXPORT_TYPE_CONFIG_KEY, "24pin" ); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
        case PlainTextExportDialog.TYPE_EPSON9_OUTPUT: {
          config.setConfigProperty( EXPORT_TYPE_CONFIG_KEY, "9pin" ); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
        case PlainTextExportDialog.TYPE_IBM_OUTPUT: {
          config.setConfigProperty( EXPORT_TYPE_CONFIG_KEY, "ibm" ); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
        case PlainTextExportDialog.TYPE_PLAIN_OUTPUT: {
          config.setConfigProperty( EXPORT_TYPE_CONFIG_KEY, "plain" ); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
        default:
          throw new IllegalStateException();
      }
      config.setConfigProperty(
          "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.FileName", getFilename() ); //$NON-NLS-1$

    }
    return config;
  }

  protected String getConfigurationSuffix() {
    return "_plaintextexport"; //$NON-NLS-1$
  }

  protected String getResourceBaseName() {
    return PlainTextExportGUIModule.BUNDLE_NAME;
  }

  /**
   * Validates the contents of the dialog's input fields. If the selected file exists, it is also checked for validity.
   *
   * @return true, if the input is valid, false otherwise
   */
  public boolean performValidate() {
    getStatusBar().clear();

    final String filename = getFilename();
    if ( filename.trim().length() == 0 ) {
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "plain-text-exportdialog.targetIsEmpty" ) ); //$NON-NLS-1$
      return false;
    }
    final File f = new File( filename );
    if ( f.exists() ) {
      if ( f.isFile() == false ) {
        getStatusBar()
            .setStatus( StatusType.ERROR, getResources().getString( "plain-text-exportdialog.targetIsNoFile" ) ); //$NON-NLS-1$
        return false;
      }
      if ( f.canWrite() == false ) {
        getStatusBar().setStatus( StatusType.ERROR,
            getResources().getString( "plain-text-exportdialog.targetIsNotWritable" ) ); //$NON-NLS-1$
        return false;
      }

      final String message =
          MessageFormat.format(
              getResources().getString( "plain-text-exportdialog.targetOverwriteWarning" ), new Object[] { filename } ); //$NON-NLS-1$
      getStatusBar().setStatus( StatusType.WARNING, message );

    }

    return true;
  }

  protected boolean performConfirm() {
    final String filename = getFilename();
    final File f = new File( filename );
    if ( f.exists() ) {
      final String key1 = "plain-text-exportdialog.targetOverwriteConfirmation"; //$NON-NLS-1$
      final String key2 = "plain-text-exportdialog.targetOverwriteTitle"; //$NON-NLS-1$
      if ( JOptionPane.showConfirmDialog( this, MessageFormat.format( getResources().getString( key1 ),
          new Object[] { getFilename() } ), getResources().getString( key2 ), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE ) == JOptionPane.NO_OPTION ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Warning: Might return null!
   *
   * @return
   */
  public String getSelected9PinPrinterModel() {
    return (String) cbEpson9PrinterType.getSelectedItem();
  }

  public String getSelected24PinPrinterModel() {
    return (String) cbEpson24PrinterType.getSelectedItem();
  }

  public void setSelected9PinPrinterModel( final String selectedPrinterModel ) {
    final int size = epson9Printers.getSize();
    for ( int i = 0; i < size; i++ ) {
      final PrinterSpecification spec = (PrinterSpecification) epson9Printers.getKeyAt( i );
      if ( spec.getDisplayName().equals( selectedPrinterModel ) ) {
        epson9Printers.setSelectedKey( spec );
        return;
      }
    }
    epson9Printers.setSelectedKey( null );
  }

  public void setSelected24PinPrinterModel( final String selectedPrinterModel ) {
    final int size = epson24Printers.getSize();
    for ( int i = 0; i < size; i++ ) {
      final PrinterSpecification spec = (PrinterSpecification) epson24Printers.getKeyAt( i );
      if ( spec.getDisplayName().equals( selectedPrinterModel ) ) {
        epson24Printers.setSelectedKey( spec );
        return;
      }
    }
    epson24Printers.setSelectedKey( null );
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext."; //$NON-NLS-1$
  }
}
