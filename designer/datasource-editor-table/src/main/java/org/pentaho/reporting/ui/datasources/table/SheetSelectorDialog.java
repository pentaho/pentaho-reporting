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
