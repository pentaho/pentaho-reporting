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
