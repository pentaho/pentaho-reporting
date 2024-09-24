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

import java.io.Serializable;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface UndoEntry extends Serializable {
  public void undo( final ReportDocumentContext renderContext );

  public void redo( final ReportDocumentContext renderContext );

  public UndoEntry merge( final UndoEntry newEntry );
}
