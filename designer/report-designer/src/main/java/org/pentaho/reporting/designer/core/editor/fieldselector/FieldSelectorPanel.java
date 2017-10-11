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

package org.pentaho.reporting.designer.core.editor.fieldselector;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDataChangeListener;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFieldNode;
import org.pentaho.reporting.designer.core.model.DataSchemaUtility;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.dnd.FieldDescriptionTransferable;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

public class FieldSelectorPanel extends SidePanel {
  private class ReportModelChangeHandler implements ReportDataChangeListener, SettingsListener {
    private ReportModelChangeHandler() {
    }

    public void dataModelChanged( final ReportDocumentContext context ) {
      final ReportDesignerContext designerContext = getReportDesignerContext();
      final ReportDocumentContext activeContext = designerContext.getActiveContext();
      if ( activeContext == null ) {
        return;
      }

      dataModel.setDataSchema( computeColumns( activeContext ) );
    }

    public void settingsChanged() {
      final ReportDesignerContext designerContext = getReportDesignerContext();
      final ReportDocumentContext activeContext = designerContext.getActiveContext();
      if ( activeContext == null ) {
        return;
      }
      dataModel.setDataSchema( computeColumns( activeContext ) );
    }
  }

  private FieldSelectorTableModel dataModel;
  private JTable table;
  private ReportModelChangeHandler changeHandler;
  private AbstractReportDefinition report;

  public FieldSelectorPanel() {
    setLayout( new BorderLayout() );

    dataModel = new FieldSelectorTableModel();

    table = new JTable();
    table.setModel( dataModel );
    table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    table.setTransferHandler( new ColumnTransferHandler() );
    table.setDefaultRenderer( ReportFieldNode.class, new FieldCellRenderer() );
    table.setDragEnabled( true );

    changeHandler = new ReportModelChangeHandler();
    WorkspaceSettings.getInstance().addSettingsListener( changeHandler );

    add( new JScrollPane( table ) );
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
    if ( oldContext != null ) {
      oldContext.removeReportDataChangeListener( changeHandler );
    }

    super.updateActiveContext( oldContext, newContext );
    if ( newContext == null ) {
      report = null;
      dataModel.setDataSchema( FieldSelectorTableModel.EMPTY_NODES );
    } else {
      report = newContext.getReportDefinition();
      newContext.addReportDataChangeListener( changeHandler );
      dataModel.setDataSchema( computeColumns( newContext ) );
    }
  }

  protected ReportFieldNode[] computeColumns( final ReportDocumentContext context ) {
    ContextAwareDataSchemaModel model = context.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<ReportFieldNode> nodes = new ArrayList<ReportFieldNode>( columnNames.length );
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String name = columnNames[ i ];
      final DataAttributes attributes = model.getDataSchema().getAttributes( name );
      if ( attributes != null ) {
        if ( DataSchemaUtility.isFiltered( attributes, model.getDataAttributeContext() ) ) {
          continue;
        }
        final Class type = (Class) attributes.getMetaAttribute
          ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class,
            model.getDataAttributeContext() );
        nodes.add( new ReportFieldNode( context, name, type ) );
      } else {
        nodes.add( new ReportFieldNode( context, name, Object.class ) );
      }
    }
    return nodes.toArray( new ReportFieldNode[ nodes.size() ] );
  }

  private class ColumnTransferHandler extends TransferHandler {
    /**
     * Creates a <code>Transferable</code> to use as the source for a data transfer. Returns the representation of the
     * data to be transferred, or <code>null</code> if the component's property is <code>null</code>
     *
     * @param c the component holding the data to be transferred; this argument is provided to enable sharing of
     *          <code>TransferHandler</code>s by multiple components
     * @return the representation of the data to be transferred, or <code>null</code> if the property associated with
     * <code>c</code> is <code>null</code>
     */
    protected Transferable createTransferable( final JComponent c ) {
      if ( c != table ) {
        return null;
      }

      final int selectedRow = table.getSelectedRow();
      if ( selectedRow == -1 ) {
        return null;
      }

      return new FieldDescriptionTransferable( dataModel.getFieldName( selectedRow ) );
    }

    public int getSourceActions( final JComponent c ) {
      return COPY;
    }
  }
}
