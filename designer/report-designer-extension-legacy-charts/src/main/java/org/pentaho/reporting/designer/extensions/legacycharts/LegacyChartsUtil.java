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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.plugin.jfreereport.reportcharts.AbstractChartExpression;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.AttributeExpressionEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LegacyChartsUtil {
  private LegacyChartsUtil() {
  }

  public static boolean isLegacyChartElement( final Element element ) {
    if ( element.getElementType() instanceof LegacyChartType ) {
      return true;
    }
    final Expression valueExpression =
      element.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    if ( valueExpression instanceof AbstractChartExpression ) {
      return true;
    }

    return false;
  }

  public static void performEdit( final Element chartElement,
                                  final ReportDesignerContext context ) {
    final ReportDocumentContext activeContext = context.getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final LegacyChartEditorDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new LegacyChartEditorDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new LegacyChartEditorDialog( (JFrame) window );
    } else {
      dialog = new LegacyChartEditorDialog();
    }

    try {
      final ChartEditingResult editResult = dialog.performEdit( chartElement, context );
      if ( editResult != null ) {
        final ArrayList<UndoEntry> undoEntries = new ArrayList<UndoEntry>();
        undoEntries.add( new AttributeExpressionEditUndoEntry( chartElement.getObjectID(),
          AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
          editResult.getOriginalChartExpression(), editResult.getChartExpression() ) );
        undoEntries.add( new AttributeEditUndoEntry( chartElement.getObjectID(),
          LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE,
          editResult.getOriginalPrimaryDataSource(), editResult.getPrimaryDataSource() ) );
        undoEntries.add( new AttributeEditUndoEntry( chartElement.getObjectID(),
          LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.SECONDARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE,
          editResult.getOriginalSecondaryDataSource(), editResult.getSecondaryDataSource() ) );

        final CompoundUndoEntry ue =
          new CompoundUndoEntry( undoEntries.toArray( new UndoEntry[ undoEntries.size() ] ) );
        activeContext.getUndo().addChange( Messages.getInstance().getString( "EditLegacyChartAction.Undo" ), ue );
        ue.redo( context.getActiveContext() );
      }
    } catch ( CloneNotSupportedException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }
}
