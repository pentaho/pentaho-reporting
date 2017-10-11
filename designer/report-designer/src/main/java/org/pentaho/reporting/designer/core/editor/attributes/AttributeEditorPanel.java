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

package org.pentaho.reporting.designer.core.editor.attributes;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellRenderer;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;

public class AttributeEditorPanel extends SidePanel {
  private class ReportModelChangeHandler implements ReportModelListener {
    private ReportModelChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() instanceof ReportElement == false ) {
        return;
      }
      // we dont have to deal with delete or add events, as this is handled by the selection model.
      // once the selection changed, we will get informed through other channels.
      final Object element = event.getElement();
      final ReportElement[] data = getData();
      for ( int i = 0; i < data.length; i++ ) {
        final ReportElement reportElement = data[ i ];
        if ( element == reportElement ) {
          // refresh, dont call stopCellEditing ..
          dataModel.setData( data );
          return;
        }
      }
    }
  }

  private static final ReportElement[] EMPTY_DATA = new ReportElement[ 0 ];

  private AttributeTableModel dataModel;
  private ElementMetaDataTable table;
  private AbstractReportDefinition report;
  private ReportModelChangeHandler changeHandler;
  private SortHeaderPanel headerPanel;

  public AttributeEditorPanel() {
    setLayout( new BorderLayout() );
    changeHandler = new ReportModelChangeHandler();
    dataModel = new AttributeTableModel();

    table = new ElementMetaDataTable();
    table.setModel( dataModel );
    table.getColumnModel().getColumn( 0 ).setCellRenderer( new GroupedNameCellRenderer() );

    headerPanel = new SortHeaderPanel( dataModel );

    add( headerPanel, BorderLayout.NORTH );
    add( new JScrollPane( table ), BorderLayout.CENTER );
  }

  protected void updateSelection( final DocumentContextSelectionModel model ) {
    List<Element> selectedElementsOfType = model.getSelectedElementsOfType( Element.class );
    final Element[] visualElements = selectedElementsOfType.toArray( new Element[ selectedElementsOfType.size() ] );
    setData( visualElements );
  }

  public ReportElement[] getData() {
    return dataModel.getData();
  }

  public void setData( final ReportElement[] elements ) {
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
      dataModel.setReportRenderContext( newContext );
      report.addReportModelListener( changeHandler );
    }
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    super.updateDesignerContext( oldContext, newContext );
    table.setReportDesignerContext( newContext );
  }
}
