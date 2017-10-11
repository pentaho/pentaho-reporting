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

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.GuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.DefaultGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.FormValidator;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStoreException;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public abstract class AbstractExportDialog extends JDialog implements ExportDialog {
  private static final Log logger = LogFactory.getLog( AbstractExportDialog.class );

  /**
   * Internal action class to confirm the dialog and to validate the input.
   */
  private class ConfirmAction extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ConfirmAction( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "OptionPane.okButtonText" ) ); //$NON-NLS-1$
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( performValidate() && performConfirm() ) {
        applyReportParameters();
        setConfirmed( true );
        setVisible( false );
      }
    }

    private void applyReportParameters() {
      final ReportParameterDefinition theParamterDefinition = reportJob.getParameterDefinition();
      if ( theParamterDefinition.getParameterCount() > 0 ) {
        final ReportParameterValues properties = parametersPanel.getReportParameterValues();
        final ReportParameterValues reportParameters = reportJob.getParameterValues();
        final String[] strings = properties.getColumnNames();
        for ( int i = 0; i < strings.length; i++ ) {
          final String propertyName = strings[i];
          reportParameters.put( propertyName, properties.get( propertyName ) );
        }
      }
    }
  }

  /**
   * Internal action class to cancel the report processing.
   */
  private class CancelAction extends AbstractAction {
    /**
     * Default constructor.
     */
    protected CancelAction( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "OptionPane.cancelButtonText" ) ); //$NON-NLS-1$
      putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ) );
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      setConfirmed( false );
      setVisible( false );
    }
  }

  private class ExportDialogValidator extends FormValidator {
    protected ExportDialogValidator() {
      super();
    }

    public boolean performValidate() {
      return AbstractExportDialog.this.performValidate();
    }

    public Action getConfirmAction() {
      return AbstractExportDialog.this.getConfirmAction();
    }
  }

  private class WindowCloseHandler extends WindowAdapter {
    protected WindowCloseHandler() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      final Action cancelAction = getCancelAction();
      if ( cancelAction != null ) {
        cancelAction.actionPerformed( null );
      } else {
        setConfirmed( false );
        setVisible( false );
      }
    }
  }

  private Action cancelAction;
  private Action confirmAction;
  private FormValidator formValidator;
  private ResourceBundle resources;
  private boolean confirmed;
  private MasterReport reportJob;
  private GuiContext guiContext;
  private GuiContext defaultContext;
  private Messages messages;
  private JPanel parametersLayoutPanel;
  private ParameterReportControllerPane parametersPanel;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner. A shared, hidden frame
   * will be set as the owner of the dialog.
   */
  protected AbstractExportDialog() {
    initialize();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   */
  protected AbstractExportDialog( final Frame owner ) {
    super( owner );
    initialize();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   */
  protected AbstractExportDialog( final Dialog owner ) {
    super( owner );
    initialize();
  }

  private void initialize() {

    defaultContext = new DefaultGuiContext( this, null );
    guiContext = defaultContext;

    final ResourceBundle resources = ResourceBundle.getBundle( SwingCommonModule.BUNDLE_NAME );
    cancelAction = new CancelAction( resources );
    confirmAction = new ConfirmAction( resources );

    formValidator = new ExportDialogValidator();
    setModal( true );
    setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    addWindowListener( new WindowCloseHandler() );

    messages =
        new Messages( defaultContext.getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingCommonModule.class ) );

    parametersPanel = new ParameterReportControllerPane();
    parametersLayoutPanel = new JPanel( new BorderLayout() );
    parametersLayoutPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    parametersLayoutPanel.add( parametersPanel, BorderLayout.NORTH );
  }

  protected JPanel createContentPane( final JComponent realContent ) {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( createButtonPanel(), BorderLayout.SOUTH );
    contentPane.add( realContent, BorderLayout.CENTER );

    final JPanel contentWithStatus = new JPanel();
    contentWithStatus.setLayout( new BorderLayout() );
    contentWithStatus.add( contentPane, BorderLayout.CENTER );
    contentWithStatus.add( getStatusBar(), BorderLayout.SOUTH );

    return contentWithStatus;
  }

  protected JPanel createButtonPanel() {
    final JButton btnCancel = new JButton( getCancelAction() );
    final JButton btnConfirm = new JButton( getConfirmAction() );
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new GridLayout( 1, 2, 5, 5 ) );
    buttonPanel.add( btnConfirm );
    buttonPanel.add( btnCancel );
    btnConfirm.setDefaultCapable( true );
    getRootPane().setDefaultButton( btnConfirm );
    buttonPanel.registerKeyboardAction( getConfirmAction(), KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

    final JPanel buttonCarrier = new JPanel();
    buttonCarrier.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonCarrier.add( buttonPanel );
    return buttonCarrier;
  }

  public abstract JStatusBar getStatusBar();

  protected Action getCancelAction() {
    return cancelAction;
  }

  protected void setCancelAction( final Action cancelAction ) {
    this.cancelAction = cancelAction;
  }

  protected Action getConfirmAction() {
    return confirmAction;
  }

  protected void setConfirmAction( final Action confirmAction ) {
    this.confirmAction = confirmAction;
  }

  protected abstract boolean performValidate();

  protected FormValidator getFormValidator() {
    return formValidator;
  }

  protected void initializeFromJob( final MasterReport job, final GuiContext guiContext ) {
    final JStatusBar statusBar = getStatusBar();
    if ( statusBar != null ) {
      statusBar.setIconTheme( guiContext.getIconTheme() );
    }
  }

  protected MasterReport getReportJob() {
    return reportJob;
  }

  protected GuiContext getGuiContext() {
    return guiContext;
  }

  /**
   * Opens the dialog to query all necessary input from the user. This will not start the processing, as this is done
   * elsewhere.
   *
   * @param reportJob
   *          the report that should be processed.
   * @return true, if the processing should continue, false otherwise.
   */
  public boolean performQueryForExport( final MasterReport reportJob, final SwingGuiContext guiContext ) {
    if ( reportJob == null ) {
      throw new NullPointerException();
    }
    if ( guiContext == null ) {
      throw new NullPointerException();
    }

    this.reportJob = reportJob;
    this.guiContext = guiContext;

    final Locale locale = guiContext.getLocale();
    setLocale( locale );
    pack();
    clear();
    initializeFromJob( reportJob, guiContext );
    createParametersPanelContent();

    final FormValidator formValidator = getFormValidator();
    formValidator.setEnabled( false );
    final ModifiableConfiguration repConf = reportJob.getReportConfiguration();
    final boolean inputStorageEnabled = isInputStorageEnabled( repConf );

    final Configuration loadedConfiguration;
    if ( inputStorageEnabled ) {
      loadedConfiguration = loadFromConfigStore( reportJob, repConf );
    } else {
      loadedConfiguration = repConf;
    }

    setDialogContents( loadedConfiguration );

    formValidator.setEnabled( true );
    formValidator.handleValidate();
    setModal( true );
    LibSwingUtil.centerDialogInParent( this );
    setVisible( true );
    if ( isConfirmed() == false ) {
      this.guiContext = defaultContext;
      return false;
    }

    formValidator.setEnabled( false );

    final Configuration fullDialogContents = grabDialogContents( true );
    final Enumeration configProperties = fullDialogContents.getConfigProperties();
    while ( configProperties.hasMoreElements() ) {
      final String key = (String) configProperties.nextElement();
      repConf.setConfigProperty( key, fullDialogContents.getConfigProperty( key ) );
    }

    if ( inputStorageEnabled ) {
      saveToConfigStore( reportJob, repConf );
    }

    formValidator.setEnabled( true );
    this.reportJob = null;
    this.guiContext = defaultContext;
    return true;
  }

  private void createParametersPanelContent() {
    final ReportParameterDefinition theParamterDefinition = reportJob.getParameterDefinition();
    if ( theParamterDefinition.getParameterCount() > 0 ) {
      try {
        parametersPanel.hideControls();
        parametersPanel.setReport( reportJob );
      } catch ( ReportProcessingException e ) {
        parametersPanel.setErrorMessage( messages.getString( "AbstractExportDialog.ERROR_PARAMETERS" ) );
      }
    } else {
      parametersPanel.setErrorMessage( messages.getString( "AbstractExportDialog.NO_PARAMETERS" ) );
    }
  }

  private void saveToConfigStore( final MasterReport reportJob, final Configuration reportConfiguration ) {
    final String configPath = ConfigFactory.encodePath( reportJob.getTitle() + getConfigurationSuffix() );

    try {
      final boolean fullStorageEnabled = isFullInputStorageEnabled( reportConfiguration );
      final Configuration dialogContents = grabDialogContents( fullStorageEnabled );
      final ConfigStorage storage = ConfigFactory.getInstance().getUserStorage();
      storage.store( configPath, dialogContents );
    } catch ( ConfigStoreException cse ) {
      AbstractExportDialog.logger.debug( messages.getString( "AbstractExportDialog.DEBUG_CANT_STORE_DEFAULTS", String
          .valueOf( getClass() ) ) ); //$NON-NLS-1$//$NON-NLS-2$
    }
  }

  private Configuration loadFromConfigStore( final MasterReport reportJob, final Configuration defaultConfig ) {
    final String configPath = ConfigFactory.encodePath( reportJob.getTitle() + getConfigurationSuffix() );
    final ConfigStorage storage = ConfigFactory.getInstance().getUserStorage();
    try {
      return storage.load( configPath, defaultConfig );
    } catch ( Exception cse ) {
      AbstractExportDialog.logger.debug( messages.getString( "AbstractExportDialog.DEBUG_CANT_LOAD_DEFAULTS", String
          .valueOf( getClass() ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return defaultConfig;
  }

  protected abstract String getConfigurationPrefix();

  /**
   * Returns a new (and not connected to the default config from the job) configuration containing all properties from
   * the dialog.
   *
   * @param full
   * @return
   */
  protected abstract Configuration grabDialogContents( boolean full );

  protected abstract void setDialogContents( Configuration properties );

  protected abstract String getConfigurationSuffix();

  /**
   * Retrieves the resources for this dialog. If the resources are not initialized, they get loaded on the first call to
   * this method.
   *
   * @return this frames ResourceBundle.
   */
  protected ResourceBundle getResources() {
    if ( resources == null ) {
      resources = ResourceBundle.getBundle( getResourceBaseName() );
    }
    return resources;
  }

  protected boolean isInputStorageEnabled( final Configuration config ) {
    if ( "true".equals( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.base.StoreExportDialogInputs" ) ) == false ) {
      return false;
    }

    final String confVal = config.getConfigProperty( getConfigurationPrefix() + "StoreDialogContents" ); //$NON-NLS-1$
    return "none".equalsIgnoreCase( confVal ) == false; //$NON-NLS-1$
  }

  protected boolean isFullInputStorageEnabled( final Configuration config ) {
    if ( "true".equals( config
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.base.StoreExportDialogInputs" ) ) == false ) {
      return false;
    }

    final String confVal = config.getConfigProperty( getConfigurationPrefix() + "StoreDialogContents" ); //$NON-NLS-1$
    return "all".equalsIgnoreCase( confVal ); //$NON-NLS-1$
  }

  /**
   * Returns <code>true</code> if the user confirmed the selection, and <code>false</code> otherwise. The file should
   * only be saved if the result is <code>true</code>.
   *
   * @return A boolean.
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Defines whether this dialog has been finished using the 'OK' or the 'Cancel' option.
   *
   * @param confirmed
   *          set to <code>true</code>, if OK was pressed, <code>false</code> otherwise
   */
  protected void setConfirmed( final boolean confirmed ) {
    this.confirmed = confirmed;
  }

  protected boolean performConfirm() {
    return true;
  }

  public abstract void clear();

  protected abstract String getResourceBaseName();

  /**
   * Resolves file names for the exports. An occurrence of "~/" at the beginning of the name will be replaced with the
   * users home directory.
   *
   * @param baseDirectory
   *          the base directory as specified in the configuration.
   * @return the file object pointing to that directory.
   * @throws IllegalArgumentException
   *           if the base directory is null.
   */
  protected File resolvePath( String baseDirectory ) {
    if ( baseDirectory == null ) {
      throw new IllegalArgumentException( messages.getString( "AbstractExportDialog.ERROR_0001_INVALID_BASE_DIR" ) ); //$NON-NLS-1$
    }

    if ( baseDirectory.startsWith( "~/" ) == false ) { //$NON-NLS-1$
      return new File( baseDirectory );
    } else {
      final String homeDirectory = System.getProperty( "user.home" ); //$NON-NLS-1$
      if ( "~/".equals( baseDirectory ) ) { //$NON-NLS-1$
        return new File( homeDirectory );
      } else {
        baseDirectory = baseDirectory.substring( 2 );
        return new File( homeDirectory, baseDirectory );
      }
    }
  }

  protected JPanel getParametersPanel() {
    return parametersLayoutPanel;
  }
}
