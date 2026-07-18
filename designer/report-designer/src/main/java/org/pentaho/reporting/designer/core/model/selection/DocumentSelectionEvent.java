/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.model.selection;

import java.util.EventObject;

public class DocumentSelectionEvent extends EventObject {
  private Object element;

  public DocumentSelectionEvent( final DocumentContextSelectionModel source,
                                 final Object element ) {
    super( source );
    this.element = element;
  }

  public DocumentContextSelectionModel getModel() {
    return (DocumentContextSelectionModel) getSource();
  }

  public Object getElement() {
    return element;
  }

}
