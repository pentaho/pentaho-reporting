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

package org.pentaho.reporting.designer.core.versionchecker;

import javax.swing.table.AbstractTableModel;

public class UpdateTableModel extends AbstractTableModel {
  private UpdateInfo[] updateList;

  public UpdateTableModel( final UpdateInfo[] updateList ) {
    this.updateList = updateList.clone();
  }

  public int getColumnCount() {
    return 2;
  }

  public Class<?> getColumnClass( final int columnIndex ) {
    return String.class;
  }

  public int getRowCount() {
    if ( updateList == null ) {
      return 0;
    }
    return updateList.length;
  }

  public Object getValueAt( final int row, final int column ) {
    if ( column == 0 ) {
      return updateList[ row ].getType();
    }
    return updateList[ row ].getVersion();
  }

  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return Messages.getInstance().getString( "UpdateTableModel.ReleaseType" );// NON-NLS
    }
    return Messages.getInstance().getString( "UpdateTableModel.Version" );// NON-NLS
  }
}
