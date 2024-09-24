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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public abstract class ScriptingExpressionEditor implements ExpressionEditor {
  private JPanel panel;
  private RSyntaxTextArea textArea;
  private JLabel statusText;

  public ScriptingExpressionEditor() {
    statusText = new JLabel();

    textArea = new RSyntaxTextArea();
    textArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    final JPanel queryContentHolder = new JPanel( new BorderLayout() );
    queryContentHolder.add( BorderLayout.NORTH,
      new JLabel( EditorExpressionsMessages.getString( "ScriptingExpressionEditor.Script" ) ) );
    queryContentHolder.add( BorderLayout.CENTER, new RTextScrollPane( 500, 300, textArea, true ) );


    panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( queryContentHolder, BorderLayout.CENTER );
  }

  protected void addValidateButton( final Action action ) {
    final JPanel validatePanel = new JPanel();
    validatePanel.setLayout( new BorderLayout() );
    validatePanel.add( new JButton( action ), BorderLayout.EAST );
    validatePanel.add( statusText, BorderLayout.CENTER );

    panel.add( validatePanel, BorderLayout.SOUTH );
  }

  public String getStatus() {
    return statusText.getText();
  }

  public void setStatus( final String text ) {
    statusText.setText( text );
  }

  public String getText() {
    return textArea.getText();
  }

  public void setText( final String t ) {
    textArea.setText( t );
  }

  public String getSyntaxEditingStyle() {
    return textArea.getSyntaxEditingStyle();
  }

  public void setSyntaxEditingStyle( final String styleKey ) {
    textArea.setSyntaxEditingStyle( styleKey );
  }

  public JPanel getPanel() {
    return panel;
  }

  public JComponent getEditorComponent() {
    return panel;
  }

  public String getTitle() {
    return EditorExpressionsMessages.getString( "ScriptingExpressionEditor.Script" );
  }
}
