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

package org.pentaho.reporting.ui.datasources.sequence;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.NamedQueryModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryAddAction;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryNameListCellRenderer;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryNameTextFieldDocumentListener;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryRemoveAction;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QuerySelectedHandler;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceRegistry;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SequenceDataSourceEditor extends CommonDialog {
  private class QueryNameHandler extends QueryNameTextFieldDocumentListener<Sequence> {
    private QueryNameHandler( final NamedQueryModel<Sequence> dialogModel ) {
      super( dialogModel );
    }

    protected void setEditorQuery( final DataSetQuery<Sequence> dataSetQuery ) {
      if ( dataSetQuery == null ) {
        queryNameTextField.setText( null );
        sequenceEditor.setSequence( null );
        return;
      }

      queryNameTextField.setText( dataSetQuery.getQueryName() );
      sequenceEditor.setSequence( dataSetQuery.getQuery() );
    }
  }

  private class PreviewAction extends AbstractAction {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "SequenceDataSourceEditor.Preview.Name" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      try {
        final Sequence query = sequenceEditor.getSequence();
        if ( query == null ) {
          return;
        }
        final DataPreviewDialog previewDialog = new DataPreviewDialog( SequenceDataSourceEditor.this );
        final SequencePreviewWorker worker = new SequencePreviewWorker( query, designTimeContext );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( SequenceDataSourceEditor.this,
            Messages.getString( "SequenceDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "SequenceDataSourceEditor.PreviewError.Message" ), factoryException );
        }

      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( SequenceDataSourceEditor.this,
          Messages.getString( "SequenceDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "SequenceDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private class SequenceChangeHandler implements PropertyChangeListener {
    private SequenceChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final DataSetQuery<Sequence> selectedQuery = queries.getQueries().getSelectedQuery();
      if ( selectedQuery != null ) {
        selectedQuery.setQuery( sequenceEditor.getSequence() );
      }
    }
  }

  private class SequenceQueryModel extends NamedQueryModel<Sequence> {
    private SequenceQueryModel() {
    }

    protected Sequence createDefaultObject() {
      final SequenceDescription defaultSequence = getDefaultSequence();
      if ( defaultSequence == null ) {
        return null;
      }
      return defaultSequence.newInstance();
    }

    protected void setQuerySelected( final boolean querySelected ) {
      super.setQuerySelected( querySelected );
      queryNameTextField.setEnabled( querySelected );
      sequenceEditor.setEnabled( querySelected );
    }

    public void setSelectedDataSetQuery( final DataSetQuery<Sequence> sequenceDataSetQuery ) {
      sequenceEditor.stopEditing();
      super.setSelectedDataSetQuery( sequenceDataSetQuery );
    }
  }

  private JTextField queryNameTextField;
  private JList queryNameList;
  private SequenceEditor sequenceEditor;
  private NamedQueryModel<Sequence> queries;
  private DesignTimeContext designTimeContext;
  private SequenceRegistry registry;
  private PreviewAction previewAction;
  /**
   * @noinspection FieldCanBeLocal, UnusedDeclaration
   */
  private QuerySelectedHandler querySelectedHandler;

  public SequenceDataSourceEditor() {
    init();
  }

  public SequenceDataSourceEditor( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public SequenceDataSourceEditor( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  private SequenceDescription getDefaultSequence() {
    final SequenceDescription[] sequences = registry.getSequences();
    if ( sequences.length == 0 ) {
      return null;
    }
    return sequences[ 0 ];
  }

  protected void init() {
    registry = new SequenceRegistry();
    queries = new SequenceQueryModel();
    previewAction = new PreviewAction();

    final QueryNameHandler queryNameHandler = new QueryNameHandler( queries );

    queryNameList = new JList( queries.getQueries() );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );

    querySelectedHandler = new QuerySelectedHandler<Sequence>( queries, queryNameList );

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( queryNameHandler );
    queryNameTextField.setEnabled( false );

    sequenceEditor = new SequenceEditor();
    sequenceEditor.addPropertyChangeListener( "sequence", new SequenceChangeHandler() );

    setTitle( Messages.getString( "SequenceDataSourceEditor.Title" ) );

    super.init();
  }

  protected String getDialogId() {
    return "SequenceDataSourceEditor";
  }

  protected Component createContentPane() {

    final JPanel namePanel = new JPanel( new BorderLayout() );
    namePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 0 ) );
    namePanel.add( BorderLayout.NORTH, new JLabel( Messages.getString( "SequenceDataSourceEditor.QueryName" ) ) );
    namePanel.add( BorderLayout.CENTER, queryNameTextField );

    final JPanel queryConfigPane = new JPanel( new BorderLayout() );
    queryConfigPane.add( createQuerySelectionPanel(), BorderLayout.NORTH );
    queryConfigPane.add( namePanel, BorderLayout.CENTER );

    final JPanel previewButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonPanel.add( new JButton( previewAction ) );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( queryConfigPane, BorderLayout.NORTH );
    contentPane.add( sequenceEditor, BorderLayout.CENTER );
    contentPane.add( previewButtonPanel, BorderLayout.SOUTH );
    return contentPane;
  }

  private JPanel createQuerySelectionPanel() {
    final QueryRemoveAction removeQueryAction = new QueryRemoveAction( queries );

    final JPanel queryListButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    queryListButtonsPanel.add( new BorderlessButton( new QueryAddAction( queries ) ) );
    queryListButtonsPanel.add( new BorderlessButton( removeQueryAction ) );


    final JPanel queryListDetailsPanel = new JPanel( new BorderLayout() );
    queryListDetailsPanel
      .add( new JLabel( Messages.getString( "SequenceDataSourceEditor.QueryDetailsLabel" ) ), BorderLayout.WEST );
    queryListDetailsPanel.add( queryListButtonsPanel, BorderLayout.EAST );

    // Create the query list panel
    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) );
    queryListPanel.add( BorderLayout.NORTH, queryListDetailsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  public SequenceDataFactory performConfiguration( final DesignTimeContext context,
                                                   final SequenceDataFactory dataFactory,
                                                   final String selectedQuery ) {

    this.designTimeContext = context;
    if ( this.designTimeContext != null ) {
      sequenceEditor.applyLocaleSettings( context.getLocaleSettings() );
    }

    if ( dataFactory != null ) {
      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final Sequence query = dataFactory.getSequence( queryName );
        queries.addQuery( queryName, query );
      }
    }

    queries.setSelectedQuery( selectedQuery );
    if ( performEdit() == false ) {
      return null;
    }

    sequenceEditor.stopEditing();

    final SequenceDataFactory retval = new SequenceDataFactory();
    for ( final DataSetQuery<Sequence> query : this.queries.getQueries() ) {
      retval.addSequence( query.getQueryName(), query.getQuery() );
    }
    return retval;
  }

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    SequenceDataSourceEditor ed = new SequenceDataSourceEditor();
    ed.performConfiguration( new DefaultDesignTimeContext( new MasterReport() ), null, null );
  }
}
