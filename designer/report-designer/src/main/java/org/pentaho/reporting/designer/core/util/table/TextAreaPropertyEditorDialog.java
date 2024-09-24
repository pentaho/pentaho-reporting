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

package org.pentaho.reporting.designer.core.util.table;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.BasicTextPropertyEditorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextAreaPropertyEditorDialog extends BasicTextPropertyEditorDialog {
  private class SyntaxHighlightAction implements ActionListener {
    private SyntaxHighlightAction() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final Object o = syntaxModel.getSelectedKey();
      if ( o instanceof String ) {
        final RSyntaxTextArea textArea = (RSyntaxTextArea) getTextArea();
        textArea.setSyntaxEditingStyle( (String) o );
      }
    }
  }

  private KeyedComboBoxModel<String, String> syntaxModel;

  public TextAreaPropertyEditorDialog()
    throws HeadlessException {
    init();
  }

  public TextAreaPropertyEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public TextAreaPropertyEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    syntaxModel = new KeyedComboBoxModel<String, String>();
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_NONE ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.None" ) );
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_JAVA ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.Java" ) );
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.JavaScript" ) );
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_GROOVY ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.Groovy" ) );
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_HTML ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.Html" ) );
    syntaxModel
      .add( ( SyntaxConstants.SYNTAX_STYLE_CSS ), UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.CSS" ) );
    syntaxModel
      .add( ( SyntaxConstants.SYNTAX_STYLE_SQL ), UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.SQL" ) );
    syntaxModel
      .add( ( SyntaxConstants.SYNTAX_STYLE_XML ), UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.XML" ) );
    syntaxModel.add( ( SyntaxConstants.SYNTAX_STYLE_PYTHON ),
      UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.Python" ) );
    syntaxModel
      .add( ( SyntaxConstants.SYNTAX_STYLE_TCL ), UtilMessages.getInstance().getString( "RSyntaxAreaLanguages.TCL" ) );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.TextAreaPropertyEditor";
  }

  protected JTextArea createTextArea() {
    final RSyntaxTextArea textArea = new RSyntaxTextArea();
    textArea.setBracketMatchingEnabled( true );
    textArea.setSyntaxEditingStyle( RSyntaxTextArea.SYNTAX_STYLE_JAVA );
    textArea.setColumns( 60 );
    textArea.setRows( 20 );
    textArea.getDocument().addDocumentListener( new DocumentUpdateHandler() );
    return textArea;
  }

  protected Component createContentPane() {
    final JComboBox syntaxBox = new JComboBox( syntaxModel );
    syntaxBox.addActionListener( new SyntaxHighlightAction() );

    final JPanel syntaxSelectionPane = new JPanel();
    syntaxSelectionPane.setLayout( new FlowLayout() );
    syntaxSelectionPane.add( syntaxBox );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( new RTextScrollPane( 500, 300, (RSyntaxTextArea) getTextArea(), true ), BorderLayout.CENTER );
    contentPane.add( syntaxBox, BorderLayout.NORTH );

    return contentPane;
  }

}
