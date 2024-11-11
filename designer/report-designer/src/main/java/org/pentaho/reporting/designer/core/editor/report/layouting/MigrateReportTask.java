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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.report.MigrateReportAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MigrateReportTask implements Runnable {
  private enum UserInput {
    Migrate, Clear, Cancel
  }

  private static class MigrateConfirmationDialog extends CommonDialog {
    private class SelectionAction extends AbstractAction {
      private UserInput option;

      private SelectionAction( final String name,
                               final UserInput option ) {
        super( name );
        this.option = option;
      }

      public void actionPerformed( final ActionEvent e ) {
        input = option;
        dispose();
      }
    }

    private UserInput input;

    private MigrateConfirmationDialog() {
      init();
    }

    private MigrateConfirmationDialog( final Frame owner ) throws HeadlessException {
      super( owner );
      init();
    }

    private MigrateConfirmationDialog( final Dialog owner ) throws HeadlessException {
      super( owner );
      init();
    }

    protected void init() {
      setTitle( Messages.getString( "MigrateReportTask.Title" ) );
      setModal( true );
      super.init();
    }

    protected void performInitialResize() {
      setSize( 500, 200 );
      LibSwingUtil.centerDialogInParent( this );
    }

    protected Action getConfirmAction() {
      return null;
    }

    protected String getDialogId() {
      return "MigrateReportTask.MigrateConfirmationDialog"; // NON-NLS
    }

    protected Component createContentPane() {
      final JEditorPane textArea = new JEditorPane();
      textArea.setEditable( false );
      textArea.setBackground( new Color( 0, 0, 0, 0 ) );
      textArea.setOpaque( false );
      textArea.setEditorKit( new HTMLEditorKit() );
      textArea.setText( Messages.getString( "MigrateReportTask.Message",
        ClassicEngineBoot.printVersion( ClassicEngineBoot.computeCurrentVersionId() ) ) );

      final JPanel panel = new JPanel( new BorderLayout() );
      panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
      panel.add( textArea );
      return panel;
    }

    protected JPanel createButtonsPane() {
      final JButton migrateButton =
        new JButton( new SelectionAction( Messages.getString( "MigrateReportTask.Migrate" ), UserInput.Migrate ) );
      final JButton clearButton =
        new JButton( new SelectionAction( Messages.getString( "MigrateReportTask.Clear" ), UserInput.Clear ) );

      final JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );

      if ( !MacOSXIntegration.MAC_OS_X ) {
        buttonsPanel.add( migrateButton );
        buttonsPanel.add( clearButton );
      }

      if ( hasCancelButton() ) {
        buttonsPanel.add( new JButton( getCancelAction() ) );
      }

      if ( MacOSXIntegration.MAC_OS_X ) {
        buttonsPanel.add( migrateButton );
        buttonsPanel.add( clearButton );
      }

      return buttonsPanel;
    }

    public UserInput performSelection() {
      input = UserInput.Cancel;
      super.performEdit();
      return input;
    }
  }

  private final ReportDesignerContext designerContext;
  private final ReportDocumentContext reportContext;
  private final long minimumVersionNeeded;

  public MigrateReportTask( final ReportDesignerContext designerContext,
                            final ReportDocumentContext reportContext,
                            final long minimumVersionNeeded ) {


    this.designerContext = designerContext;
    this.reportContext = reportContext;
    this.minimumVersionNeeded = minimumVersionNeeded;
  }

  public void run() {
    final MasterReport masterReportElement = reportContext.getContextRoot();
    final Integer compatibilityLevel = masterReportElement.getCompatibilityLevel();
    if ( compatibilityLevel == null ) {
      return;
    }
    if ( compatibilityLevel.intValue() > minimumVersionNeeded ) {
      // already done.
      return;
    }

    final Window window = LibSwingUtil.getWindowAncestor( designerContext.getView().getParent() );
    final MigrateConfirmationDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new MigrateConfirmationDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new MigrateConfirmationDialog( (JFrame) window );
    } else {
      dialog = new MigrateConfirmationDialog();
    }

    final UserInput userInput = dialog.performSelection();
    if ( userInput == UserInput.Clear ) {
      masterReportElement.setCompatibilityLevel( null );
    } else if ( userInput == UserInput.Migrate ) {
      MigrateReportAction.migrateReport( designerContext );
    }
  }
}
