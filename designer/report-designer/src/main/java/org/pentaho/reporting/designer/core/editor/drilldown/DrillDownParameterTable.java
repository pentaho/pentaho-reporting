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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.FormulaFragmentCellRenderer;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellEditor;
import org.pentaho.reporting.designer.core.util.table.GroupedTableModel;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

public class DrillDownParameterTable extends JComponent {
  private class AddParameterAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddParameterAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "DrillDownParameterTable.AddParameter" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DrillDownParameter[] data = parameterTableModel.getData();
      final DrillDownParameter[] data2 = new DrillDownParameter[ data.length + 1 ];
      System.arraycopy( data, 0, data2, 0, data.length );

      data2[ data.length ] =
        new DrillDownParameter( Messages.getString( "DrillDownParameterTable.Parameter.DefaultName" ) );
      data2[ data.length ].setPosition( data.length );
      parameterTableModel.setData( data2 );
    }
  }

  private class RemoveParameterAction extends AbstractAction implements ListSelectionListener {
    private GroupedTableModel model;
    private JTable table;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     *
     * @param table the table.
     */
    private RemoveParameterAction( final JTable table ) {
      this.model = (GroupedTableModel) table.getModel();
      this.table = table;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "DrillDownParameterTable.RemoveParameter" ) );
      setEnabled( false );

      table.getSelectionModel().addListSelectionListener( this );

    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      final int[] selectedRows = table.getSelectedRows();
      for ( int i = 0; i < selectedRows.length; i++ ) {
        final int row = selectedRows[ i ];
        final DrillDownParameter.Type type = parameterTableModel.getParameterType( model.mapToModel( row ) );
        if ( DrillDownParameter.Type.MANUAL == type ) {
          setEnabled( true );
          return;
        }
      }
      setEnabled( false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( isEnabled() == false ) {
        return;
      }

      final DrillDownParameter[] data = parameterTableModel.getGroupedData();
      final ListSelectionModel listSelectionModel = table.getSelectionModel();
      final ArrayList<DrillDownParameter> result = new ArrayList<DrillDownParameter>( data.length );
      for ( int i = 0; i < data.length; i++ ) {
        final DrillDownParameter parameter = data[ i ];
        if ( parameter == null ) {
          continue;
        }
        if ( listSelectionModel.isSelectedIndex( model.mapFromModel( i ) ) == false
            || parameter.getType() != DrillDownParameter.Type.MANUAL ) {
          result.add( data[ i ] );
        }
      }

      parameterTableModel.setData( result.toArray( new DrillDownParameter[ result.size() ] ) );
    }
  }

  private class RefreshParameterAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RefreshParameterAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRefreshIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "DrillDownParameterTable.RefreshParameter" ) );
      setEnabled( false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      // rescue all manual parameter ...
      fireRefreshEvent();
    }
  }

  private class DrillDownParameterChangeHandler implements TableModelListener {
    private DrillDownParameter[] oldData;

    private DrillDownParameterChangeHandler() {
      oldData = parameterTableModel.getData();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed.
     */
    public void tableChanged( final TableModelEvent e ) {
      final DrillDownParameter[] newParams = getDrillDownParameter();
      firePropertyChange( DRILL_DOWN_PARAMETER_PROPERTY, oldData, newParams );
      this.oldData = newParams;
    }
  }

  private class HideParamUiSelectionListener implements ItemListener {
    private boolean hideParameterUiValue;

    private HideParamUiSelectionListener() {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged( final ItemEvent e ) {
      final boolean oldValue = this.hideParameterUiValue;
      this.hideParameterUiValue = hideParameterUiCheckbox.isSelected();
      firePropertyChange( HIDE_PARAMETER_UI_PARAMETER_PROPERTY, oldValue, hideParameterUiValue );
    }
  }


  private class ShowAdvancedEditorAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ShowAdvancedEditorAction() {
      putValue( Action.NAME, Messages.getString( "DrillDownParameterTable.Advanced" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Window window = LibSwingUtil.getWindowAncestor( DrillDownParameterTable.this );
      final SystemParameterDialog dialog;
      if ( window instanceof Dialog ) {
        dialog = new SystemParameterDialog( (Dialog) window, parameterTableModel, reportDesignerContext );
      } else if ( window instanceof Frame ) {
        dialog = new SystemParameterDialog( (Frame) window, parameterTableModel, reportDesignerContext );
      } else {
        dialog = new SystemParameterDialog( parameterTableModel, reportDesignerContext );
      }
      dialog.showAdvancedEditor();
      dialog.dispose();
    }
  }

  public static final String DRILL_DOWN_PARAMETER_PROPERTY = "drillDownParameter";
  public static final String HIDE_PARAMETER_UI_PARAMETER_PROPERTY = "hideParameterUi";

  private DrillDownParameterTableModel parameterTableModel;

  private boolean allowCustomParameter;
  private boolean showRefreshButton;
  private JLabel title;
  private ArrayList<DrillDownParameterRefreshListener> listeners;
  private DrillDownParameterTable.RefreshParameterAction refreshParameterAction;

  private boolean singleTabMode;
  private JCheckBox hideParameterUiCheckbox;

  private ElementMetaDataTable allInOneTable;
  private ElementMetaDataTable systemParameterTable;
  private ElementMetaDataTable manualParameterTable;
  private ElementMetaDataTable predefinedParameterTable;
  private RemoveParameterAction allInOneRemoveAction;
  private RemoveParameterAction manualParameterRemoveAction;
  private ReportDesignerContext reportDesignerContext;
  private AddParameterAction addParameterAction;
  private DrillDownParameterTable.ShowAdvancedEditorAction advancedEditorAction;

  public DrillDownParameterTable() {
    setLayout( new BorderLayout() );

    listeners = new ArrayList<DrillDownParameterRefreshListener>();
    refreshParameterAction = new RefreshParameterAction();
    addParameterAction = new AddParameterAction();
    advancedEditorAction = new ShowAdvancedEditorAction();

    parameterTableModel = new DrillDownParameterTableModel();
    parameterTableModel.addTableModelListener( new DrillDownParameterChangeHandler() );

    allInOneTable = new ElementMetaDataTable();
    allInOneTable.setFormulaFragment( true );
    allInOneTable.setDefaultEditor( GroupedName.class, new GroupedNameCellEditor() );
    allInOneTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    allInOneTable.setModel( new GroupedMetaTableModel( parameterTableModel ) );

    allInOneRemoveAction = new RemoveParameterAction( allInOneTable );

    systemParameterTable = new ElementMetaDataTable();
    systemParameterTable.setFormulaFragment( true );
    systemParameterTable.setDefaultEditor( GroupedName.class, new GroupedNameCellEditor() );
    systemParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    systemParameterTable.setModel(
        new FilteringParameterTableModel( DrillDownParameter.Type.SYSTEM, parameterTableModel, true ) );

    manualParameterTable = new ElementMetaDataTable();
    manualParameterTable.setFormulaFragment( true );
    manualParameterTable.setDefaultEditor( GroupedName.class, new GroupedNameCellEditor() );
    manualParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    manualParameterTable.setModel(
        new FilteringParameterTableModel( DrillDownParameter.Type.MANUAL, parameterTableModel ) );
    manualParameterRemoveAction = new RemoveParameterAction( manualParameterTable );

    predefinedParameterTable = new ElementMetaDataTable();
    predefinedParameterTable.setFormulaFragment( true );
    predefinedParameterTable.setDefaultEditor( GroupedName.class, new GroupedNameCellEditor() );
    predefinedParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    predefinedParameterTable.setModel(
        new FilteringParameterTableModel( DrillDownParameter.Type.PREDEFINED, parameterTableModel ) );

    hideParameterUiCheckbox = new JCheckBox( Messages.getString( "DrillDownParameterTable.HideParameterUI" ) );
    hideParameterUiCheckbox.addItemListener( new HideParamUiSelectionListener() );

    title = new JLabel( Messages.getString( "DrillDownParameterTable.Title" ) );

    rebuildUi();
  }

  private void rebuildUi() {
    removeAll();
    if ( isSingleTabMode() ) {
      final JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
      if ( isShowRefreshButton() ) {
        buttonPanel.add( new BorderlessButton( refreshParameterAction ) );
        if ( isAllowCustomParameter() ) {
          buttonPanel.add( Box.createHorizontalStrut( 10 ) );
        }
      }

      if ( isAllowCustomParameter() ) {
        buttonPanel.add( new BorderlessButton( addParameterAction ) );
        buttonPanel.add( new BorderlessButton( allInOneRemoveAction ) );
      }

      final JPanel tablePanel = new JPanel( new BorderLayout() );
      tablePanel.add( new JScrollPane( allInOneTable ), BorderLayout.CENTER );
      if ( isShowHideParameterUiCheckbox() ) {
        tablePanel.add( hideParameterUiCheckbox, BorderLayout.NORTH );
      }

      final JPanel headerPanel = new JPanel();
      headerPanel.setLayout( new BorderLayout() );
      headerPanel.add( title, BorderLayout.WEST );
      headerPanel.add( buttonPanel, BorderLayout.EAST );

      final JPanel centralPanel = new JPanel();
      centralPanel.setLayout( new BorderLayout() );
      //centralPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) ); // Better layout --Kaa
      centralPanel.add( headerPanel, BorderLayout.NORTH );
      centralPanel.add( tablePanel, BorderLayout.CENTER );
      add( centralPanel, BorderLayout.CENTER );

    } else {
      final JPanel systemTablePanel = new JPanel( new BorderLayout() );
      systemTablePanel.add( new JScrollPane( systemParameterTable ), BorderLayout.CENTER );
      systemTablePanel.add( createButtonPanel( null, isShowHideParameterUiCheckbox() ), BorderLayout.NORTH );
      systemTablePanel.add( createAdvancedParameterPanel(), BorderLayout.SOUTH );

      final JPanel predefinedTablePanel = new JPanel( new BorderLayout() );
      predefinedTablePanel.add( new JScrollPane( predefinedParameterTable ), BorderLayout.CENTER );
      predefinedTablePanel.add( createButtonPanel( null, false ), BorderLayout.NORTH );

      final JPanel manualTablePanel = new JPanel( new BorderLayout() );
      manualTablePanel.add( new JScrollPane( manualParameterTable ), BorderLayout.CENTER );
      manualTablePanel.add( createButtonPanel( manualParameterRemoveAction, false ), BorderLayout.NORTH );

      final JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.addTab( Messages.getString( "DrillDownParameterTable.Tab.Report" ), predefinedTablePanel );
      tabbedPane.addTab( Messages.getString( "DrillDownParameterTable.Tab.System" ), systemTablePanel );
      tabbedPane.addTab( Messages.getString( "DrillDownParameterTable.Tab.Manual" ), manualTablePanel );

      add( tabbedPane, BorderLayout.CENTER );
    }
  }


  private JPanel createAdvancedParameterPanel() {
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonPanel.add( new JButton( advancedEditorAction ) );
    return buttonPanel;
  }

  private JPanel createButtonPanel( final Action removeAction,
                                    final boolean addHideParamUiCheckbox ) {
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    if ( isShowRefreshButton() ) {
      buttonPanel.add( new BorderlessButton( refreshParameterAction ) );
      if ( removeAction != null ) {
        buttonPanel.add( Box.createHorizontalStrut( 10 ) );
      }
    }

    if ( removeAction != null ) {
      buttonPanel.add( new BorderlessButton( addParameterAction ) );
      buttonPanel.add( new BorderlessButton( removeAction ) );
    }
    if ( addHideParamUiCheckbox == false ) {
      return buttonPanel;
    }

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( buttonPanel, BorderLayout.CENTER );
    panel.add( hideParameterUiCheckbox, BorderLayout.NORTH );
    return panel;
  }

  public void addDrillDownParameterRefreshListener( final DrillDownParameterRefreshListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    listeners.add( listener );
    refreshParameterAction.setEnabled( isEnabled() && listeners.isEmpty() == false );
  }

  public void removeDrillDownParameterRefreshListener( final DrillDownParameterRefreshListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    listeners.remove( listener );
    refreshParameterAction.setEnabled( isEnabled() && listeners.isEmpty() == false );
  }

  public String getTitle() {
    return title.getText();
  }

  public void setTitle( final String title ) {
    this.title.setText( title );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    reportDesignerContext = context;
    allInOneTable.setReportDesignerContext( context );
    systemParameterTable.setReportDesignerContext( context );
    manualParameterTable.setReportDesignerContext( context );
    predefinedParameterTable.setReportDesignerContext( context );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return allInOneTable.getReportDesignerContext();
  }


  public void setDrillDownParameter( final DrillDownParameter[] parameter ) {
    final DrillDownParameter[] oldParameter = parameterTableModel.getData();
    parameterTableModel.setData( parameter );
    if ( Arrays.equals( oldParameter, parameter ) == false ) {
      firePropertyChange( DrillDownModel.DRILL_DOWN_PARAMETER_PROPERTY, oldParameter, parameter );
    }
  }

  public String[] getExtraFields() {
    return parameterTableModel.getExtraFields();
  }

  public void setExtraFields( final String[] extraFields ) {
    parameterTableModel.setExtraFields( extraFields );
  }

  public DrillDownParameter[] getDrillDownParameter() {
    return parameterTableModel.getData();
  }

  public String[] getFilteredParameterNames() {
    return parameterTableModel.getFilteredParameterNames();
  }

  public void setFilteredParameterNames( final String[] names ) {
    parameterTableModel.setFilteredParameterNames( names );
  }

  public boolean isAllowCustomParameter() {
    return allowCustomParameter;
  }

  public void setAllowCustomParameter( final boolean allowCustomParameter ) {
    this.allowCustomParameter = allowCustomParameter;
    rebuildUi();
  }

  public boolean isShowRefreshButton() {
    return showRefreshButton;
  }

  public void setShowRefreshButton( final boolean showRefreshButton ) {
    this.showRefreshButton = showRefreshButton;
    rebuildUi();
  }

  public void refreshParameterData() {
    fireRefreshEvent();
  }

  protected void fireRefreshEvent() {
    if ( listeners.isEmpty() ) {
      return;
    }

    final DrillDownParameterRefreshEvent event = new DrillDownParameterRefreshEvent( this, getDrillDownParameter() );
    for ( int i = 0; i < listeners.size(); i++ ) {
      final DrillDownParameterRefreshListener listener = listeners.get( i );
      listener.requestParameterRefresh( event );
    }
  }

  public boolean isSingleTabMode() {
    return singleTabMode;
  }

  public void setSingleTabMode( final boolean singleTabMode ) {
    this.singleTabMode = singleTabMode;
    rebuildUi();
  }

  public boolean isHideParameterUi() {
    return hideParameterUiCheckbox.isSelected();
  }

  public void setHideParameterUi( final boolean hideParameterUi ) {
    this.hideParameterUiCheckbox.setSelected( hideParameterUi );
  }

  public boolean isShowHideParameterUiCheckbox() {
    return hideParameterUiCheckbox.isVisible();
  }

  public void setShowHideParameterUiCheckbox( final boolean showHideParameterUiCheckbox ) {
    this.hideParameterUiCheckbox.setVisible( showHideParameterUiCheckbox );
    rebuildUi();
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    hideParameterUiCheckbox.setEnabled( enabled );
    allInOneTable.setEnabled( enabled );
    systemParameterTable.setEnabled( enabled );
    manualParameterTable.setEnabled( enabled );
    predefinedParameterTable.setEnabled( enabled );
    refreshParameterAction.setEnabled( enabled && listeners.isEmpty() == false );
    allInOneRemoveAction.setEnabled( enabled );
    manualParameterRemoveAction.setEnabled( enabled );
    addParameterAction.setEnabled( enabled );
    advancedEditorAction.setEnabled( enabled );

  }

}
