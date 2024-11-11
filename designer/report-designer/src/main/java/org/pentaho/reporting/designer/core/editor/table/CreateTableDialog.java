/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.table;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.GroupLayoutUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateTableDialog extends CommonDialog {
  private JSpinner headerRowsField;
  private JSpinner dataRowsField;
  private JSpinner colsField;

  public CreateTableDialog() {
    init();
  }

  public CreateTableDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public CreateTableDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    dataRowsField = new JSpinner( new SpinnerNumberModel( 5, 1, 1000, 1 ) );
    headerRowsField = new JSpinner( new SpinnerNumberModel( 5, 1, 1000, 1 ) );
    colsField = new JSpinner( new SpinnerNumberModel( 5, 1, 1000, 1 ) );

    setTitle( "Insert Table .." );
    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.CreateTable";
  }

  protected Component createContentPane() {
    final JPanel root = new JPanel();
    root.setLayout( new BorderLayout() );
    root.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    root.add( GroupLayoutUtil.makeSimpleForm( 3, 2,
        new JLabel( "Columns" ), colsField,
        new JLabel( "Header Rows" ), headerRowsField,
        new JLabel( "Data Rows" ), dataRowsField ),
      BorderLayout.NORTH );
    return root;
  }

  public boolean createTable() {
    return super.performEdit();
  }

  public int getColumns() {
    final Object value = colsField.getValue();
    if ( value instanceof Integer ) {
      final Integer i = (Integer) value;
      return i.intValue();
    }
    return -1;
  }

  public int getHeaderRows() {
    final Object value = headerRowsField.getValue();
    if ( value instanceof Integer ) {
      final Integer i = (Integer) value;
      return i.intValue();
    }
    return -1;
  }

  public int getDataRows() {
    final Object value = dataRowsField.getValue();
    if ( value instanceof Integer ) {
      final Integer i = (Integer) value;
      return i.intValue();
    }
    return -1;
  }
}
