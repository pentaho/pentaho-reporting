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
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class BandedSubreportEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private int position;
  private SubReport oldElement;
  private SubReport newElement;

  public BandedSubreportEditUndoEntry( final InstanceID target,
                                       final int position,
                                       final SubReport oldElement,
                                       final SubReport newElement ) {
    this.target = target;
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractRootLevelBand elementById = (AbstractRootLevelBand)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    if ( newElement != null ) {
      elementById.removeSubreport( newElement );
    }
    if ( oldElement != null ) {
      elementById.addSubReport( position, oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractRootLevelBand elementById = (AbstractRootLevelBand)
      ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    if ( oldElement != null ) {
      elementById.removeSubreport( oldElement );
    }
    if ( newElement != null ) {
      elementById.addSubReport( position, newElement );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
