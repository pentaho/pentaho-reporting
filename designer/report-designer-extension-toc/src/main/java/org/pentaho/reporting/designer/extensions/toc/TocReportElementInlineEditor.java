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

package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElement;

import javax.swing.*;
import java.awt.*;

public class TocReportElementInlineEditor extends AbstractCellEditor implements ReportElementInlineEditor {
  public TocReportElementInlineEditor() {
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

    final TocElement report = (TocElement) value;
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

