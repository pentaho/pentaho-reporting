/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.editor.styles;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellRenderer;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.libraries.designtime.swing.DefaultTableHeaderRenderer;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class StyleEditorPanel extends SidePanel {
  protected static final Element[] EMPTY_DATA = new Element[ 0 ];

  private class ReportModelChangeHandler implements ReportModelListener {
    private ReportModelChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() instanceof Element == false ) {
        return;
      }
      // we dont have to deal with delete or add events, as this is handled by the selection model.
      // once the selection changed, we will get informed through other channels.
      final Object element = event.getElement();
      final Element[] data = getData();
      for ( int i = 0; i < data.length; i++ ) {
        final Element reportElement = data[ i ];
        if ( element == reportElement ) {
          // refresh, but one time is enough
          // refresh, dont call stopCellEditing ..
          refreshData();
          return;
        }
      }
    }
  }

  private StyleTableModel dataModel;
  private ElementMetaDataTable table;
  private ReportModelChangeHandler changeHandler;
  private AbstractReportDefinition report;
  private SortHeaderPanel headerPanel;

  public StyleEditorPanel() {
    setLayout( new BorderLayout() );

    dataModel = createDataModel();

    table = new ElementMetaDataTable();
    table.setModel( new GroupedMetaTableModel( dataModel ) );
    table.getColumnModel().getColumn( 0 ).setCellRenderer( new GroupedNameCellRenderer() );
    applyHeaderSize( table.getColumnModel().getColumn( 1 ) );
    applyHeaderSize( table.getColumnModel().getColumn( 3 ) );

    changeHandler = new ReportModelChangeHandler();
    headerPanel = new SortHeaderPanel( dataModel );

    add( headerPanel, BorderLayout.NORTH );
    add( new JScrollPane( table ), BorderLayout.CENTER );
  }

  protected StyleTableModel createDataModel() {
    return new StyleTableModel();
  }

  private void applyHeaderSize( final TableColumn col ) {
    col.setHeaderRenderer( new DefaultTableHeaderRenderer() );
    col.sizeWidthToFit();
  }

  protected void updateSelection( final DocumentContextSelectionModel model ) {
    List<Element> selectedElementsOfType = model.getSelectedElementsOfType( Element.class );
    final Element[] visualElements = selectedElementsOfType.toArray( new Element[ selectedElementsOfType.size() ] );
    setData( visualElements );
  }

  public Element[] getData() {
    return dataModel.getData();
  }

  protected void refreshData() {
    dataModel.setData( dataModel.getData() );
  }

  public void setData( final Element[] elements ) {
    final TableCellEditor tableCellEditor = table.getCellEditor();
    if ( tableCellEditor != null ) {
      tableCellEditor.stopCellEditing();
    }

    dataModel.setData( elements );
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    table.setEnabled( enabled );
    headerPanel.setEnabled( enabled );
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
    table.stopEditing();

    super.updateActiveContext( oldContext, newContext );
    if ( report != null ) {
      report.removeReportModelListener( changeHandler );
    }
    if ( newContext == null ) {
      report = null;
      dataModel.setReportRenderContext( null );
      setData( EMPTY_DATA );
    } else {
      report = newContext.getReportDefinition();
      report.addReportModelListener( changeHandler );
      dataModel.setReportRenderContext( newContext );
    }
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    super.updateDesignerContext( oldContext, newContext );
    table.setReportDesignerContext( newContext );
  }

  protected StyleTableModel getDataModel() {
    return dataModel;
  }
}
