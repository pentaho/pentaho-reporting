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

package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.ExpressionEditorPane;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class ConditionalVisibilityDialog extends CommonDialog {
  private ExpressionEditorPane editorPane;

  public ConditionalVisibilityDialog()
    throws HeadlessException {
    init();
  }

  public ConditionalVisibilityDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ConditionalVisibilityDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "ConditionalVisibilityDialog.HideObject" ) );
    setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    setLayout( new BorderLayout() );

    editorPane = new ExpressionEditorPane();
    setModal( true );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ConditionalVisibility";
  }

  protected Component createContentPane() {
    final JPanel floatingPanel = new JPanel();
    floatingPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    floatingPanel.add( new JLabel( Messages.getString( "ConditionalVisibilityDialog.Condition" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    floatingPanel.add( editorPane, gbc );
    return floatingPanel;
  }

  public Expression performEdit( final Expression expression ) {
    editorPane.setValue( expression );

    if ( performEdit() == false ) {
      return null;
    }

    return editorPane.getValue();
  }

  public ReportDesignerContext getReportDesignerContext() {
    return editorPane.getReportDesignerContext();
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    editorPane.setReportDesignerContext( reportDesignerContext );
  }
}
