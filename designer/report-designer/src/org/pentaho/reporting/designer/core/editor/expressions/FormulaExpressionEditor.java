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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorPanel;
import org.pentaho.openformula.ui.FunctionParameterEditor;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.designer.core.util.DataSchemaFieldDefinition;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.designer.core.util.ReportDesignerFunctionParameterEditor;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class FormulaExpressionEditor implements ExpressionEditor
{
  private static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[0];

  private FormulaEditorPanel editorPanel;
  private ReportDocumentContext renderContext;
  private DefaultDataAttributeContext dataAttributeContext;
  private FormulaExpression formulaExpression;
  private FormulaFunction formulaFunction;

  public FormulaExpressionEditor()
  {
    this.dataAttributeContext = new DefaultDataAttributeContext();
    this.editorPanel = new FormulaEditorPanel();
  }

  public void initialize(final Expression expression, final ReportDesignerContext designerContext)
  {
    this.renderContext = designerContext.getActiveContext();

    if (expression instanceof FormulaExpression)
    {
      formulaExpression = (FormulaExpression) expression;
      editorPanel.setFormulaText(formulaExpression.getFormula());
    }
    else if (expression instanceof FormulaFunction)
    {

      formulaFunction = (FormulaFunction) expression;
      editorPanel.setFormulaText(formulaFunction.getFormula());
    }

    if (StringUtils.isEmpty(editorPanel.getFormulaText()))
    {
      editorPanel.setFormulaText("=");
    }

    editorPanel.setFields(getFields());

    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator propertyKeys = configuration.findPropertyKeys(GUIUtils.FUNCTION_EDITOR_CONFIX_PREFIX);
    while (propertyKeys.hasNext())
    {
      final String key = (String) propertyKeys.next();
      final String function = key.substring(GUIUtils.FUNCTION_EDITOR_CONFIX_PREFIX.length());
      final String editor = configuration.getConfigProperty(key);
      final FunctionParameterEditor fnEditor =
          ObjectUtilities.loadAndInstantiate(editor, GUIUtils.class, FunctionParameterEditor.class);
      if (fnEditor instanceof ReportDesignerFunctionParameterEditor)
      {
        final ReportDesignerFunctionParameterEditor rfn = (ReportDesignerFunctionParameterEditor) fnEditor;
        rfn.setReportDesignerContext(designerContext);
      }
      this.editorPanel.setEditor(function, fnEditor);
    }
  }

  private ReportDocumentContext getRenderContext()
  {
    return renderContext;
  }

  private FieldDefinition[] getFields()
  {
    final ReportDocumentContext renderContext = getRenderContext();
    if (renderContext == null)
    {
      return EMPTY_FIELDS;
    }

    final ReportDataSchemaModel model = renderContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>(columnNames.length);
    final DataSchema dataSchema = model.getDataSchema();
    for (int i = 0; i < columnNames.length; i++)
    {
      final String columnName = columnNames[i];
      final DataAttributes attributes = dataSchema.getAttributes(columnName);
      if (attributes == null)
      {
        throw new IllegalStateException("No data-schema for expression with name '" + columnName + '\'');
      }
      if (ReportDataSchemaModel.isFiltered(attributes, dataAttributeContext))
      {
        continue;
      }

      fields.add(new DataSchemaFieldDefinition(columnName, attributes, dataAttributeContext));
    }
    return fields.toArray(new FieldDefinition[fields.size()]);
  }

  public JComponent getEditorComponent()
  {
    return editorPanel;
  }

  public void stopEditing()
  {
    String formulaText = editorPanel.getFormulaText();
    if (StringUtils.isEmpty(formulaText, true) || formulaText.trim().equals("="))
    {
      formulaText = null;
    }

    if (formulaExpression != null)
    {
      formulaExpression.setFormula(formulaText);
    }
    else if (formulaFunction != null)
    {
      formulaFunction.setFormula(formulaText);
    }
  }

  public String getTitle()
  {
    return EditorExpressionsMessages.getString("FormulaExpressionEditor.Formula");
  }
}
