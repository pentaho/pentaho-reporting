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
import org.pentaho.reporting.designer.core.editor.ReportDataChangeListener;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.TagListTableCellEditor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 * @author Ezequiel Cuellar
 */

public class SubReportParameterDialog extends CommonDialog implements ReportDataChangeListener {
  public static class EditResult {
    private ParameterMapping[] importParameters;
    private ParameterMapping[] exportParameters;

    public EditResult( final ParameterMapping[] importParameters, final ParameterMapping[] exportParameters ) {
      this.importParameters = importParameters.clone();
      this.exportParameters = exportParameters.clone();
    }

    public ParameterMapping[] getImportParameters() {
      return importParameters;
    }

    public ParameterMapping[] getExportParameters() {
      return exportParameters;
    }
  }

  private static class RemoveParameterAction extends AbstractAction implements ListSelectionListener {
    private JTable exportTable;

    private RemoveParameterAction( final JTable exportTable ) {
      this.exportTable = exportTable;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "SubReportParameterDialog.RemoveParameter" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final int i = exportTable.getSelectedRow();
      if ( i == -1 ) {
        return;
      }

      final ParameterMappingTableModel tableModel = (ParameterMappingTableModel) exportTable.getModel();
      tableModel.removeRow( i );
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( exportTable.getSelectedRow() != -1 );
    }
  }


  private static class AddParameterAction extends AbstractAction {
    private JTable exportTable;

    private AddParameterAction( final JTable exportTable ) {
      this.exportTable = exportTable;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "SubReportParameterDialog.AddParameter" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final ParameterMappingTableModel tableModel = (ParameterMappingTableModel) exportTable.getModel();
      tableModel.addRow();
    }
  }

  private JTable importTable;
  private JTable exportTable;
  private TagListTableCellEditor importInnerTableCellEditor;
  private TagListTableCellEditor importOuterTableCellEditor;
  private TagListTableCellEditor exportInnerTableCellEditor;
  private TagListTableCellEditor exportOuterTableCellEditor;
  private ReportDocumentContext activeReportContext;
  private ReportDocumentContext parentReportContext;

  public SubReportParameterDialog() {
    init();
  }

  public SubReportParameterDialog( final Dialog aParent ) {
    super( aParent );
    init();
  }

