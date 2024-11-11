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
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StyleExpressionEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private StyleKey styleKey;
  private Expression newValue;
  private Expression oldValue;

  public StyleExpressionEditUndoEntry( final InstanceID target,
                                       final StyleKey styleKey,
                                       final Expression oldValue,
                                       final Expression newValue ) {
    this.target = target;
    this.styleKey = styleKey;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setStyleExpression( styleKey, oldValue );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setStyleExpression( styleKey, newValue );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    if ( newEntry instanceof StyleExpressionEditUndoEntry == false ) {
      return null;
    }

    final StyleExpressionEditUndoEntry entry = (StyleExpressionEditUndoEntry) newEntry;
    if ( entry.target == target &&
      ObjectUtilities.equal( entry.styleKey, styleKey ) ) {
      return newEntry;
    }
    return null;
  }
}
