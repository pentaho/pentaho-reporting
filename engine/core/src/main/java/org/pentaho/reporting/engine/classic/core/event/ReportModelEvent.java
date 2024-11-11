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


package org.pentaho.reporting.engine.classic.core.event;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;

import java.util.EventObject;

public class ReportModelEvent extends EventObject {
  public static final int NODE_PROPERTIES_CHANGED = 0;
  public static final int NODE_ADDED = 1;
  public static final int NODE_REMOVED = 2;
  public static final int NODE_STRUCTURE_CHANGED = NODE_ADDED | NODE_REMOVED;

  private Object element;
  private int type;
  private Object parameter;

  public ReportModelEvent( final ReportDefinition source, final Object sourceElement, final int type,
      final Object parameter ) {
    super( source );
    this.element = sourceElement;
    this.type = type;
    this.parameter = parameter;
  }

  public int getType() {
    return type;
  }

  public Object getParameter() {
    return parameter;
  }

  public boolean isNodeAddedEvent() {
    return ( type & NODE_ADDED ) == NODE_ADDED;
  }

  public boolean isNodeDeleteEvent() {
    return ( type & NODE_REMOVED ) == NODE_REMOVED;
  }

  public boolean isNodeStructureChanged() {
    return ( type & NODE_STRUCTURE_CHANGED ) == NODE_STRUCTURE_CHANGED;
  }

  public ReportDefinition getReport() {
    return (ReportDefinition) getSource();
  }

  public Object getElement() {
    return element;
  }
}
