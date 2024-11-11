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

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class RemoveHorizontalGuidelineUndoEntry implements UndoEntry {
  private final GuideLine guideLine;

  public RemoveHorizontalGuidelineUndoEntry( final GuideLine guideLine ) {
    this.guideLine = guideLine;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final LinealModel linealModel = ModelUtility.getHorizontalLinealModel( abstractReportDefinition );
    linealModel.addGuidLine( guideLine );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final LinealModel linealModel = ModelUtility.getHorizontalLinealModel( abstractReportDefinition );
    linealModel.removeGuideLine( guideLine );
  }

  public UndoEntry merge( final UndoEntry other ) {
    return null;
  }
}
