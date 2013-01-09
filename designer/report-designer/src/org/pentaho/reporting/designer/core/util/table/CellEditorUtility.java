package org.pentaho.reporting.designer.core.util.table;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.designer.core.util.DataSchemaFieldDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;

public class CellEditorUtility
{
  private CellEditorUtility()
  {
  }


  public static String[] getExcelColorsAsText()
  {
    final Color[] excelColors = ColorUtility.getPredefinedExcelColors();
    final String[] textColors = new String[excelColors.length];
    for (int i = 0; i < excelColors.length; i++)
    {
      final Color excelColor = excelColors[i];
      final String color = Integer.toHexString(excelColor.getRGB() & 0x00ffffff);
      final StringBuffer retval = new StringBuffer(7);
      retval.append('#');
      final int fillUp = 6 - color.length();
      for (int x = 0; x < fillUp; x++)
      {
        retval.append('0');
      }
      retval.append(color);
      textColors[i] = retval.toString();
    }
    return textColors;
  }

  public static String[] getQueryNames(final ReportDesignerContext designerContext)
  {
    if (designerContext == null)
    {
      return new String[0];
    }

    final ReportRenderContext reportContext = designerContext.getActiveContext();
    if (reportContext == null)
    {
      return new String[0];
    }

    AbstractReportDefinition definition = reportContext.getReportDefinition();
    final LinkedHashSet<String> names = new LinkedHashSet<String>();
    while (definition != null)
    {
      final CompoundDataFactory dataFactoryElement = (CompoundDataFactory) definition.getDataFactory();
      final int dataFactoryCount = dataFactoryElement.size();
      for (int i = 0; i < dataFactoryCount; i++)
      {
        final DataFactory dataFactory = dataFactoryElement.getReference(i);
        final String[] queryNames = dataFactory.getQueryNames();
        names.addAll(Arrays.asList(queryNames));
      }
      if (definition instanceof SubReport)
      {
        final Section parentSection = definition.getParentSection();
        definition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
      else
      {
        definition = null;
      }
    }
    return names.toArray(new String[names.size()]);
  }


  public static FieldDefinition[] getFields(final ReportDesignerContext designerContext,
                                            final String[] extraFields)
  {
    if (designerContext == null)
    {
      return new FieldDefinition[0];
    }

    final ReportRenderContext reportContext = designerContext.getActiveContext();
    if (reportContext == null)
    {
      return new FieldDefinition[0];
    }

    final ReportDataSchemaModel model = reportContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>(columnNames.length + extraFields.length);
    final DataSchema dataSchema = model.getDataSchema();
    final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();

    for (int i = 0; i < extraFields.length; i++)
    {
      final String extraField = extraFields[i];
      fields.add(new DataSchemaFieldDefinition(extraField, new EmptyDataAttributes(), dataAttributeContext));
    }

    for (int i = columnNames.length - 1; i >= 0; i -= 1)
    {
      final String columnName = columnNames[i];
      final DataAttributes attributes = dataSchema.getAttributes(columnName);
      if (attributes == null)
      {
        throw new IllegalStateException("No data-schema for field with name '" + columnName + '\'');
      }
      if (ReportDataSchemaModel.isFiltered(attributes, dataAttributeContext))
      {
        continue;
      }
      fields.add(new DataSchemaFieldDefinition(columnName, attributes, dataAttributeContext));
    }

    return fields.toArray(new FieldDefinition[fields.size()]);
  }


  public static String[] getFieldsAsString(final ReportDesignerContext designerContext,
                                            final String[] extraFields)
  {
    if (designerContext == null)
    {
      return extraFields.clone();
    }

    final ReportRenderContext reportContext = designerContext.getActiveContext();
    if (reportContext == null)
    {
      return extraFields.clone();
    }

    final ReportDataSchemaModel model = reportContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<String> fields = new ArrayList<String>(columnNames.length + extraFields.length);
    final DataSchema dataSchema = model.getDataSchema();
    final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();

    for (int i = 0; i < extraFields.length; i++)
    {
      final String extraField = extraFields[i];
      fields.add(extraField);
    }

    for (int i = columnNames.length - 1; i >= 0; i -= 1)
    {
      final String columnName = columnNames[i];
      final DataAttributes attributes = dataSchema.getAttributes(columnName);
      if (attributes == null)
      {
        throw new IllegalStateException("No data-schema for field with name '" + columnName + '\'');
      }
      if (ReportDataSchemaModel.isFiltered(attributes, dataAttributeContext))
      {
        continue;
      }
      fields.add(columnName);
    }

    return fields.toArray(new String[fields.size()]);
  }

  public static String[] getGroups(final ReportDesignerContext designerContext)
  {
    if (designerContext == null)
    {
      return new String[0];
    }

    final ReportRenderContext reportContext = designerContext.getActiveContext();
    if (reportContext == null)
    {
      return new String[0];
    }

    return ModelUtility.getGroups(reportContext.getReportDefinition());
  }
}
