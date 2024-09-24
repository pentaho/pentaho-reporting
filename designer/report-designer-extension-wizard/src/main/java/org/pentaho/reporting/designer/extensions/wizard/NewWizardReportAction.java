/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.extensions.wizard;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.OutputStream;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.EmbeddedWizard;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;

public class NewWizardReportAction extends AbstractDesignerContextAction {

  private static final String TRANSLATIONS_PROPERTIES = "translations.properties";

  public NewWizardReportAction() {
    putValue( Action.NAME, Messages.getString( "NewWizardReportAction.MenuTitle" ) ); //$NON-NLS-1$
    putValue( "WIZARD.BUTTON.TEXT", Messages.getString( "NewWizardReportAction.ButtonTitle" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    putValue( Action.ACCELERATOR_KEY, Messages.getOptionalKeyStroke( "NewWizardReportAction.Accelerator" ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext designerContext = getReportDesignerContext();
    if ( designerContext == null ) {
      return;
    }
    final Component parent = designerContext.getView().getParent();
    final Window window = getWindowAncestor( parent );
    final EmbeddedWizard dialog = getEmbeddedWizard( window );

    try {
      final MasterReport def = runDialog( dialog );
      if ( def == null ) {
        return;
      }

      try {
        final MemoryDocumentBundle bundle = (MemoryDocumentBundle) def.getBundle();
        if ( bundle.isEntryExists( TRANSLATIONS_PROPERTIES ) == false ) {
          final String defaultMessage = ActionMessages.getString( "Translations.DefaultContent" );
          final OutputStream outputStream = bundle.createEntry( TRANSLATIONS_PROPERTIES, "text/plain" );
          outputStream.write( defaultMessage.getBytes( "ISO-8859-1" ) );
          outputStream.close();
          bundle.getWriteableDocumentMetaData().setEntryAttribute( TRANSLATIONS_PROPERTIES,
              BundleUtilities.STICKY_FLAG, "true" );
        }
      } catch ( Exception ex ) {
        // ignore, its not that important ..
        DebugLog.log( "Failed to created default translation entry", ex );
      }

      designerContext.addMasterReport( def );
    } catch ( ReportProcessingException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }

  /**
   * @param parent
   *          - a Component
   * @return Window
   * 
   *         Method replaced inline code to facilitate testing.
   */
  Window getWindowAncestor( Component parent ) {
    return LibSwingUtil.getWindowAncestor( parent );
  }

  /**
   * @param window
   * @return
   * 
   *         Method replaced inline code to facilitate testing.
   */
  EmbeddedWizard getEmbeddedWizard( Window window ) {
    final EmbeddedWizard dialog;
    if ( window instanceof JDialog ) {
      dialog = new EmbeddedWizard( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new EmbeddedWizard( (JFrame) window );
    } else {
      dialog = new EmbeddedWizard();
    }

    return dialog;
  }

  /**
   * @param dialog
   * @return - A master report
   * @throws ReportProcessingException
   * 
   *           Method replaced inline code to facilitate testing.
   * 
   */
  MasterReport runDialog( EmbeddedWizard dialog ) throws ReportProcessingException {
    return (MasterReport) dialog.run( null );
  }
}