  public SubReportParameterDialog( final Frame aParent ) {
    super( aParent );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "SubReportParameterDialog.Title" ) );
    setModal( true );

    importInnerTableCellEditor = new TagListTableCellEditor();
    importOuterTableCellEditor = new TagListTableCellEditor();
    exportInnerTableCellEditor = new TagListTableCellEditor();
    exportOuterTableCellEditor = new TagListTableCellEditor();

    exportTable = new JTable( new ParameterMappingTableModel() );
    exportTable.setShowHorizontalLines( true );
    exportTable.setShowVerticalLines( true );
    exportTable.setGridColor( SystemColor.controlShadow );
    exportTable.getColumnModel().getColumn( 0 ).setCellEditor( exportOuterTableCellEditor );
    exportTable.getColumnModel().getColumn( 1 ).setCellEditor( exportInnerTableCellEditor );
    exportTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );


    importTable = new JTable( new ParameterMappingTableModel() );
    importTable.setShowHorizontalLines( true );
    importTable.setShowVerticalLines( true );
    importTable.setGridColor( SystemColor.controlShadow );
    importTable.getColumnModel().getColumn( 0 ).setCellEditor( importOuterTableCellEditor );
    importTable.getColumnModel().getColumn( 1 ).setCellEditor( importInnerTableCellEditor );
    importTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.SubReportParameter";
  }

  protected Component createContentPane() {

    final JButton importParamAddButton = new BorderlessButton( new AddParameterAction( importTable ) );
    final JButton importParamRemoveButton = new BorderlessButton( new RemoveParameterAction( importTable ) );

    final JPanel importButtonsToolbar = new JPanel();
    importButtonsToolbar.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    importButtonsToolbar.add( importParamAddButton );
    importButtonsToolbar.add( importParamRemoveButton );

    final JPanel theImportPanel = new JPanel( new BorderLayout() );
    theImportPanel.setBorder( BorderFactory.createTitledBorder( Messages.getString
      ( "SubReportParameterDialog.ImportParameter" ) ) );
    theImportPanel.add( new JScrollPane( importTable ), BorderLayout.CENTER );
    theImportPanel.add( importButtonsToolbar, BorderLayout.NORTH );

    final JButton exportParamAddButton = new BorderlessButton( new AddParameterAction( exportTable ) );
    final JButton exportParamRemoveButton = new BorderlessButton( new RemoveParameterAction( exportTable ) );

    final JPanel exportButtonsToolbar = new JPanel();
    exportButtonsToolbar.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    exportButtonsToolbar.add( exportParamAddButton );
    exportButtonsToolbar.add( exportParamRemoveButton );

    final JPanel exportPanel = new JPanel( new BorderLayout() );
    exportPanel.setBorder( BorderFactory.createTitledBorder
      ( Messages.getString( "SubReportParameterDialog.ExportParameter" ) ) );
    exportPanel.add( new JScrollPane( exportTable ), BorderLayout.CENTER );
    exportPanel.add( exportButtonsToolbar, BorderLayout.NORTH );

    final JPanel mainPanel = new JPanel( new GridLayout( 1, 2 ) );
    mainPanel.add( theImportPanel );
    mainPanel.add( exportPanel );
    return mainPanel;
  }

  public void dataModelChanged( final ReportDocumentContext context ) {
    configureEditors();
  }

  public EditResult performEdit( final ReportDesignerContext context,
                                 final ParameterMapping[] importParameters,
                                 final ParameterMapping[] exportParameters ) {
    try {
      this.activeReportContext = context.getActiveContext();
      if ( activeReportContext == null ) {
        throw new IllegalStateException( "ActiveContext should not be null when editing a report." );
      }
      this.activeReportContext.addReportDataChangeListener( this );

      this.parentReportContext = findParentContext( context );
      if ( this.parentReportContext != null ) {
        this.parentReportContext.addReportDataChangeListener( this );
      }

      configureEditors();

      final ParameterMappingTableModel importModel = (ParameterMappingTableModel) importTable.getModel();
      importModel.setMappings( importParameters );

      final ParameterMappingTableModel exportModel = (ParameterMappingTableModel) exportTable.getModel();
      exportModel.setMappings( exportParameters );

      if ( performEdit() == false ) {
        return null;
      }
      return saveParameters();
    } finally {
      if ( activeReportContext != null ) {
        activeReportContext.removeReportDataChangeListener( this );
      }
      if ( parentReportContext != null ) {
        parentReportContext.removeReportDataChangeListener( this );
      }
    }
  }

  private String[] add( final String value, final String[] base ) {
    ArrayList<String> tmp = new ArrayList<String>();
    tmp.add( value );
    tmp.addAll( Arrays.asList( base ) );
    return tmp.toArray( new String[ tmp.size() ] );
  }

  private String[] collectParentContextFields() {
    if ( parentReportContext == null ) {
      return new String[ 0 ];
    }
    return parentReportContext.getReportDataSchemaModel().getColumnNames();
  }

  private ReportDocumentContext findParentContext( final ReportDesignerContext context ) {
    final ReportDocumentContext activeContext = context.getActiveContext();
    final Section parentSection = activeContext.getReportDefinition().getParentSection();
    if ( parentSection == null ) {
      return null;
    }
    final ReportDefinition parentReport = parentSection.getReportDefinition();
    if ( parentReport == null ) {
      return null;
    }

    final int contextCount = context.getReportRenderContextCount();
    for ( int i = 0; i < contextCount; i += 1 ) {
      final ReportRenderContext contextAt = context.getReportRenderContext( i );
      if ( parentReport == contextAt.getReportDefinition() ) {
        return contextAt;
      }
    }
    return null;
  }

  private void configureEditors() {
    String[] parentNames = collectParentContextFields();
    importOuterTableCellEditor.setTags( add( "*", parentNames ) );
    exportOuterTableCellEditor.setTags( parentNames );

    // Add any unique columns from import/export outer to the inner parameter
    // list for both the import and export panels
    List<String> columnNames = new ArrayList<String>();
    columnNames.add( "*" );
    columnNames.addAll( Arrays.asList( activeReportContext.getReportDataSchemaModel().getColumnNames() ) );

    List<String> l = new LinkedList<String>( Arrays.asList( parentNames ) );
    l.removeAll( columnNames );
    columnNames.addAll( l );

    String[] paramList = columnNames.toArray( new String[ columnNames.size() ] );
    importInnerTableCellEditor.setTags( paramList );
    exportInnerTableCellEditor.setTags( paramList );
  }

  private EditResult saveParameters() {
    TableCellEditor theCellEditor = importTable.getCellEditor();
    if ( theCellEditor != null ) {
      theCellEditor.stopCellEditing();
    }

    theCellEditor = exportTable.getCellEditor();
    if ( theCellEditor != null ) {
      theCellEditor.stopCellEditing();
    }

    final ParameterMappingTableModel importModel = (ParameterMappingTableModel) importTable.getModel();
    final ParameterMappingTableModel exportModel = (ParameterMappingTableModel) exportTable.getModel();

    return new EditResult( importModel.getMappings(), exportModel.getMappings() );
  }

}
