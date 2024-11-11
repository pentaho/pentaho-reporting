/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SettingsDialog extends JDialog {
  private static final ValidationMessage.Severity[] ALL_SEVERITIES = new ValidationMessage.Severity[]
    { ValidationMessage.Severity.WARN, ValidationMessage.Severity.ERROR };


  private class ApplyAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ApplyAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "SettingsDialog.Apply" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < settingsPlugins.size(); i++ ) {
        final SettingsPlugin settingsPanel = settingsPlugins.get( i );
        final ValidationResult validationResult = settingsPanel.validate( new ValidationResult() );
        final ValidationMessage[] validationMessages = validationResult.getValidationMessages( ALL_SEVERITIES );

        if ( validationMessages.length == 0 ) {
          continue;
        }

        final StringBuilder messages = new StringBuilder( 100 );
        for ( final ValidationMessage validationMessage : validationMessages ) {
          messages.append( validationMessage.getMessage() );
          messages.append( '\n' );
        }
        JOptionPane.showMessageDialog( SettingsDialog.this, messages,
          SettingsMessages.getInstance().getString( "SettingsDialog.ErrorTitle" ), JOptionPane.ERROR_MESSAGE );
        return;
      }

      for ( int i = 0; i < settingsPlugins.size(); i++ ) {
        final SettingsPlugin settingsPanel = settingsPlugins.get( i );
        settingsPanel.apply();
      }

      dispose();
    }
  }

  private class ResetAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ResetAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "SettingsDialog.Reset" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < settingsPlugins.size(); i++ ) {
        final SettingsPlugin settingsPanel = settingsPlugins.get( i );
        settingsPanel.reset();
      }
    }
  }

  private class CancelAction extends AbstractAction {
    private CancelAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "SettingsDialog.Cancel" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < settingsPlugins.size(); i++ ) {
        final SettingsPlugin settingsPanel = settingsPlugins.get( i );
        settingsPanel.reset();
      }

      dispose();
    }
  }

  private NetworkSettingsPanel networkSettingsPanel;
  private ArrayList<SettingsPlugin> settingsPlugins;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner.  A shared, hidden
   * frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see GraphicsEnvironment#isHeadless
   * @see JComponent#getDefaultLocale
   */
  public SettingsDialog()
    throws HeadlessException {
    init();
  }

  public SettingsDialog( final Frame owner ) {
    super( owner );
    init();
  }


  public SettingsDialog( final Dialog owner ) {
    super( owner );
    init();
  }


  private void init() {
    setTitle( SettingsMessages.getInstance().getString( "SettingsDialog.Title" ) );
    setModal( true );

    networkSettingsPanel = new NetworkSettingsPanel();

    settingsPlugins = new ArrayList<SettingsPlugin>();
    settingsPlugins.add( new GeneralSettingsPanel() );
    settingsPlugins.add( networkSettingsPanel );
    settingsPlugins.add( new BrowserSettingsPanel() );
    settingsPlugins.add( new ExternalToolSettingsPanel() );
    settingsPlugins.add( new StorageLocationSettingsPanel() );
    settingsPlugins.add( new ColorSettingsPanel() );

    final ButtonTabbedPane listTabbedPane = new ButtonTabbedPane();
    for ( int i = 0; i < settingsPlugins.size(); i++ ) {
      final SettingsPlugin settingsPlugin = settingsPlugins.get( i );
      listTabbedPane.addTab( settingsPlugin.getIcon(), settingsPlugin.getTitle(),
        new JScrollPane( settingsPlugin.getComponent() ) );
    }
    listTabbedPane.showFirst();

    final JButton applyButton = new JButton( new ApplyAction() );

    final JPanel buttonPane = new JPanel( new GridLayout( 1, 3, 5, 5 ) );
    buttonPane.add( applyButton );
    buttonPane.add( new JButton( new ResetAction() ) );
    buttonPane.add( new JButton( new CancelAction() ) );

    final JPanel buttonCarrier = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    buttonCarrier.add( buttonPane );

    final JPanel centerPanel = new JPanel( new BorderLayout() );
    centerPanel.add( listTabbedPane, BorderLayout.CENTER );
    centerPanel.add( buttonCarrier, BorderLayout.SOUTH );

    setContentPane( centerPanel );

    getRootPane().getInputMap().put( KeyStroke.getKeyStroke( "ESCAPE" ), "cancel" );//NON-NLS
    getRootPane().getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ), "apply" );//NON-NLS
    getRootPane().getActionMap().put( "apply", new ApplyAction() );
    getRootPane().getActionMap().put( "cancel", new CancelAction() );
    getRootPane().setDefaultButton( applyButton );

    pack();
    GUIUtils.ensureMinimumDialogSize( this, 500, 400 );
    LibSwingUtil.centerFrameOnScreen( this );
  }

  public void performEdit( final ReportDesignerContext context ) {
    try {
      networkSettingsPanel.setReportDesignerContext( context );
      setVisible( true );
    } finally {
      networkSettingsPanel.setReportDesignerContext( null );
    }
  }
}
