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


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.actions.elements.EditContentRefAction;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.engine.classic.core.ReportElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document me!
 * <p/>
 * Date: 06.05.2009 Time: 10:40:13
 *
 * @author Thomas Morgner.
 */
public class ContentReportElementInlineEditor extends AbstractCellEditor implements ReportElementInlineEditor {
  public ContentReportElementInlineEditor() {
  }

  public Component getElementCellEditorComponent( final ReportElementEditorContext rootBandRenderComponent,
                                                  final ReportElement value ) {
    EditContentRefAction action = new EditContentRefAction();
    action.setReportDesignerContext( rootBandRenderComponent.getDesignerContext() );
    action.actionPerformed( new ActionEvent( this, 0, null ) );
    return null;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    return null;
  }
}
