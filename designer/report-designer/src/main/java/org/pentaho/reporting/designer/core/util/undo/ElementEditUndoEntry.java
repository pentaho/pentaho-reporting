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
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class ElementEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private int position;
  private Element oldElement;
  private Element newElement;

  public ElementEditUndoEntry( final InstanceID target,
                               final int position,
                               final Element oldElement,
                               final Element newElement ) {
    if ( position < 0 ) {
      throw new IllegalArgumentException();
    }
    if ( target == null ) {
      throw new NullPointerException();
    }

    this.target = target;
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final Band elementById = (Band)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    if ( newElement != null ) {
      elementById.removeElement( newElement );
    }
    if ( oldElement != null ) {
      elementById.addElement( position, oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final Band elementById = (Band)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    if ( oldElement != null ) {
      elementById.removeElement( oldElement );
    }
    if ( newElement != null ) {
      elementById.addElement( position, newElement );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
