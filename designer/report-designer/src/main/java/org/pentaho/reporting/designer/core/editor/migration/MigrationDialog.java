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


package org.pentaho.reporting.designer.core.editor.migration;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.report.SaveReportAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityConverter;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityConverterRegistry;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityUpdater;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverterUtilities;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class MigrationDialog extends CommonDialog {
  private ReportDocumentContext reportRenderContext;
  private JLabel warningLabel;
  private JEditorPane textArea;
  private int fromVersion;
  private int toVersion;

  public MigrationDialog() {
    init();
  }

  public MigrationDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public MigrationDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    warningLabel = new JLabel();

    textArea = new JEditorPane();
    textArea.setEditable( false );
    textArea.setBackground( new Color( 0, 0, 0, 0 ) );
    textArea.setOpaque( false );
    textArea.setEditorKit( new HTMLEditorKit() );

    setTitle( Messages.getString( "MigrationDialog.Title" ) );

    super.init();
  }

  protected void performInitialResize() {
    setSize( 750, 550 );
    LibSwingUtil.centerDialogInParent( this );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.Migration";
  }

  protected Document createDescriptionText() throws IOException, BadLocationException {
    final StringBuilder sb = new StringBuilder( 10000 );
    sb.append( "<html><head></head><body>\n" );//NON-NLS
    final CompatibilityConverterRegistry registry = CompatibilityConverterRegistry.getInstance();
    final CompatibilityConverter[] converters = registry.getConverters();
    for ( int i = 0; i < converters.length; i++ ) {
      final CompatibilityConverter converter = converters[ i ];
      if ( toVersion > 0 && converter.getTargetVersion() <= toVersion ) {
        sb.append( converter.getUpgradeDescription( Locale.getDefault() ) );
      }
    }
    sb.append( "</body></html>" );

    return RichTextConverterUtilities.parseDocument( textArea.getEditorKit(), sb.toString() );
  }

  protected Component createContentPane() {
    final JPanel migrationPanel = new JPanel();
    migrationPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    migrationPanel.setLayout( new BorderLayout() );
    migrationPanel.add( warningLabel, BorderLayout.CENTER );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( new JScrollPane( textArea ), BorderLayout.CENTER );
    panel.add( migrationPanel, BorderLayout.SOUTH );
    return panel;
  }

  public void performMigration( final ReportDesignerContext designerContext,
                                final ReportDocumentContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;

    toVersion = ClassicEngineBoot.computeCurrentVersionId();

    final MasterReport masterReportElement = this.reportRenderContext.getContextRoot();
    final Integer compatibilityLevel = masterReportElement.getCompatibilityLevel();
    if ( compatibilityLevel == null ) {
      fromVersion = toVersion;
    } else {
      fromVersion = compatibilityLevel;
    }

    setupFromReportVersion();
    if ( super.performEdit() == false ) {
      return;
    }

    if ( ( new SaveReportAction() ).saveReport( designerContext, reportRenderContext, this ) == false ) {
      return;
    }

    final CompatibilityUpdater updater = new CompatibilityUpdater();
    updater.performUpdate( reportRenderContext.getContextRoot() );
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    if ( onConfirm == false ) {
      return true;
    }

    if ( fromVersion != toVersion ) {
      if ( JOptionPane.showConfirmDialog( this,
        Messages.getString( "MigrationDialog.Confirm.Message" ),
        Messages.getString( "MigrationDialog.Confirm.Title" ),
        JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION ) {
        return true;
      }
    }
    return false;
  }

  private void setupFromReportVersion() {
    warningLabel.setText( Messages.getString( "MigrationDialog.WarningLabel",
      ClassicEngineBoot.printVersion( fromVersion ), ClassicEngineBoot.printVersion( toVersion ) ) );
    try {
      textArea.setDocument( createDescriptionText() );
    } catch ( IOException e ) {
      textArea.setText( Messages.getString( "MigrationDialog.ErrorReadingDescription" ) );
    } catch ( BadLocationException e ) {
      e.printStackTrace();
    }
  }
}
