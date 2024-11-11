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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

public class CompoundUndoEntry implements UndoEntry {
  private UndoEntry[] undoEntries;

  public CompoundUndoEntry( final UndoEntry... undoEntries ) {
    this.undoEntries = undoEntries.clone();
  }

  public void undo( final ReportDocumentContext renderContext ) {
    for ( int i = undoEntries.length - 1; i >= 0; i-- ) {
      final UndoEntry undoEntry = undoEntries[ i ];
      undoEntry.undo( renderContext );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    for ( int i = 0; i < undoEntries.length; i++ ) {
      final UndoEntry undoEntry = undoEntries[ i ];
      undoEntry.redo( renderContext );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
