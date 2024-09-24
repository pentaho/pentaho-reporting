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

package org.pentaho.reporting.engine.classic.core.modules.gui.xls;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.AbstractExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * A dialog that is used to prepare the printing of a report into an Excel file.
 * <p/>
 * The main method to call the dialog is
 * {@link ExcelExportDialog#performQueryForExport(org.pentaho.reporting.engine.classic.core.MasterReport, org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext)}
 * . Given a report, the dialog is shown and if the user approved the dialog, the excel file is saved using the settings
 * made in the dialog.
 *
 * @author Heiko Evermann
 */
public class ExcelExportDialog extends AbstractExportDialog {
  /**
   * Internal action class to select a target file.
   */
  private class ActionSelectFile extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ActionSelectFile( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "excelexportdialog.selectFile" ) ); //$NON-NLS-1$
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
   * Select file action.
   */
  private Action actionSelectFile;

  /**
   * Filename text field.
   */
  private JTextField txFilename;

  /**
   * The strict layout check-box.
   */
  private JCheckBox cbStrictLayout;

  private JStatusBar statusBar;

  /**
   * A file chooser.
   */
  private JFileChooser fileChooser;
  private static final String XLS_FILE_EXTENSION = ".xls"; //$NON-NLS-1$

  /**
   * Creates a new Excel save dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public ExcelExportDialog( final Frame owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new Excel dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public ExcelExportDialog( final Dialog owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new Excel save dialog. The created dialog is modal.
   */
  public ExcelExportDialog() {
    initConstructor();
  }

  protected String getConfigurationSuffix() {
    return "_xlsexport"; //$NON-NLS-1$
  }

  /**
   * Initialisation.
   */
  private void initConstructor() {
    actionSelectFile = new ActionSelectFile( getResources() );
    statusBar = new JStatusBar();
    setTitle( getResources().getString( "excelexportdialog.dialogtitle" ) ); //$NON-NLS-1$
    initialize();
    clear();
  }

  public JStatusBar getStatusBar() {
    return statusBar;
  }

  /**
   * Returns a single instance of the file selection action.
   *
   * @return the action.
   */
  private Action getActionSelectFile() {
    return actionSelectFile;
  }

  /**
   * Initializes the Swing components of this dialog.
   */
  private void initialize() {
    final JTabbedPane theTabbedPane = new JTabbedPane();
    theTabbedPane.add( getResources().getString( "excelexportdialog.export-settings" ), createExportPanel() );
    theTabbedPane.add( getResources().getString( "excelexportdialog.parameters" ), getParametersPanel() );
    setContentPane( createContentPane( theTabbedPane ) );
  }

  private JPanel createExportPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

    final JLabel lblFileName = new JLabel( getResources().getString( "excelexportdialog.filename" ) ); //$NON-NLS-1$
    final JButton btnSelect = new JButton( getActionSelectFile() );

    txFilename = new JTextField();
    cbStrictLayout = new JCheckBox( getResources().getString( "excelexportdialog.strict-layout" ) ); //$NON-NLS-1$

    getFormValidator().registerButton( cbStrictLayout );
    getFormValidator().registerTextField( txFilename );

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
    gbc.ipadx = 120;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 120;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbStrictLayout, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 2;
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
   * Returns the filename of the excel file.
   *
   * @return the name of the file where to save the excel file.
   */
  public String getFilename() {
    return txFilename.getText();
  }

  /**
   * Defines the filename of the excel file.
   *
   * @param filename
   *          the filename of the excel file
   */
  public void setFilename( final String filename ) {
    this.txFilename.setText( filename );
  }

  /**
   * Returns the setting of the 'strict layout' check-box.
   *
   * @return A boolean.
   */
  public boolean isStrictLayout() {
    return cbStrictLayout.isSelected();
  }

  /**
   * Sets the 'strict-layout' check-box.
   *
   * @param strictLayout
   *          the new setting.
   */
  public void setStrictLayout( final boolean strictLayout ) {
    cbStrictLayout.setSelected( strictLayout );
  }

  /**
   * Clears all selections and input fields.
   */
  public void clear() {
    txFilename.setText( "" ); //$NON-NLS-1$
    cbStrictLayout.setSelected( false );
  }

  /**
   * Selects a file to use as target for the report processing.
   */
  protected void performSelectFile() {
    if ( fileChooser == null ) {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter =
          new FilesystemFilter( ExcelExportDialog.XLS_FILE_EXTENSION, getResources().getString(
              "excelexportdialog.excel-file-description" ) ); //$NON-NLS-1$
      fileChooser.addChoosableFileFilter( filter );
      fileChooser.setMultiSelectionEnabled( false );
    }

    final File file = new File( getFilename() );
    fileChooser.setCurrentDirectory( file );
    fileChooser.setSelectedFile( file );
    final int option = fileChooser.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on xls
      if ( StringUtils.endsWithIgnoreCase( selFileName, ExcelExportDialog.XLS_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + ExcelExportDialog.XLS_FILE_EXTENSION;
      }
      setFilename( selFileName );
    }
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
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "excelexportdialog.targetIsEmpty" ) ); //$NON-NLS-1$
      return false;
    }
    final File f = new File( filename );
    if ( f.exists() ) {
      if ( f.isFile() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "excelexportdialog.targetIsNoFile" ) ); //$NON-NLS-1$
        return false;
      }
      if ( f.canWrite() == false ) {
        getStatusBar()
            .setStatus( StatusType.ERROR, getResources().getString( "excelexportdialog.targetIsNotWritable" ) ); //$NON-NLS-1$
        return false;
      }
      final String message = MessageFormat.format( getResources().getString( "excelexportdialog.targetExistsWarning" ), //$NON-NLS-1$
          new Object[] { filename } );
      getStatusBar().setStatus( StatusType.WARNING, message );
    }
    return true;
  }

  protected boolean performConfirm() {
    final String filename = getFilename();
    final File f = new File( filename );
    if ( f.exists() ) {
      final String key1 = "excelexportdialog.targetOverwriteConfirmation"; //$NON-NLS-1$
      final String key2 = "excelexportdialog.targetOverwriteTitle"; //$NON-NLS-1$
      if ( JOptionPane.showConfirmDialog( this, MessageFormat.format( getResources().getString( key1 ),
          new Object[] { getFilename() } ), getResources().getString( key2 ), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE ) == JOptionPane.NO_OPTION ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Initialises the Excel export dialog from the settings in the report configuration.
   *
   * @param config
   *          the report configuration.
   */
  protected void setDialogContents( final Configuration config ) {
    final String strict =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.StrictLayout" ); //$NON-NLS-1$
    setStrictLayout( "true".equals( strict ) ); //$NON-NLS-1$

    final String defaultFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.xls.FileName" ); //$NON-NLS-1$
    if ( defaultFileName != null ) {
      setFilename( resolvePath( defaultFileName ).getAbsolutePath() );
    }
  }

  /**
   * Returns a new (and not connected to the default config from the job) configuration containing all properties from
   * the dialog.
   *
   * @param full
   * @return
   */
  protected Configuration grabDialogContents( final boolean full ) {
    final DefaultConfiguration p = new DefaultConfiguration();

    if ( full ) {
      p.setProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.xls.FileName", getFilename() ); //$NON-NLS-1$
    }
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.StrictLayout", String
        .valueOf( isStrictLayout() ) ); //$NON-NLS-1$
    return p;
  }

  protected String getResourceBaseName() {
    return ExcelExportPlugin.BASE_RESOURCE_CLASS;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.xls."; //$NON-NLS-1$
  }
}
