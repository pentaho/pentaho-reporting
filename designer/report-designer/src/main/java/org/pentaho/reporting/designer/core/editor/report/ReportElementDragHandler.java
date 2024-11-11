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


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

public interface ReportElementDragHandler {
  public int dragStarted( final DropTargetDragEvent event,
                          final ReportElementEditorContext dragContext,
                          final ElementMetaData elementMetaData,
                          final String fieldName );

  public int dragUpdated( final DropTargetDragEvent event,
                          final ReportElementEditorContext dragContext,
                          final ElementMetaData elementMetaData,
                          final String fieldName );

  public void dragAborted( final DropTargetEvent event,
                           final ReportElementEditorContext dragContext );

  public void drop( final DropTargetDropEvent event,
                    final ReportElementEditorContext dragContext,
                    final ElementMetaData elementMetaData,
                    final String fieldName );
}
