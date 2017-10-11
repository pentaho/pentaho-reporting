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

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.table.FormulaFragmentCellEditor;
import org.pentaho.openformula.ui.table.FormulaFragmentCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.icons.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTable;
import org.pentaho.reporting.libraries.formula.FormulaContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings( "HardCodedStringLiteral" )
public class FormulaParameterEditor extends JComponent {
  private class AddArgumentAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddArgumentAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "FormulaParameterEditor.Add" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      FormulaParameterEntity entity = new FormulaParameterEntity( FormulaParameterEntity.Type.ARGUMENT,
        String.format( "ZZZ%012d", System.currentTimeMillis() ), null );
      int idx = argumentParameterTable.getSelectedRow();
      insertNewEntry( idx, entity );
    }
  }

  private class AddParameterAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddParameterAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "FormulaParameterEditor.Add" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      String parameterName = "parameter";
      FormulaParameterEntity entity =
        new FormulaParameterEntity( FormulaParameterEntity.Type.PARAMETER, parameterName, null );
      int idx = systemParameterTable.getSelectedRow();
      insertNewEntry( idx, entity );
    }
  }

  private class RemoveParameterAction extends AbstractAction implements ListSelectionListener {
    private FilteringParameterTableModel model;
    private JTable table;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     *
     * @param table the table.
     */
    private RemoveParameterAction( final JTable table ) {
      this.model = (FilteringParameterTableModel) table.getModel();
      this.table = table;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "FormulaParameterEditor.Remove" ) );
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
        final FormulaParameterEntity.Type type = parameterTableModel.getParameterType( model.mapToModel( row ) );
        if ( FormulaParameterEntity.Type.ARGUMENT == type ) {
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

      final FormulaParameterEntity[] data = parameterTableModel.getData();
      final ListSelectionModel listSelectionModel = table.getSelectionModel();
      final ArrayList<FormulaParameterEntity> result = new ArrayList<FormulaParameterEntity>( data.length );
      for ( int i = 0; i < data.length; i++ ) {
        final FormulaParameterEntity parameter = data[ i ];
        if ( parameter == null ) {
          continue;
        }
        if ( listSelectionModel.isSelectedIndex( model.mapFromModel( i ) ) == false ) {
          result.add( data[ i ] );
        }
      }

      parameterTableModel.setData( result.toArray( new FormulaParameterEntity[ result.size() ] ) );
    }
  }

  private class ParameterChangeHandler implements TableModelListener {
    private FormulaParameterEntity[] oldData;

    private ParameterChangeHandler() {
      oldData = parameterTableModel.getData();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed.
     */
    public void tableChanged( final TableModelEvent e ) {
      final FormulaParameterEntity[] newParams = getFormulaParameter();
      firePropertyChange( FORMULA_PARAMETER_PROPERTY, oldData, newParams );
      this.oldData = newParams;
    }
  }

  public static final String FORMULA_PARAMETER_PROPERTY = "formulaParameter";

  private FormulaParameterTableModel parameterTableModel;

  private FormulaFragmentCellEditor systemParameterEditor;
  private FormulaFragmentCellEditor argumentParameterEditor;

  private PropertyTable systemParameterTable;
  private PropertyTable argumentParameterTable;
  private RemoveParameterAction removeParameterAction;
  private RemoveParameterAction removeArgumentAction;
  private AddParameterAction addParameterAction;
  private AddArgumentAction addArgumentAction;

  public FormulaParameterEditor() {
    setLayout( new BorderLayout() );

    parameterTableModel = new FormulaParameterTableModel();
    parameterTableModel.addTableModelListener( new ParameterChangeHandler() );

    systemParameterEditor = new FormulaFragmentCellEditor();
    systemParameterTable = new PropertyTable();
    systemParameterTable.setDefaultEditor( String.class, systemParameterEditor );
    systemParameterTable.setDefaultEditor( FormulaParameterEntity.class, new FormulaParameterEntityCellEditor() );
    systemParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    systemParameterTable.setModel
      ( new FilteringParameterTableModel( new FormulaParameterFilterStrategy( FormulaParameterEntity.Type.PARAMETER ),
        parameterTableModel ) );

    FilteringParameterTableModel dataModel = new FilteringParameterTableModel
      ( new FormulaParameterFilterStrategy( FormulaParameterEntity.Type.ARGUMENT ), parameterTableModel );
    argumentParameterEditor = new FormulaFragmentCellEditor();
    argumentParameterTable = new PropertyTable();
    argumentParameterTable.setDefaultEditor( String.class, argumentParameterEditor );
    argumentParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    argumentParameterTable.setDefaultRenderer( FormulaParameterEntity.class, new ArgumentCountCellRenderer() );
    argumentParameterTable.setModel( dataModel );

    removeParameterAction = new RemoveParameterAction( systemParameterTable );
    removeArgumentAction = new RemoveParameterAction( argumentParameterTable );

    addParameterAction = new AddParameterAction();
    addArgumentAction = new AddArgumentAction();

    rebuildUi();
  }

  private void rebuildUi() {
    removeAll();
    final JPanel systemTablePanel = new JPanel( new BorderLayout() );
    systemTablePanel.add( new JScrollPane( systemParameterTable ), BorderLayout.CENTER );
    systemTablePanel.add( createButtonPanel( addParameterAction, removeParameterAction ), BorderLayout.NORTH );

    final JPanel manualTablePanel = new JPanel( new BorderLayout() );
    manualTablePanel.add( new JScrollPane( argumentParameterTable ), BorderLayout.CENTER );
    manualTablePanel.add( createButtonPanel( addArgumentAction, removeArgumentAction ), BorderLayout.NORTH );

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getInstance().getString( "FormulaParameterEditor.Tab.Parameter" ), systemTablePanel );
    tabbedPane.addTab( Messages.getInstance().getString( "FormulaParameterEditor.Tab.Arguments" ), manualTablePanel );

    add( tabbedPane, BorderLayout.CENTER );
  }

  private JPanel createButtonPanel( final Action addAction, final Action removeAction ) {
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonPanel.add( new BorderlessButton( addAction ) );
    buttonPanel.add( new BorderlessButton( removeAction ) );
    return buttonPanel;
  }

  public void setFields( final FieldDefinition[] fields ) {
    systemParameterEditor.setFields( fields );
    argumentParameterEditor.setFields( fields );
  }

  public void setFormulaParameter( final FormulaParameterEntity[] parameter ) {
    final FormulaParameterEntity[] oldParameter = parameterTableModel.getData();
    parameterTableModel.setData( parameter );
    if ( Arrays.equals( oldParameter, parameter ) == false ) {
      firePropertyChange( FORMULA_PARAMETER_PROPERTY, oldParameter, parameter );
    }
  }

  public FormulaParameterEntity[] getFormulaParameter() {
    return parameterTableModel.getData();
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    systemParameterTable.setEnabled( enabled );
    argumentParameterTable.setEnabled( enabled );
    removeParameterAction.setEnabled( enabled );
    removeArgumentAction.setEnabled( enabled );
    addParameterAction.setEnabled( enabled );
    addArgumentAction.setEnabled( enabled );
  }

  public void setFormulaContext( final FormulaContext context ) {
    argumentParameterEditor.setFormulaContext( context );
  }

  protected void insertNewEntry( final int idx, final FormulaParameterEntity entity ) {
    final FormulaParameterEntity[] data = parameterTableModel.getData();
    final ArrayList<FormulaParameterEntity> data2 = new ArrayList<FormulaParameterEntity>( Arrays.asList( data ) );
    if ( idx == -1 ) {
      data2.add( entity );
    } else {
      data2.add( idx, entity );
    }
    parameterTableModel.setData( data2.toArray( new FormulaParameterEntity[ data2.size() ] ) );
  }

}
