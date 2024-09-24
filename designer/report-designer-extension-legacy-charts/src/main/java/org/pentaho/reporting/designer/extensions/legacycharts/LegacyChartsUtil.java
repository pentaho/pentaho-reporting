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
