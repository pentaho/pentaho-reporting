/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
