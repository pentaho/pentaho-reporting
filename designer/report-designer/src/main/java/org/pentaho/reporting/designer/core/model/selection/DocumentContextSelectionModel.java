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


package org.pentaho.reporting.designer.core.model.selection;

import java.util.List;

public interface DocumentContextSelectionModel {
  public boolean add( Object o );

  public void remove( Object o );

  public boolean isSelected( Object o );

  public void clearSelection();

  public int getSelectionCount();

  public Object[] getSelectedElements();

  public <T> List<T> getSelectedElementsOfType( Class<T> t );

  public Object getSelectedElement( int index );

  public void setSelectedElements( Object[] elements );

  public Object getLeadSelection();

  public void addReportSelectionListener( ReportSelectionListener listener );

  public void removeReportSelectionListener( ReportSelectionListener listener );

}
