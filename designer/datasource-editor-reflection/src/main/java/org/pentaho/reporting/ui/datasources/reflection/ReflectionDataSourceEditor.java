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

package org.pentaho.reporting.ui.datasources.reflection;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.NamedStaticDataFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Ezequiel Cuellar
 */
public class ReflectionDataSourceEditor extends CommonDialog {
  private class QueryRemoveAction extends AbstractAction implements ListSelectionListener {
    private QueryRemoveAction() {
      final URL resource = ReflectionDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/reflection/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "ReflectionDataSourceEditor.RemoveQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ReflectionDataSourceEditor.RemoveQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        queries.remove( query.getQueryName() );
      }

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.clearSelection();
      inModifyingQueryNameList = false;
      updateComponents();
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( queryNameList.isSelectionEmpty() == false );
    }
  }

  private class QueryNameTextFieldDocumentListener implements DocumentListener {
    public void insertUpdate( final DocumentEvent e ) {
      update();
    }

    public void removeUpdate( final DocumentEvent e ) {
      update();
    }

    public void changedUpdate( final DocumentEvent e ) {
      update();
    }

    private void update() {
      if ( inModifyingQueryNameList ) {
        return;
      }
      final String queryName = queryNameTextField.getText();
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if ( currentQuery == null ) {
        return;
      }

      if ( queryName.equals( currentQuery.getQueryName() ) ) {
        return;
      }
      if ( queries.containsKey( queryName ) ) {
        return;
      }

      inQueryNameUpdate = true;
      queries.remove( currentQuery.getQueryName() );
      currentQuery.setQueryName( queryName );
      queries.put( currentQuery.getQueryName(), currentQuery );
      updateQueryList();
      queryNameList.setSelectedValue( currentQuery, true );
      inQueryNameUpdate = false;
    }
  }

  private static class QueryNameListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      final JLabel listCellRendererComponent =
        (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
      if ( value != null ) {
        final String queryName = ( (DataSetQuery) value ).getQueryName();
        if ( StringUtils.isEmpty( queryName ) == false ) {
          listCellRendererComponent.setText( queryName );
        } else {
          listCellRendererComponent.setText( " " );
        }
      }
      return listCellRendererComponent;
    }
  }

  private class QueryNameListSelectionListener implements ListSelectionListener {
    public void valueChanged( final ListSelectionEvent e ) {
      if ( !inQueryNameUpdate ) {
        final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
        if ( query != null ) {
          queryNameTextField.setText( query.getQueryName() );
          queryTextArea.setText( query.getQuery() );
          updateComponents();
        } else {
          queryNameTextField.setText( "" );
          queryTextArea.setText( "" );
          updateComponents();
        }
      }
    }
  }

  private class QueryAddAction extends AbstractAction {
    private QueryAddAction() {
      final URL resource = ReflectionDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/reflection/resources/Add.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "ReflectionDataSourceEditor.AddQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ReflectionDataSourceEditor.AddQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      // Find a unique query name
      String queryName = Messages.getString( "ReflectionDataSourceEditor.Query" );
      for ( int i = 1; i < 1000; ++i ) {
        final String newQueryName = Messages.getString( "ReflectionDataSourceEditor.Query" ) + " " + i;
        if ( !queries.containsKey( newQueryName ) ) {
          queryName = newQueryName;
          break;
        }
      }

      final DataSetQuery newQuery = new DataSetQuery( queryName, "" );
      queries.put( newQuery.getQueryName(), newQuery );

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.setSelectedValue( newQuery, true );
      inModifyingQueryNameList = false;
      updateComponents();
    }
  }

  private class QueryDocumentListener implements DocumentListener {
    private QueryDocumentListener() {
    }

    public void insertUpdate( final DocumentEvent e ) {
      update();
    }

    public void removeUpdate( final DocumentEvent e ) {
      update();
    }

    public void changedUpdate( final DocumentEvent e ) {
      update();
    }

    private void update() {
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if ( currentQuery == null ) {
        return;
      }

      currentQuery.setQuery( queryTextArea.getText() );
    }
  }

  private class PreviewAction extends AbstractAction {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "ReflectionDataSourceEditor.Preview.Name" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      try {
        final String query = queryTextArea.getText();
        final DataPreviewDialog previewDialog = new DataPreviewDialog( ReflectionDataSourceEditor.this );
        final ReflectionPreviewWorker worker = new ReflectionPreviewWorker( query );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( ReflectionDataSourceEditor.this,
            Messages.getString( "ReflectionDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "ReflectionDataSourceEditor.PreviewError.Message" ), factoryException );
        }

      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( ReflectionDataSourceEditor.this,
          Messages.getString( "ReflectionDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "ReflectionDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private static final ResourceBundleSupport messages =
    new ResourceBundleSupport( Locale.getDefault(), ReflectionDataSourceModule.BUNDLE,
      ObjectUtilities.getClassLoader( ReflectionDataSourceModule.class ) );

  private JList queryNameList;
  private JTextField queryNameTextField;
  private JTextArea queryTextArea;
  private Map<String, DataSetQuery> queries;
  private boolean inQueryNameUpdate;
  private boolean inModifyingQueryNameList;
  private PreviewAction previewAction;

  public ReflectionDataSourceEditor() {
    init();
  }

  public ReflectionDataSourceEditor( final Dialog aOwner ) {
    super( aOwner );
    init();
  }

  public ReflectionDataSourceEditor( final Frame aOwner ) {
    super( aOwner );
    init();
  }

  protected void init() {

    setModal( true );
    setTitle( messages.getString( "ReflectionDataSourceEditor.Title" ) );

    queries = new LinkedHashMap<String, DataSetQuery>();
    previewAction = new PreviewAction();

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( new QueryNameTextFieldDocumentListener() );

    queryTextArea = new JTextArea();
    queryTextArea.setRows( 10 );
    queryTextArea.setColumns( 35 );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    queryNameList = new JList();
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QueryNameListSelectionListener() );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );

    final QueryRemoveAction removeQueryAction = new QueryRemoveAction();
    queryNameList.addListSelectionListener( removeQueryAction );


    super.init();
  }

  protected String getDialogId() {
    return "ReflectionDataSourceEditor";
  }

  protected Component createContentPane() {
    // Create the query list panel

    final JPanel queryDetailsNamePanel = new JPanel( new BorderLayout() );
    queryDetailsNamePanel
      .add( new JLabel( messages.getString( "ReflectionDataSourceEditor.QueryName" ) ), BorderLayout.NORTH );
    queryDetailsNamePanel.add( queryNameTextField, BorderLayout.CENTER );

    final JPanel queryContentHolder = new JPanel( new BorderLayout() );
    queryContentHolder
      .add( BorderLayout.NORTH, new JLabel( messages.getString( "ReflectionDataSourceEditor.QueryLabel" ) ) );
    queryContentHolder.add( BorderLayout.CENTER, new JScrollPane( queryTextArea ) );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.setBorder( new EmptyBorder( 0, 8, 8, 8 ) );
    queryDetailsPanel.add( BorderLayout.NORTH, queryDetailsNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryContentHolder );

    final JPanel previewButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonPanel.add( new JButton( previewAction ) );

    final JPanel previewPanel = new JPanel( new BorderLayout() );
    previewPanel.add( BorderLayout.SOUTH, previewButtonPanel );
    previewPanel.add( BorderLayout.CENTER, queryDetailsPanel );

    final JPanel queryContentPanel = new JPanel( new BorderLayout() );
    queryContentPanel.add( BorderLayout.NORTH, createQueryListPanel() );
    queryContentPanel.add( BorderLayout.CENTER, previewPanel );

    return queryContentPanel;
  }

  private JPanel createQueryListPanel() {
    // Create the query list panel

    final QueryRemoveAction queryRemoveAction = new QueryRemoveAction();
    queryNameList.addListSelectionListener( queryRemoveAction );

    final JPanel theQueryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    theQueryButtonsPanel.add( new BorderlessButton( new QueryAddAction() ) );
    theQueryButtonsPanel.add( new BorderlessButton( queryRemoveAction ) );

    final JPanel theQueryControlsPanel = new JPanel( new BorderLayout() );
    theQueryControlsPanel
      .add( new JLabel( messages.getString( "ReflectionDataSourceEditor.AvailableQueries" ) ), BorderLayout.WEST );
    theQueryControlsPanel.add( theQueryButtonsPanel, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, theQueryControlsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  public NamedStaticDataFactory performConfiguration( final NamedStaticDataFactory dataFactory,
                                                      final String selectedQuery ) {
    // Initialize the internal storage
    queries.clear();

    // Load the current configuration
    if ( dataFactory != null ) {
      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final String query = dataFactory.getQuery( queryName );
        queries.put( queryName, new DataSetQuery( queryName, query ) );
      }
    }

    // Prepare the data and the enable the proper buttons
    updateComponents();
    updateQueryList();
    setSelectedQuery( selectedQuery );

    // Enable the dialog

    if ( !performEdit() ) {
      return null;
    }

    return produceFactory();
  }

  private NamedStaticDataFactory produceFactory() {
    final NamedStaticDataFactory returnDataFactory = new NamedStaticDataFactory();
    final DataSetQuery[] queries = this.queries.values().toArray( new DataSetQuery[ this.queries.size() ] );
    for ( int i = 0; i < queries.length; i++ ) {
      final DataSetQuery query = queries[ i ];
      returnDataFactory.setQuery( query.getQueryName(), query.getQuery() );
    }
    return returnDataFactory;
  }

  protected void updateQueryList() {
    queryNameList.removeAll();
    queryNameList.setListData( queries.values().toArray( new DataSetQuery[ queries.size() ] ) );
  }

  private void setSelectedQuery( final String aQuery ) {
    final ListModel theModel = queryNameList.getModel();
    for ( int i = 0; i < theModel.getSize(); i++ ) {
      final DataSetQuery theDataSet = (DataSetQuery) theModel.getElementAt( i );
      if ( theDataSet.getQueryName().equals( aQuery ) ) {
        queryNameList.setSelectedValue( theDataSet, true );
        break;
      }
    }
  }

  protected void updateComponents() {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;
    final boolean hasQueries = queryNameList.getModel().getSize() > 0;

    queryNameTextField.setEnabled( querySelected );
    queryTextArea.setEnabled( querySelected );

    getConfirmAction().setEnabled( hasQueries );
    previewAction.setEnabled( querySelected );
  }

}
