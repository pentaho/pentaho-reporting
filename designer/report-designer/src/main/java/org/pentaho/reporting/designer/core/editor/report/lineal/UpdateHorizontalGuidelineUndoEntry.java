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

package org.pentaho.reporting.designer.core.editor.report.lineal;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class UpdateHorizontalGuidelineUndoEntry implements UndoEntry {
  private int index;
  private GuideLine guideLine;
  private GuideLine oldGuideLine;

  public UpdateHorizontalGuidelineUndoEntry( final int index,
                                             final GuideLine guideLine,
                                             final GuideLine oldGuideLine ) {
    this.index = index;
    this.guideLine = guideLine;
    this.oldGuideLine = oldGuideLine;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final LinealModel linealModel = ModelUtility.getHorizontalLinealModel( abstractReportDefinition );
    linealModel.updateGuideLine( index, oldGuideLine );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final LinealModel linealModel = ModelUtility.getHorizontalLinealModel( abstractReportDefinition );
    linealModel.updateGuideLine( index, guideLine );
  }

  public UndoEntry merge( final UndoEntry other ) {
    return null;
  }
}
