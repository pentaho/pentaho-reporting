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

package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.Messages;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * User: Martin Date: 01.02.2006 Time: 19:12:32
 */
public class InspectionResultTableModel extends AbstractTableModel implements InspectionResultListener {
  private ArrayList<InspectionResult> inspectionResults;

  public InspectionResultTableModel() {
    inspectionResults = new ArrayList<InspectionResult>();
  }

  public void notifyInspectionResult( final InspectionResult result ) {
    add( result );
  }

  public boolean add( final InspectionResult inspectionResult ) {
    for ( int i = 0; i < inspectionResults.size(); i++ ) {
      final InspectionResult result = inspectionResults.get( i );
      final int compareResult = result.getSeverity().compareTo( inspectionResult.getSeverity() );
      if ( compareResult < 0 ) {
        inspectionResults.add( i, inspectionResult );
        fireTableRowsInserted( i, i );
        return true;
      }
    }

    final int index = inspectionResults.size();
    inspectionResults.add( inspectionResult );
    fireTableRowsInserted( index, index );

    return true;
  }

  public String getColumnName( final int column ) {
    return Messages.getString( "InspectionResult.Description" );
  }


  public int getRowCount() {
    return inspectionResults.size();
  }


  public int getColumnCount() {
    return 1;
  }

  public Class<?> getColumnClass( final int columnIndex ) {
    return InspectionResult.class;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return inspectionResults.get( rowIndex );
  }

  public InspectionResult getInspectionResult( final int row ) {
    return inspectionResults.get( row );
  }

  public void clear() {
    inspectionResults.clear();
    fireTableDataChanged();
  }

  public void notifyInspectionStarted() {
    clear();
  }
}
