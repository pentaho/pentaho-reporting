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
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.FormulaFragmentCellRenderer;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SystemParameterDialog extends CommonDialog {
  private ElementMetaDataTable systemParameterTable;

  /**
   * Creates a new modal dialog.
   */
  public SystemParameterDialog( final DrillDownParameterTableModel parameterTableModel,
                                final ReportDesignerContext reportDesignerContext ) {
    init( parameterTableModel, reportDesignerContext );
  }

  public SystemParameterDialog( final Frame owner,
                                final DrillDownParameterTableModel parameterTableModel,
                                final ReportDesignerContext reportDesignerContext )
    throws HeadlessException {
    super( owner );
    init( parameterTableModel, reportDesignerContext );
  }

  public SystemParameterDialog( final Dialog owner,
                                final DrillDownParameterTableModel parameterTableModel,
                                final ReportDesignerContext reportDesignerContext )
    throws HeadlessException {
    super( owner );
    init( parameterTableModel, reportDesignerContext );
  }

  protected void init( final DrillDownParameterTableModel parameterTableModel,
                       final ReportDesignerContext reportDesignerContext ) {
    if ( parameterTableModel == null ) {
      throw new NullPointerException();
    }

    setTitle( Messages.getString( "SystemParameterDialog.Title" ) );
    systemParameterTable = new ElementMetaDataTable();
    systemParameterTable.setReportDesignerContext( reportDesignerContext );
    systemParameterTable.setFormulaFragment( true );
    systemParameterTable.setDefaultEditor( GroupedName.class, new GroupedNameCellEditor() );
    systemParameterTable.setDefaultRenderer( String.class, new FormulaFragmentCellRenderer() );
    systemParameterTable
      .setModel( new FilteringParameterTableModel( DrillDownParameter.Type.SYSTEM, parameterTableModel ) );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.SystemParameter";
  }

  protected boolean hasCancelButton() {
    return false;
  }

  protected Component createContentPane() {
    final JPanel panel = new JPanel();
    panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    panel.setLayout( new BorderLayout( 5, 5 ) );
    panel.add( new JLabel( Messages.getString( "SystemParameterDialog.Available" ) ), BorderLayout.NORTH );
    panel.add( new JScrollPane( systemParameterTable ), BorderLayout.CENTER );
    return panel;
  }

  public void showAdvancedEditor() {
    performEdit();
  }
}
