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
