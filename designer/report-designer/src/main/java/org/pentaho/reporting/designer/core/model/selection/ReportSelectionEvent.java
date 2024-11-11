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


package org.pentaho.reporting.designer.core.model.selection;

public class ReportSelectionEvent extends DocumentSelectionEvent {
  public ReportSelectionEvent( final DocumentContextSelectionModel source, final Object element ) {
    super( source, element );
  }

  public DocumentContextSelectionModel getModel() {
    return (DocumentContextSelectionModel) getSource();
  }
}
