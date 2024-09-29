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


package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;

import javax.swing.*;
import java.awt.*;

public class LegacyChartReportElementInlineEditor extends AbstractCellEditor implements ReportElementInlineEditor {
  public LegacyChartReportElementInlineEditor() {
  }

  public Component getElementCellEditorComponent( final ReportElementEditorContext rootBandRenderComponent,
                                                  final ReportElement value ) {
    LegacyChartsUtil.performEdit( (Element) value, rootBandRenderComponent.getDesignerContext() );
    return null;
  }

  public Object getCellEditorValue() {
    return null;
  }
}
