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

package org.pentaho.reporting.designer.core.editor.styles;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.styles.styleeditor.StyleDefinitionEditorContext;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellRenderer;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleChangeListener;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.libraries.designtime.swing.DefaultTableHeaderRenderer;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;

public class SimpleStyleEditorPanel extends SidePanel {
  protected static final Element[] EMPTY_DATA = new Element[ 0 ];

  private class ReportModelChangeHandler implements StyleChangeListener {
    private StyleDefinitionEditorContext editorContext;

    private ReportModelChangeHandler( final StyleDefinitionEditorContext editorContext ) {
      this.editorContext = editorContext;
    }

    public void styleChanged( final ElementStyleSheet source, final StyleKey key, final Object value ) {
      dataModel.setData( source );

      final ElementStyleDefinition editorStyleDefinition = editorContext.getStyleDefinition();
      editorStyleDefinition.updateRule( source );
    }

    public void styleRemoved( final ElementStyleSheet source, final StyleKey key ) {
      styleChanged( source, key, null );
    }
  }

  private SimpleStyleTableModel dataModel;
  private ElementMetaDataTable table;
  private ReportModelChangeHandler changeHandler;
  private SortHeaderPanel headerPanel;
  private StyleDefinitionEditorContext editorContext;

  public SimpleStyleEditorPanel( final StyleDefinitionEditorContext editorContext ) {
    setLayout( new BorderLayout() );

    this.editorContext = editorContext;

    dataModel = new SimpleStyleTableModel();


    table = new ElementMetaDataTable();
    table.setModel( new GroupedMetaTableModel( dataModel ) );
    table.getColumnModel().getColumn( 0 ).setCellRenderer( new GroupedNameCellRenderer() );
    applyHeaderSize( table.getColumnModel().getColumn( 1 ) );

    changeHandler = new ReportModelChangeHandler( editorContext );
    headerPanel = new SortHeaderPanel( dataModel );

    add( headerPanel, BorderLayout.NORTH );
    add( new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ),
      BorderLayout.CENTER );

    setEnabled( true );
  }

  private void applyHeaderSize( final TableColumn col ) {
    col.setHeaderRenderer( new DefaultTableHeaderRenderer() );
    col.sizeWidthToFit();
  }

  public ElementStyleSheet getData() {
    return dataModel.getData();
  }

  public void setData( final ElementStyleSheet elements ) {
    final TableCellEditor tableCellEditor = table.getCellEditor();
    if ( tableCellEditor != null ) {
      tableCellEditor.stopCellEditing();
    }

    final ElementStyleSheet data = dataModel.getData();
    if ( data != null ) {
      data.removeListener( changeHandler );
    }

    dataModel.setData( elements );
    if ( elements != null ) {
      elements.addListener( changeHandler );
    }
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    table.setEnabled( enabled );
    headerPanel.setEnabled( enabled );
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    super.updateDesignerContext( oldContext, newContext );
    table.setReportDesignerContext( newContext );
  }
}
