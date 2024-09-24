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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerDocumentContext;
import org.pentaho.reporting.designer.core.actions.global.DeleteAction;
import org.pentaho.reporting.designer.core.actions.report.AddDataFactoryAction;
import org.pentaho.reporting.designer.core.actions.report.EditQueryAction;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChange;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProvisionDataSourcePanel extends JPanel {
  private class RemoveDataSourceAction extends AbstractAction implements TreeSelectionListener {
    public RemoveDataSourceAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ParameterDialog.DeleteDataSourceAction" ) );
      setEnabled( false );
    }

    public void valueChanged( final TreeSelectionEvent e ) {
      setEnabled( isDataSourceSelected() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final int result = JOptionPane.showConfirmDialog( ProvisionDataSourcePanel.this,
        Messages.getString( "ParameterDialog.DeleteDataSourceWarningMessage" ),
        Messages.getString( "ParameterDialog.DeleteDataSourceWarningTitle" ), JOptionPane.YES_NO_OPTION );
      if ( result == JOptionPane.YES_OPTION ) {
        final DataFactory theSelectedDataFactory = getSelectedDataSource();

        // Delete data-source from structure panel
        reportDesignerContext.getActiveContext().getSelectionModel()
          .setSelectedElements( new Object[] { theSelectedDataFactory } );

        final DeleteAction deleteAction = new DeleteAction();
        deleteAction.setReportDesignerContext( reportDesignerContext );
        deleteAction.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );

        // Delete data-source from the Select data-source dialog
        availableDataSourcesModel.remove( theSelectedDataFactory );
      }
    }
  }


  private class EditDataSourceAction extends AbstractAction implements TreeSelectionListener {
    private EditDataSourceAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getEditIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ParameterDialog.EditDataSourceAction" ) );
      setEnabled( false );
    }

    public void valueChanged( final TreeSelectionEvent e ) {
      if ( getSelectedDataSource() == null ) {
        setEnabled( false );
        return;
      }
      setEnabled( true );
    }

    public void actionPerformed( final ActionEvent e ) {
      final DataFactory dataFactory = getSelectedDataSource();
      if ( dataFactory == null ) {
        return;
      }
      final DataFactoryMetaData metadata = dataFactory.getMetaData();
      if ( metadata.isEditable() == false ) {
        return;
      }

      // Edit data-source from structure panel
      reportDesignerContext.getActiveContext().getSelectionModel().setSelectedElements( new Object[] { dataFactory } );

      final EditQueryAction editQueryAction = new EditQueryAction();
      editQueryAction.setReportDesignerContext( reportDesignerContext );
      editQueryAction.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );

      final int idx = availableDataSourcesModel.indexOf( dataFactory );
      if ( idx == -1 ) {
        throw new IllegalStateException( "DataSource Model is out of sync with the GUI" );
      }
      if ( editQueryAction.getEditedDataFactory() != null ) {
        availableDataSourcesModel.edit( idx, editQueryAction.getEditedDataFactory() );
      }
    }
  }

  private final class ShowAddDataSourcePopupAction extends AbstractAction {
    public ShowAddDataSourcePopupAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final JPopupMenu menu = new JPopupMenu();
      createDataSourceMenu( menu );

      final Object source = e.getSource();
      if ( source instanceof Component ) {
        final Component c = (Component) source;
        menu.show( c, 0, c.getHeight() );
      } else {
        menu.show( ProvisionDataSourcePanel.this, 0, 0 );
      }
    }
  }

  public boolean isDataSourceSelected() {
    final TreePath selectionPath = availableDataSources.getSelectionPath();
    if ( selectionPath == null ) {
      return false;
    }
    return selectionPath.getLastPathComponent() instanceof DataFactoryWrapper;
  }

  public DataFactory getSelectedDataSource() {
    final TreePath selectionPath = availableDataSources.getSelectionPath();
    if ( selectionPath == null ) {
      return null;
    }

    final int size = selectionPath.getPathCount();
    if ( size >= 2 ) {
      final DataFactoryWrapper dataFactoryWrapper =
        (DataFactoryWrapper) selectionPath.getPathComponent( 1 );
      return dataFactoryWrapper.getEditedDataFactory();
    }
    return null;
  }

  private void createDataSourceMenu( final JComponent insertDataSourcesMenu ) {
    final DataFactoryMetaData[] datas = DataFactoryRegistry.getInstance().getAll();
    final Map<String, Boolean> groupMap = new HashMap<String, Boolean>();
    for ( int i = 0; i < datas.length; i++ ) {
      final DataFactoryMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( data.isEditorAvailable() == false ) {
        continue;
      }
      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      groupMap.put( currentGrouping, groupMap.containsKey( currentGrouping ) );
    }

    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    JMenu subMenu = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final DataFactoryMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( data.isEditorAvailable() == false ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      final Boolean isMultiGrouping = groupMap.get( currentGrouping );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          if ( isMultiGrouping ) {
            subMenu = new JMenu( currentGrouping );
            insertDataSourcesMenu.add( subMenu );
          }
        }
      } else {
        firstElement = false;
        grouping = currentGrouping;
        if ( isMultiGrouping ) {
          subMenu = new JMenu( currentGrouping );
          insertDataSourcesMenu.add( subMenu );
        }
      }
      final AddDataSourceAction action = new AddDataSourceAction( data );
      if ( isMultiGrouping ) {
        //noinspection ConstantConditions
        subMenu.add( new JMenuItem( action ) );
      } else {
        insertDataSourcesMenu.add( new JMenuItem( action ) );
      }
    }
  }

  private class AddDataSourceAction extends AbstractAction {
    private DataFactoryMetaData dataSourcePlugin;

    private AddDataSourceAction( final DataFactoryMetaData dataSourcePlugin ) {
      this.dataSourcePlugin = dataSourcePlugin;
      putValue( Action.NAME, dataSourcePlugin.getDisplayName( Locale.getDefault() ) );
      putValue( Action.SHORT_DESCRIPTION, dataSourcePlugin.getDescription( Locale.getDefault() ) );
      final Image image = dataSourcePlugin.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
      if ( image != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( image ) );
      }
    }


    public void actionPerformed( final ActionEvent e ) {
      final DataSourcePlugin editor = dataSourcePlugin.createEditor();
      if ( editor == null ) {
        return;
      }

      final DataFactory dataFactory = editor.performEdit( new DataSourceDesignTimeContext(), null, null, null );

      if ( dataFactory == null ) {
        return;
      }

      // Add data-source factory into Structure panel.
      // This happens after user adds a new data source from the Edit DataSource dialog
      AddDataFactoryAction
        .addDataFactory( reportDesignerContext.getActiveContext(), dataFactory, new DataFactoryChange[ 0 ] );
      availableDataSourcesModel.add( new DataFactoryWrapper( null, dataFactory ) );

      expandAllNodes();
      SwingUtilities
        .invokeLater( new DataTabSetVisible( reportDesignerContext, reportDesignerContext.getActiveContext() ) );
    }
  }


  private static class DataTabSetVisible implements Runnable {
    private ReportDesignerContext designerContext;
    private ReportDesignerDocumentContext<?> activeContext;

    public DataTabSetVisible( final ReportDesignerContext designerContext,
                              final ReportDesignerDocumentContext<?> activeContext ) {
      this.designerContext = designerContext;
      this.activeContext = activeContext;
    }

    public void run() {
      if ( this.activeContext == null ) {
        return;
      }

      designerContext.setActiveDocument( activeContext );
      designerContext.getView().showDataTree();
    }
  }

  private class DataSourceDesignTimeContext implements DesignTimeContext {
    public DataSourceDesignTimeContext() {
    }

    /**
     * The currently active report (or subreport).
     *
     * @return the active report.
     */
    public AbstractReportDefinition getReport() {
      return reportDesignerContext.getActiveContext().getContextRoot();
    }

    /**
     * The parent window in the GUI for showing modal dialogs.
     *
     * @return the window or null, if there is no parent.
     */
    public Window getParentWindow() {
      return LibSwingUtil.getWindowAncestor( ProvisionDataSourcePanel.this );
    }

    public DataSchemaModel getDataSchemaModel() {
      // todo: Filter so that only env- and parameter are visible here.
      return reportDesignerContext.getActiveContext().getReportDataSchemaModel();
    }

    public void error( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }

    public void userError( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }

    public LocaleSettings getLocaleSettings() {
      return WorkspaceSettings.getInstance();
    }

    public boolean isShowExpertItems() {
      return WorkspaceSettings.getInstance().isShowExpertItems();
    }

    public boolean isShowDeprecatedItems() {
      return WorkspaceSettings.getInstance().isShowDeprecatedItems();
    }

    public DataFactoryContext getDataFactoryContext() {
      return new DesignTimeDataFactoryContext( reportDesignerContext.getActiveContext().getContextRoot() );
    }

    public MaturityLevel getMaturityLevel() {
      return WorkspaceSettings.getInstance().getMaturityLevel();
    }
  }


  private JTree availableDataSources;
  private DataFactoryTreeModel availableDataSourcesModel;
  private ReportDesignerContext reportDesignerContext;

  public ProvisionDataSourcePanel() {
    availableDataSourcesModel = new DataFactoryTreeModel();

    availableDataSources = new JTree( availableDataSourcesModel );
    availableDataSources.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
    availableDataSources.setCellRenderer( new DataFactoryTreeCellRenderer() );
    availableDataSources.setRootVisible( false );

    init();
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }

  public void importDataSourcesFromMaster( final CompoundDataFactory cdf ) {
    availableDataSourcesModel.importFromReport( cdf );
  }

  public DataFactoryTreeModel getDataFactoryTreeModel() {
    return availableDataSourcesModel;
  }

  public JTree getDataSourcesTree() {
    return availableDataSources;
  }

  public String getSelectedQueryName() {
    final Object node = availableDataSources.getLastSelectedPathComponent();
    if ( node != null ) {
      return node.toString();
    }

    return "";
  }

  public void expandAllNodes() {
    for ( int i = 0; i < availableDataSources.getRowCount(); i++ ) {
      availableDataSources.expandRow( i );
    }
  }

  protected void init() {
    final RemoveDataSourceAction removeAction = new RemoveDataSourceAction();
    final EditDataSourceAction editDataSourceAction = new EditDataSourceAction();
    final ShowAddDataSourcePopupAction showAddDataSourcePopupAction = new ShowAddDataSourcePopupAction();

    availableDataSources.addTreeSelectionListener( editDataSourceAction );
    availableDataSources.addTreeSelectionListener( removeAction );

    final JScrollPane theScrollPanel = new JScrollPane( availableDataSources );
    theScrollPanel.setAutoscrolls( true );

    final JPanel theDataSetsButtonPanel = new JPanel();
    theDataSetsButtonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    theDataSetsButtonPanel.add( new BorderlessButton( showAddDataSourcePopupAction ) );
    theDataSetsButtonPanel.add( new BorderlessButton( editDataSourceAction ) );
    theDataSetsButtonPanel.add( new BorderlessButton( removeAction ) );

    final JPanel theControlsPanel = new JPanel( new BorderLayout() );
    theControlsPanel.add( new JLabel( Messages.getString( "ParameterDialog.DataSources" ) ), BorderLayout.WEST );
    theControlsPanel.add( theDataSetsButtonPanel, BorderLayout.EAST );

    final JPanel dataSetsPanel = new JPanel( new BorderLayout() );
    dataSetsPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
    dataSetsPanel.add( theScrollPanel, BorderLayout.CENTER );
    dataSetsPanel.add( theControlsPanel, BorderLayout.NORTH );

    setLayout( new BorderLayout() );
    add( dataSetsPanel, BorderLayout.CENTER );
  }
}
