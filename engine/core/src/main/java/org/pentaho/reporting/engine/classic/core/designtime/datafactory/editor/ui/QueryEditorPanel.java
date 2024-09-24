/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.script.ScriptEngineFactory;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModelEvent;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModelListenerAdapter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;

public abstract class QueryEditorPanel<T> extends JPanel {
  private class QuerySelectedHandler implements ListSelectionListener {
    private QueryDialogModel<T> dialogModel;
    private JList queryNameList;

    private QuerySelectedHandler( final QueryDialogModel<T> dialogModel, final JList queryNameList ) {
      this.dialogModel = dialogModel;
      this.queryNameList = queryNameList;
    }

    public void valueChanged( final ListSelectionEvent e ) {
      dialogModel.setSelectedQuery( (Query<T>) queryNameList.getSelectedValue() );
    }
  }

  private class UpdateQueryScriptLanguageHandler implements ActionListener {
    private UpdateQueryScriptLanguageHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final Query<T> query = dialogModel.getSelectedQuery();
      if ( query == null ) {
        return;
      }

      String selectedQueryScriptLanguage = getSelectedQueryScriptLanguage();
      if ( ObjectUtilities.equal( selectedQueryScriptLanguage, query.getQueryLanguage() ) ) {
        return;
      }

      Query<T> newQuery = query.updateQueryScript( selectedQueryScriptLanguage, query.getQueryScript() );
      dialogModel.updateSelectedQuery( newQuery );
    }
  }

  private class UpdateGlobalScriptLanguageHandler implements ActionListener {
    private UpdateGlobalScriptLanguageHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      dialogModel.setGlobalScripting( getSelectedGlobalScriptLanguage(), dialogModel.getGlobalScript() );
    }
  }

  private class QueryNameUpdateHandler extends TextFieldBinding {
    private QueryNameUpdateHandler() {
    }

    protected void performUpdate() {
      final Query<T> selectedQuery = dialogModel.getSelectedQuery();
      if ( selectedQuery == null ) {
        return;
      }

      String text = queryNameTextField.getText();
      if ( ObjectUtilities.equal( text, selectedQuery.getName() ) ) {
        return;
      }
      dialogModel.updateSelectedQuery( selectedQuery.updateName( text ) );
    }
  }

  private class GlobalScriptUpdateHandler extends TextFieldBinding {
    private GlobalScriptUpdateHandler() {
    }

    protected void performUpdate() {
      String text = globalScriptTextArea.getText();
      if ( StringUtils.isEmpty( text ) ) {
        text = null;
      }
      if ( ObjectUtilities.equal( text, dialogModel.getGlobalScript() ) ) {
        return;
      }

      dialogModel.setGlobalScripting( getSelectedGlobalScriptLanguage(), text );
    }
  }

  private class QueryScriptUpdateHandler extends TextFieldBinding {
    private QueryScriptUpdateHandler() {
    }

    protected void performUpdate() {
      final Query<T> selectedQuery = dialogModel.getSelectedQuery();
      if ( selectedQuery == null ) {
        return;
      }

      String text = queryScriptTextArea.getText();
      if ( StringUtils.isEmpty( text ) ) {
        text = null;
      }
      if ( ObjectUtilities.equal( text, selectedQuery.getQueryScript() ) ) {
        return;
      }

      dialogModel.updateSelectedQuery( selectedQuery.updateQueryScript( getSelectedQueryScriptLanguage(), text ) );
    }
  }

  private class DialogModelChangesDispatcher<T> extends QueryDialogModelListenerAdapter<T> {
    private DialogModelChangesDispatcher() {
    }

    public void globalScriptChanged( final QueryDialogModelEvent<T> event ) {
      String globalScript = dialogModel.getGlobalScript();
      String globalScriptLanguage = dialogModel.getGlobalScriptLanguage();

      if ( ObjectUtilities.equal( getSelectedGlobalScriptLanguage(), globalScriptLanguage ) == false ) {
        setGlobalScriptingLanguage( globalScriptLanguage );

        ScriptEngineFactory globalLanguage = (ScriptEngineFactory) globalLanguageField.getSelectedItem();
        globalScriptTextArea.setSyntaxEditingStyle( DataFactoryEditorSupport
            .mapLanguageToSyntaxHighlighting( globalLanguage ) );
        queryLanguageListCellRenderer.setDefaultValue( globalLanguage );
      }
      if ( ObjectUtilities.equal( globalScriptTextArea.getText(), globalScript ) == false ) {
        globalScriptTextArea.setText( globalScript );
      }

      globalTemplateAction.update( (ScriptEngineFactory) globalLanguageField.getSelectedItem() );
    }

    public void queryUpdated( final QueryDialogModelEvent<T> event ) {
      if ( ObjectUtilities.equal( event.getNewQuery(), dialogModel.getSelectedQuery() ) ) {
        selectionChanged( event );
      }
    }

    public void selectionChanged( final QueryDialogModelEvent<T> event ) {
      Query<T> newQuery = event.getNewQuery();
      if ( newQuery == null ) {
        queryScriptTextArea.setEnabled( false );
        queryScriptTextArea.setText( null );
        queryLanguageField.setEnabled( false );
        setQueryScriptingLanguage( null );
        queryNameTextField.setEnabled( false );
        queryNameTextField.setText( null );
        queryNameList.setSelectedIndex( -1 );
      } else {
        queryScriptTextArea.setEnabled( true );
        queryScriptTextArea.setText( newQuery.getQueryScript() );
        queryLanguageField.setEnabled( true );
        setQueryScriptingLanguage( newQuery.getQueryLanguage() );
        queryNameTextField.setEnabled( true );
        queryNameTextField.setText( newQuery.getName() );

        queryNameList.setSelectedIndex( event.getNewIndex() );

        final ScriptEngineFactory queryScriptLanguage = (ScriptEngineFactory) queryLanguageField.getSelectedItem();
        if ( queryScriptLanguage == null ) {
          queryScriptTextArea.setSyntaxEditingStyle( globalScriptTextArea.getSyntaxEditingStyle() );
        } else {
          queryScriptTextArea.setSyntaxEditingStyle( DataFactoryEditorSupport
              .mapLanguageToSyntaxHighlighting( queryScriptLanguage ) );
        }
      }

      queryTemplateAction.update( (ScriptEngineFactory) queryLanguageField.getSelectedItem() );
      updateSelectedQueryFromModel();
    }
  }

  private QueryDialogModel<T> dialogModel;
  private JList queryNameList;
  private JTextField queryNameTextField;
  private SmartComboBox globalLanguageField;
  private RSyntaxTextArea globalScriptTextArea;
  private RSyntaxTextArea queryScriptTextArea;
  private SmartComboBox queryLanguageField;
  private QueryLanguageListCellRenderer queryLanguageListCellRenderer;
  private GlobalTemplateAction globalTemplateAction;
  private QueryTemplateAction queryTemplateAction;

  protected QueryEditorPanel( final QueryDialogModel<T> dialogModel ) {
    if ( dialogModel == null ) {
      throw new NullPointerException();
    }
    this.dialogModel = dialogModel;
    init();
  }

  @SuppressWarnings( "unchecked" )
  private void init() {
    globalTemplateAction = new GlobalTemplateAction( this, dialogModel );
    queryTemplateAction = new QueryTemplateAction( this, dialogModel );

    queryNameList = new JList( dialogModel.getQueries() );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.setCellRenderer( new QueryListCellRenderer() );
    queryNameList.addListSelectionListener( new QuerySelectedHandler( dialogModel, queryNameList ) );

    queryNameTextField = new JTextField();
    queryNameTextField.setColumns( 35 );
    queryNameTextField.setEnabled( dialogModel.isQuerySelected() );
    queryNameTextField.getDocument().addDocumentListener( new QueryNameUpdateHandler() );

    globalScriptTextArea = new RSyntaxTextArea();
    globalScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    globalScriptTextArea.getDocument().addDocumentListener( new GlobalScriptUpdateHandler() );

    globalLanguageField =
        new SmartComboBox( new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );
    globalLanguageField.setRenderer( new QueryLanguageListCellRenderer() );
    globalLanguageField.addActionListener( new UpdateGlobalScriptLanguageHandler() );

    queryScriptTextArea = new RSyntaxTextArea();
    queryScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    queryScriptTextArea.getDocument().addDocumentListener( new QueryScriptUpdateHandler() );

    queryLanguageField =
        new SmartComboBox( new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );

    queryLanguageListCellRenderer = new QueryLanguageListCellRenderer();
    queryLanguageField.setRenderer( queryLanguageListCellRenderer );
    queryLanguageField.addActionListener( new UpdateQueryScriptLanguageHandler() );

    dialogModel.addQueryDialogModelListener( new DialogModelChangesDispatcher() );

    initialize();
    createComponents();
  }

  protected abstract void initialize();

  public QueryDialogModel<T> getDialogModel() {
    return dialogModel;
  }

  private String getSelectedGlobalScriptLanguage() {
    Object selectedItem = globalLanguageField.getSelectedItem();
    if ( selectedItem instanceof ScriptEngineFactory == false ) {
      return null;
    }
    ScriptEngineFactory engineFactory = (ScriptEngineFactory) selectedItem;
    return engineFactory.getLanguageName();
  }

  private String getSelectedQueryScriptLanguage() {
    Object selectedItem = queryLanguageField.getSelectedItem();
    if ( selectedItem instanceof ScriptEngineFactory == false ) {
      return null;
    }
    ScriptEngineFactory engineFactory = (ScriptEngineFactory) selectedItem;
    return engineFactory.getLanguageName();
  }

  private void setQueryScriptingLanguage( final String lang ) {
    setScriptingLanguage( lang, queryLanguageField );
  }

  private void setGlobalScriptingLanguage( final String lang ) {
    setScriptingLanguage( lang, globalLanguageField );
  }

  protected void setScriptingLanguage( final String lang, final JComboBox languageField ) {
    if ( lang == null ) {
      languageField.setSelectedItem( null );
      return;
    }

    final ListModel model = languageField.getModel();
    for ( int i = 0; i < model.getSize(); i++ ) {
      final ScriptEngineFactory elementAt = (ScriptEngineFactory) model.getElementAt( i );
      if ( elementAt == null ) {
        continue;
      }
      if ( elementAt.getNames().contains( lang ) ) {
        languageField.setSelectedItem( elementAt );
        return;
      }
    }
  }

  private JPanel createQueryScriptTab() {
    final JPanel queryHeader2 = new JPanel( new BorderLayout() );
    queryHeader2.add( new JLabel( Messages.getString( "QueryEditorPanel.QueryScript" ) ), BorderLayout.CENTER );
    queryHeader2.add( new JButton( queryTemplateAction ), BorderLayout.EAST );

    final JPanel queryScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryScriptHeader.add( new JLabel( Messages.getString( "QueryEditorPanel.QueryScriptLanguage" ) ) );
    queryScriptHeader.add( queryLanguageField );
    queryScriptHeader.add( queryHeader2 );

    final JPanel queryScriptContentHolder = new JPanel( new BorderLayout() );
    queryScriptContentHolder.add( queryScriptHeader, BorderLayout.NORTH );
    queryScriptContentHolder.add( new RTextScrollPane( 700, 300, queryScriptTextArea, true ), BorderLayout.CENTER );
    return queryScriptContentHolder;
  }

  private JPanel createGlobalScriptTab() {
    final JPanel globalHeader2 = new JPanel( new BorderLayout() );
    globalHeader2.add( new JLabel( Messages.getString( "QueryEditorPanel.GlobalScript" ) ), BorderLayout.CENTER );
    globalHeader2.add( new JButton( globalTemplateAction ), BorderLayout.EAST );

    final JPanel globalScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    globalScriptHeader.add( new JLabel( Messages.getString( "QueryEditorPanel.GlobalScriptLanguage" ) ) );
    globalScriptHeader.add( globalLanguageField );
    globalScriptHeader.add( globalHeader2 );

    final JPanel globalScriptContentHolder = new JPanel( new BorderLayout() );
    globalScriptContentHolder.add( globalScriptHeader, BorderLayout.NORTH );
    globalScriptContentHolder.add( new RTextScrollPane( 700, 600, globalScriptTextArea, true ), BorderLayout.CENTER );
    globalScriptContentHolder.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
    return globalScriptContentHolder;
  }

  protected void createComponents() {
    // Create the connection panel
    final JPanel queryContentPanel = new JPanel( new BorderLayout() );
    queryContentPanel.add( BorderLayout.NORTH, createQueryListPanel() );
    queryContentPanel.add( BorderLayout.CENTER, createQueryDetailsPanel() );

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getString( "QueryEditorPanel.DataSource" ), queryContentPanel );
    tabbedPane.addTab( Messages.getString( "QueryEditorPanel.GlobalScripting" ), createGlobalScriptTab() );

    setLayout( new BorderLayout() );
    add( BorderLayout.CENTER, tabbedPane );
    setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
  }

  protected JComponent getExtraQueryButtons() {
    return null;
  }

  protected abstract JComponent getQueryEditor();

  private JPanel createQueryDetailsPanel() {
    final JPanel queryNamePanel = new JPanel( new BorderLayout() );
    queryNamePanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );
    queryNamePanel.add( new JLabel( Messages.getString( "QueryEditorPanel.QueryStringLabel" ) ), BorderLayout.NORTH );
    queryNamePanel.add( queryNameTextField, BorderLayout.SOUTH );

    final JPanel queryControlsPanel = new JPanel( new BorderLayout() );
    queryControlsPanel
        .add( new JLabel( Messages.getString( "QueryEditorPanel.QueryDetailsLabel" ) ), BorderLayout.WEST );
    JComponent extraQueryButtons = getExtraQueryButtons();
    if ( extraQueryButtons != null ) {
      queryControlsPanel.add( extraQueryButtons, BorderLayout.EAST );
    }

    final JPanel queryPanel = new JPanel( new BorderLayout() );
    queryPanel.add( queryControlsPanel, BorderLayout.NORTH );
    queryPanel.add( getQueryEditor(), BorderLayout.CENTER );
    queryPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );

    final JTabbedPane queryScriptTabPane = new JTabbedPane();
    queryScriptTabPane.addTab( Messages.getString( "QueryEditorPanel.StaticQuery" ), queryPanel );
    queryScriptTabPane.addTab( Messages.getString( "QueryEditorPanel.QueryScripting" ), createQueryScriptTab() );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.add( BorderLayout.NORTH, queryNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryScriptTabPane );
    return queryDetailsPanel;
  }

  private JPanel createQueryListPanel() {
    // Create the query list panel
    final JPanel queryButtons = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    queryButtons.add( new BorderlessButton( new QueryAddAction<T>( dialogModel ) ) );
    queryButtons.add( new BorderlessButton( new QueryRemoveAction<T>( dialogModel ) ) );

    final JPanel queryControlPanel = new JPanel( new BorderLayout() );
    queryControlPanel.add( new JLabel( Messages.getString( "QueryEditorPanel.AvailableQueries" ) ), BorderLayout.WEST );
    queryControlPanel.add( queryButtons, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, queryControlPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  protected void updateSelectedQueryFromModel() {
  }
}
