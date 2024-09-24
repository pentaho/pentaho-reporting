package org.pentaho.reporting.designer.extensions.connectioneditor;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.EditDataSourceMgmtService;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.ui.xul.XulException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;

public class ConnectionEditorPanel extends JPanel {
  private static class DataSourceDefinitionListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      final JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent( list, value, index,
        isSelected,
        cellHasFocus );
      if ( value != null ) {
        final String jndiName = ( (IDatabaseConnection) value ).getName();
        if ( !"".equals( jndiName ) ) {
          listCellRendererComponent.setText( jndiName );
        } else {
          listCellRendererComponent.setText( " " );
        }
      }
      return listCellRendererComponent;
    }
  }

  private class EditDataSourceAction extends AbstractAction implements ListSelectionListener {
    private JList dataSourceList;

    private EditDataSourceAction( final JList dataSourceList ) {
      this.dataSourceList = dataSourceList;
      final URL location =
        ConnectionEditorPanel.class.getResource( "/org/pentaho/reporting/ui/datasources/jdbc/resources/Edit.png" );
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getInstance().getString( "ConnectionEditorPanel.Edit.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "ConnectionEditorPanel.Edit.Description" ) );
      setEnabled( isConnectionSelected() );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( isConnectionSelected() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final IDatabaseConnection existingConnection = (IDatabaseConnection) dataSourceList.getSelectedValue();

      try {
        final Window parentWindow = LibSwingUtil.getWindowAncestor( ConnectionEditorPanel.this );
        final XulDatabaseDialog connectionDialog = new XulDatabaseDialog( parentWindow );
        final IDatabaseConnection connectionDefinition = connectionDialog.open( existingConnection );

        // See if the edit completed...
        if ( connectionDefinition != null ) {
          dialogModel.updateDatasourceById( existingConnection.getId(), connectionDefinition );
          dataSourceModel.updateElementAt( connectionDefinition, dataSourceList.getSelectedIndex() );
          dataSourceList.setSelectedValue( connectionDefinition, true );
        }
      } catch ( XulException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  private class RemoveDataSourceAction extends AbstractAction implements ListSelectionListener {
    private JList dataSourceList;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     *
     * @param dataSourceList the list containing the datasources
     */
    private RemoveDataSourceAction( final JList dataSourceList ) {
      this.dataSourceList = dataSourceList;
      setEnabled( isConnectionSelected() );
      final URL resource =
        ConnectionEditorPanel.class.getResource( "/org/pentaho/reporting/ui/datasources/jdbc/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getInstance().getString( "ConnectionEditorPanel.Remove.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "ConnectionEditorPanel.Remove.Description" ) );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( isConnectionSelected() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final IDatabaseConnection source = (IDatabaseConnection) dataSourceList.getSelectedValue();
      if ( source != null ) {
        dialogModel.deleteDatasourceById( source.getId() );
        dataSourceModel.removeElement( source );
      }
    }
  }

  private class AddDataSourceAction extends AbstractAction {
    private JList dataSourceList;

    private AddDataSourceAction( final JList dataSourceList ) {
      this.dataSourceList = dataSourceList;
      final URL location = ConnectionEditorPanel.class.getResource(
        "/org/pentaho/reporting/ui/datasources/jdbc/resources/Add.png" );
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getInstance().getString( "ConnectionEditorPanel.Add.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "ConnectionEditorPanel.Add.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      try {
        final Window parentWindow = LibSwingUtil.getWindowAncestor( ConnectionEditorPanel.this );
        final XulDatabaseDialog connectionDialog = new XulDatabaseDialog( parentWindow );
        final IDatabaseConnection connectionDefinition = connectionDialog.open( null );

        if ( connectionDefinition != null &&
          !StringUtils.isEmpty( connectionDefinition.getName() ) ) {
          // A new JNDI source was created
          dialogModel.createDatasource( connectionDefinition );
          dataSourceModel.addElement( connectionDefinition );
          dataSourceList.setSelectedValue( connectionDefinition, true );
        }
      } catch ( XulException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  private EditDataSourceMgmtService dialogModel;
  private JList dataSourceList;
  private EditorComboBoxModel dataSourceModel;

  public ConnectionEditorPanel() {
    this.dialogModel = new EditDataSourceMgmtService();
    initPanel();
  }

  protected void initPanel() {
    setLayout( new BorderLayout() );
    setBorder( BorderFactory.createEmptyBorder( 2, 5, 5, 5 ) );

    dataSourceModel = new EditorComboBoxModel();
    final List<IDatabaseConnection> datasources = dialogModel.getDatasources();
    for ( int i = 0; i < datasources.size(); i++ ) {
      dataSourceModel.addElement( datasources.get( i ) );
    }

    dataSourceList = new JList( dataSourceModel );
    dataSourceList.setCellRenderer( new DataSourceDefinitionListCellRenderer() );
    dataSourceList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    dataSourceList.setVisibleRowCount( 10 );

    final EditDataSourceAction editDataSourceAction = new EditDataSourceAction( dataSourceList );
    dataSourceList.addListSelectionListener( editDataSourceAction );

    final RemoveDataSourceAction removeDataSourceAction = new RemoveDataSourceAction( dataSourceList );
    dataSourceList.addListSelectionListener( removeDataSourceAction );

    final JPanel connectionButtonPanel = new JPanel();
    connectionButtonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    connectionButtonPanel.add( new BorderlessButton( editDataSourceAction ) );
    connectionButtonPanel.add( new BorderlessButton( new AddDataSourceAction( dataSourceList ) ) );
    connectionButtonPanel.add( new BorderlessButton( removeDataSourceAction ) );

    final JPanel connectionButtonPanelWrapper = new JPanel( new BorderLayout() );
    connectionButtonPanelWrapper
      .add( new JLabel( Messages.getInstance().getString( "ConnectionEditorPanel.Connections" ) ),
        BorderLayout.CENTER );
    connectionButtonPanelWrapper.add( connectionButtonPanel, BorderLayout.EAST );

    add( BorderLayout.NORTH, connectionButtonPanelWrapper );
    add( BorderLayout.CENTER, new JScrollPane( dataSourceList ) );
  }

  protected boolean isConnectionSelected() {
    return dataSourceList.getSelectedIndex() != -1;
  }

  public void commit() {
    dialogModel.commit();
  }
}
