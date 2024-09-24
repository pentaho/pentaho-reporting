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

package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.extensions.toc.IndexElement;

import javax.swing.*;
import java.awt.*;

public class IndexReportElementInlineEditor extends AbstractCellEditor implements ReportElementInlineEditor {
  public IndexReportElementInlineEditor() {
  }

  public Component getElementCellEditorComponent( final ReportElementEditorContext rootBandRenderComponent,
                                                  final ReportElement value ) {
    final ReportDesignerContext context = rootBandRenderComponent.getDesignerContext();
    final int contextCount = context.getReportRenderContextCount();
    for ( int i = 0; i < contextCount; i++ ) {
      final ReportRenderContext rrc = context.getReportRenderContext( i );
      if ( rrc.getReportDefinition() == value ) {
        context.setActiveDocument( rrc );
        return null;
      }
    }

    final IndexElement report = (IndexElement) value;
    try {
      context.addSubReport( rootBandRenderComponent.getRenderContext(), report );
    } catch ( ReportDataFactoryException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }

    return null;
  }

  public Object getCellEditorValue() {
    return null;
  }
}
