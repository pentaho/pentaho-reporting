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
