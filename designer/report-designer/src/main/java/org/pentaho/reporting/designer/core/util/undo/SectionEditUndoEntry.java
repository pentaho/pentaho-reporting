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
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class SectionEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private int position;
  private Element oldElement;
  private Element newElement;

  public SectionEditUndoEntry( final InstanceID target,
                               final int position,
                               final Element oldElement,
                               final Element newElement ) {
    this.target = target;
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final Section elementById = (Section)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setElementAt( position, oldElement );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final Section elementById = (Section)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setElementAt( position, newElement );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
