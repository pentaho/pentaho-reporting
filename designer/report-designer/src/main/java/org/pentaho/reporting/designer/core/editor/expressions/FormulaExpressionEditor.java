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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorPanel;
import org.pentaho.openformula.ui.FunctionParameterEditor;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.designer.core.util.ReportDesignerFunctionParameterEditor;
import org.pentaho.reporting.designer.core.util.table.CellEditorUtility;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import java.util.Iterator;

public class FormulaExpressionEditor implements ExpressionEditor {
  private static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[ 0 ];

  private FormulaEditorPanel editorPanel;
  private ReportDocumentContext renderContext;
  private FormulaExpression formulaExpression;
  private FormulaFunction formulaFunction;

  public FormulaExpressionEditor() {
    this.editorPanel = new FormulaEditorPanel();
  }

  public void initialize( final Expression expression, final ReportDesignerContext designerContext ) {
    this.renderContext = designerContext.getActiveContext();

    if ( expression instanceof FormulaExpression ) {
      formulaExpression = (FormulaExpression) expression;
      editorPanel.setFormulaText( formulaExpression.getFormula() );
    } else if ( expression instanceof FormulaFunction ) {

      formulaFunction = (FormulaFunction) expression;
      editorPanel.setFormulaText( formulaFunction.getFormula() );
    }

    if ( StringUtils.isEmpty( editorPanel.getFormulaText() ) ) {
      editorPanel.setFormulaText( "=" );
    }

    editorPanel.setFields( getFields() );

    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator propertyKeys = configuration.findPropertyKeys( GUIUtils.FUNCTION_EDITOR_CONFIX_PREFIX );
    while ( propertyKeys.hasNext() ) {
      final String key = (String) propertyKeys.next();
      final String function = key.substring( GUIUtils.FUNCTION_EDITOR_CONFIX_PREFIX.length() );
      final String editor = configuration.getConfigProperty( key );
      final FunctionParameterEditor fnEditor =
        ObjectUtilities.loadAndInstantiate( editor, GUIUtils.class, FunctionParameterEditor.class );
      if ( fnEditor instanceof ReportDesignerFunctionParameterEditor ) {
        final ReportDesignerFunctionParameterEditor rfn = (ReportDesignerFunctionParameterEditor) fnEditor;
        rfn.setReportDesignerContext( designerContext );
      }
      this.editorPanel.setEditor( function, fnEditor );
    }
  }

  private ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  private FieldDefinition[] getFields() {
    return CellEditorUtility.getFields( getRenderContext(), new String[ 0 ] );
  }

  public JComponent getEditorComponent() {
    return editorPanel;
  }

  public void stopEditing() {
    String formulaText = editorPanel.getFormulaText();
    if ( StringUtils.isEmpty( formulaText, true ) || formulaText.trim().equals( "=" ) ) {
      formulaText = null;
    }

    if ( formulaExpression != null ) {
      formulaExpression.setFormula( formulaText );
    } else if ( formulaFunction != null ) {
      formulaFunction.setFormula( formulaText );
    }
  }

  public String getTitle() {
    return EditorExpressionsMessages.getString( "FormulaExpressionEditor.Formula" );
  }
}
