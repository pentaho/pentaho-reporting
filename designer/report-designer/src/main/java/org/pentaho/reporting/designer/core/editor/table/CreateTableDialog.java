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
