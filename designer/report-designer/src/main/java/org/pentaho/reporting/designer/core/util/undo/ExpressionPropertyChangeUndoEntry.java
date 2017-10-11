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

package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

/**
 * Used when a expression property has
 *
 * @author Thomas Morgner
 */
public class ExpressionPropertyChangeUndoEntry implements UndoEntry {
  private Expression element;
  private Object oldPropertyValue;
  private Object newPropertyValue;
  private String name;

  public ExpressionPropertyChangeUndoEntry( final Expression element,
                                            final String name,
                                            final Object oldPropertyValue,
                                            final Object newPropertyValue ) {
    this.element = element;
    this.name = name;
    this.oldPropertyValue = oldPropertyValue;
    this.newPropertyValue = newPropertyValue;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    try {
      final BeanUtility bu = new BeanUtility( element );
      bu.setProperty( name, oldPropertyValue );
      renderContext.getReportDefinition().fireModelLayoutChanged
        ( renderContext.getReportDefinition(), ReportModelEvent.NODE_PROPERTIES_CHANGED, element );
    } catch ( final Exception e ) {
      throw new IllegalStateException();
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    try {
      final BeanUtility bu = new BeanUtility( element );
      bu.setProperty( name, newPropertyValue );
      renderContext.getReportDefinition().fireModelLayoutChanged
        ( renderContext.getReportDefinition(), ReportModelEvent.NODE_PROPERTIES_CHANGED, element );
    } catch ( final Exception e ) {
      throw new IllegalStateException();
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    if ( newEntry instanceof ExpressionPropertyChangeUndoEntry == false ) {
      return null;
    }

    final ExpressionPropertyChangeUndoEntry entry = (ExpressionPropertyChangeUndoEntry) newEntry;
    if ( entry.element == element ) {
      return newEntry;
    }
    return null;
  }
}
