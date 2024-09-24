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
