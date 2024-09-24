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

package org.pentaho.reporting.ui.datasources.table;

import org.apache.poi.ss.usermodel.Workbook;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class SheetSelectorDialog extends CommonDialog {
  private Workbook workbook;
  private JComboBox sheetsComboBox;

  public SheetSelectorDialog( final Workbook aWorkbook, final JDialog aParent ) {
    super( aParent );
    workbook = aWorkbook;
    init();
  }

  protected void init() {
    final Object[] theSheetsData = new Object[ workbook.getNumberOfSheets() ];
    for ( int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
      theSheetsData[ i ] = workbook.getSheetName( i );
    }
    sheetsComboBox = new JComboBox( theSheetsData );

    setTitle( Messages.getString( "SheetSelectorDialog.Import" ) );
    setLayout( new BorderLayout() );
    setModal( true );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    super.init();
  }

  protected String getDialogId() {
    return "TableDataSourceEditor.SheetSelector";
  }

  protected Component createContentPane() {
    final JPanel centerPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
    final JLabel theSheetLabel = new JLabel( Messages.getString( "SheetSelectorDialog.ChooseSheet" ) );
    centerPanel.add( theSheetLabel );
    centerPanel.add( sheetsComboBox );
    return centerPanel;
  }

  public int getSelectedIndex() {
    return sheetsComboBox.getSelectedIndex();
  }

  public boolean performSelection() {
    return super.performEdit();
  }
}
