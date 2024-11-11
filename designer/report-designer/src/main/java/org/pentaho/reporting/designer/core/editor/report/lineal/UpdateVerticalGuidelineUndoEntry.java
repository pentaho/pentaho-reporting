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


package org.pentaho.reporting.designer.core.editor.report.lineal;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class UpdateVerticalGuidelineUndoEntry implements UndoEntry {
  private int index;
  private GuideLine guideLine;
  private GuideLine oldGuideLine;
  private InstanceID id;

  public UpdateVerticalGuidelineUndoEntry( final int index,
                                           final GuideLine guideLine,
                                           final GuideLine oldGuideLine,
                                           final InstanceID id ) {
    this.index = index;
    this.guideLine = guideLine;
    this.oldGuideLine = oldGuideLine;
    this.id = id;
  }

  public void undo( final ReportDocumentContext context ) {
    final AbstractReportDefinition abstractReportDefinition = context.getReportDefinition();
    final Band band = (Band) ModelUtility.findElementById( abstractReportDefinition, id );
    final LinealModel linealModel = ModelUtility.getVerticalLinealModel( band );
    linealModel.updateGuideLine( index, oldGuideLine );
  }

  public void redo( final ReportDocumentContext context ) {
    final AbstractReportDefinition abstractReportDefinition = context.getReportDefinition();
    final Band band = (Band) ModelUtility.findElementById( abstractReportDefinition, id );
    final LinealModel linealModel = ModelUtility.getVerticalLinealModel( band );
    linealModel.updateGuideLine( index, guideLine );
  }

  public UndoEntry merge( final UndoEntry other ) {
    return null;
  }
}
