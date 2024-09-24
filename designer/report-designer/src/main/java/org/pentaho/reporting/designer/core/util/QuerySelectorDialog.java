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

package org.pentaho.reporting.designer.core.util;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.FixDefaultListCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuerySelectorDialog extends CommonDialog {
  private class MouseHandler extends MouseAdapter {
    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1 ) {
        setConfirmed( true );
        QuerySelectorDialog.this.dispose();
      }
    }
  }

  private class SyntaxHighlightAction implements ActionListener {
    private SyntaxHighlightAction() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final Object o = syntaxModel.getSelectedKey();
      if ( o instanceof String ) {
        textArea.setSyntaxEditingStyle( (String) o );
      }
    }
  }

  private JList fieldList;
  private RSyntaxTextArea textArea;
  private KeyedComboBoxModel<String, String> syntaxModel;
  private JTabbedPane tab;

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public QuerySelectorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public QuerySelectorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public QuerySelectorDialog()
    throws HeadlessException {
    init();
  }

  protected void init() {
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    fieldList = new JList();
    fieldList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fieldList.setVisibleRowCount( 5 );
    fieldList.setCellRenderer( new FixDefaultListCellRenderer() );
    fieldList.addMouseListener( new MouseHandler() );

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

    textArea = new RSyntaxTextArea();
    textArea.setBracketMatchingEnabled( true );
    textArea.setSyntaxEditingStyle( RSyntaxTextArea.SYNTAX_STYLE_JAVA );
    textArea.setColumns( 60 );
    textArea.setRows( 20 );

    final JPanel syntaxSelectionPane = new JPanel();
    syntaxSelectionPane.setLayout( new FlowLayout() );
    final JComboBox syntaxBox = new JComboBox( syntaxModel );
    syntaxBox.addActionListener( new SyntaxHighlightAction() );
    syntaxSelectionPane.add( syntaxBox );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( new RTextScrollPane( 500, 300, textArea, true ), BorderLayout.CENTER );
    contentPane.add( syntaxBox, BorderLayout.NORTH );

    tab = new JTabbedPane();
    tab.addTab( UtilMessages.getInstance().getString( "QuerySelectorDialog.DefinedQueries" ),
      new JScrollPane( fieldList ) );
    tab.addTab( UtilMessages.getInstance().getString( "QuerySelectorDialog.CustomQuery" ), contentPane );

    setTitle( UtilMessages.getInstance().getString( "QuerySelectorDialog.SelectQuery" ) );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.QuerySelector";
  }

  protected Component createContentPane() {
    return tab;
  }

  public void setQueries( final String[] queries, final String reportQuery ) {
    final DefaultListModel listModel = new DefaultListModel();
    boolean found = false;
    for ( int i = 0; i < queries.length; i++ ) {
      final String query = queries[ i ];
      if ( ObjectUtilities.equal( query, reportQuery ) ) {
        found = true;
      }
      listModel.addElement( query );
    }
    fieldList.setModel( listModel );
    fieldList.setSelectedValue( reportQuery, true );
    this.textArea.setText( reportQuery );
    if ( found == false ) {
      tab.setSelectedIndex( 1 );
    } else {
      tab.setSelectedIndex( 0 );
    }
  }

  public String performEdit( final String[] queries, final String selectedQuery ) {
    setQueries( queries, selectedQuery );

    if ( performEdit() == false ) {
      return selectedQuery;
    }

    if ( tab.getSelectedIndex() == 1 ) {
      return textArea.getText();
    } else {
      return ( (String) fieldList.getSelectedValue() );
    }
  }
}
