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

package gui;

import javax.swing.*;
import java.awt.*;

public class ExpressionPropertyMetaDataEditor extends JDialog {
  private EditableExpressionPropertyMetaData[] metaData;
  private JTable expressionsTable;
  private EditableMetaDataTableModel expressionsTableModel;

  public ExpressionPropertyMetaDataEditor() {
    init();
  }

  public ExpressionPropertyMetaDataEditor( final Frame owner ) {
    super( owner );
    init();
  }

  public ExpressionPropertyMetaDataEditor( final Dialog owner ) {
    super( owner );
    init();
  }

  public void init() {
    setTitle( "Expression Property Metadata Editor" );
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    expressionsTableModel = new EditableMetaDataTableModel();
    expressionsTable = new JTable( expressionsTableModel );
    expressionsTable.setDefaultRenderer( String.class, new EditableMetaDataRenderer() );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( new JScrollPane( expressionsTable ), BorderLayout.CENTER );
    setContentPane( contentPane );
    setSize( 800, 600 );
  }

  public void performEdit( final String name,
                           final EditableExpressionPropertyMetaData[] metaData ) {
    setTitle( name + " - Expression Property Metadata Editor" );
    this.metaData = metaData.clone();
    expressionsTableModel.populate( this.metaData );
    setVisible( true );
  }
}
