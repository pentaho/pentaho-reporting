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
