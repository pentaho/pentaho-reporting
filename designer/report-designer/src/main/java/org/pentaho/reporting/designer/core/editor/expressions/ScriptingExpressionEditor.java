/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
