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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.util.ArrayList;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class CrosstabBuilder
{
  private ArrayList<CrosstabDimension> rows;
  private ArrayList<CrosstabDimension> columns;
  private ArrayList<String> others;
  private ArrayList<CrosstabDetail> details;
  private ContextAwareDataSchemaModel dataSchemaModel;
  private String groupNamePrefix;
  private Float minimumWidth;
  private Float minimumHeight;
  private Float maximumWidth;
  private Float maximumHeight;
  private Float prefWidth;
  private Float prefHeight;
  private Boolean allowMetaDataStyling;
  private Boolean allowMetaDataAttributes;

  @Deprecated
  public CrosstabBuilder(final DesignTimeDataSchemaModel dataSchemaModel)
  {
    this((ContextAwareDataSchemaModel) dataSchemaModel);
  }

  public CrosstabBuilder(final ContextAwareDataSchemaModel dataSchemaModel)
  {
    rows = new ArrayList<CrosstabDimension>();
    columns = new ArrayList<CrosstabDimension>();
    others = new ArrayList<String>();
    details = new ArrayList<CrosstabDetail>();
    this.dataSchemaModel = dataSchemaModel;
    this.groupNamePrefix = "";
    this.minimumHeight = 20f;
    this.maximumHeight = 20f;
    this.maximumWidth = 80f;
    this.minimumWidth = 80f;
  }

  public Float getMinimumWidth()
  {
    return minimumWidth;
  }

  public void setMinimumWidth(final Float minimumWidth)
  {
    this.minimumWidth = minimumWidth;
  }

  public Float getMinimumHeight()
  {
    return minimumHeight;
  }

  public void setMinimumHeight(final Float minimumHeight)
  {
    this.minimumHeight = minimumHeight;
  }

  public Float getMaximumWidth()
  {
    return maximumWidth;
  }

  public void setMaximumWidth(final Float maximumWidth)
  {
    this.maximumWidth = maximumWidth;
  }

  public Float getMaximumHeight()
  {
    return maximumHeight;
  }

  public void setMaximumHeight(final Float maximumHeight)
  {
    this.maximumHeight = maximumHeight;
  }

  public Float getPrefWidth()
  {
    return prefWidth;
  }

  public void setPrefWidth(final Float prefWidth)
  {
    this.prefWidth = prefWidth;
  }

  public Float getPrefHeight()
  {
    return prefHeight;
  }

  public void setPrefHeight(final Float prefHeight)
  {
    this.prefHeight = prefHeight;
  }

  public Boolean getAllowMetaDataStyling()
  {
    return allowMetaDataStyling;
  }

  public void setAllowMetaDataStyling(final Boolean allowMetaDataStyling)
  {
    this.allowMetaDataStyling = allowMetaDataStyling;
  }

  public Boolean getAllowMetaDataAttributes()
  {
    return allowMetaDataAttributes;
  }

  public void setAllowMetaDataAttributes(final Boolean allowMetaDataAttributes)
  {
    this.allowMetaDataAttributes = allowMetaDataAttributes;
  }

  public String getGroupNamePrefix()
  {
    return groupNamePrefix;
  }

  public void setGroupNamePrefix(final String groupNamePrefix)
  {
    this.groupNamePrefix = groupNamePrefix;
  }

  public void addOtherDimension(final String field)
  {
    others.add(field);
  }

  public void addRowDimension(final CrosstabDimension dimension)
  {
    rows.add(dimension);
  }

  public void addRowDimension(final String field)
  {
    addRowDimension(new CrosstabDimension(field, field, false, "Summary"));
  }

  public void addRowDimension(final String field, final boolean addSummary)
  {
    addRowDimension(new CrosstabDimension(field, field, addSummary, "Summary"));
  }

  public void addColumnDimension(final CrosstabDimension dimension)
  {
    columns.add(dimension);
  }

  public void addColumnDimension(final String field)
  {
    addColumnDimension(new CrosstabDimension(field, field, false, "Summary"));
  }

  public void addColumnDimension(final String field, final boolean addSummary)
  {
    addColumnDimension(new CrosstabDimension(field, field, addSummary, "Summary"));
  }

  public void addDetails(final CrosstabDetail detail)
  {
    details.add(detail);
  }

  public void addDetails(final String field, final Class aggregation)
  {
    details.add(new CrosstabDetail(field, field, aggregation));
  }

  public MasterReport createReport()
  {
    final MasterReport report = new MasterReport();
    report.setRootGroup(create());
    return report;
  }

  public CrosstabGroup create()
  {
    if (columns.size() == 0)
    {
      throw new IllegalStateException();
    }
    if (rows.size() == 0)
    {
      throw new IllegalStateException();
    }

    final CrosstabCellBody cellBody = new CrosstabCellBody();
    cellBody.addElement(createDetailsCell());
    setupDetailsHeader(cellBody.getHeader());

    GroupBody body = createColumnGroups(cellBody);
    body = createRowGroups(cellBody, body);
    body = createOtherGroups(body);

    return new CrosstabGroup(body);
  }

  private GroupBody createOtherGroups(GroupBody body)
  {
    for (int other = others.size() - 1; other >= 0; other -= 1)
    {
      final String column = others.get(other);
      final CrosstabOtherGroup columnGroup = new CrosstabOtherGroup(body);
      columnGroup.setField(column);
      columnGroup.getHeader().addElement(createFieldItem(column));

      body = new CrosstabOtherGroupBody(columnGroup);
    }
    return body;
  }

  private GroupBody createRowGroups(final CrosstabCellBody cellBody, GroupBody body)
  {
    for (int row = rows.size() - 1; row >= 0; row -= 1)
    {
      final CrosstabDimension rowDimension = rows.get(row);
      final CrosstabRowGroup rowGroup = new CrosstabRowGroup(body);
      rowGroup.setName(groupNamePrefix + rowDimension.getField());
      rowGroup.setField(rowDimension.getField());
      rowGroup.getTitleHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getTitleHeader().addElement(createLabel(rowDimension.getTitle(), rowDimension.getField()));
      rowGroup.getHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getHeader().addElement(createFieldItem(rowDimension.getField()));
      rowGroup.getSummaryHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getSummaryHeader().addElement(createLabel(rowDimension.getSummaryTitle(), null));
      rowGroup.setPrintSummary(rowDimension.isPrintSummary());

      if (rowDimension.isPrintSummary())
      {
        final CrosstabCell cell = createDetailsCell();
        cell.setRowField(rowDimension.getField());
        cell.setName(rowDimension.getField());
        cellBody.addElement(cell);

        for (int col = columns.size() - 1; col >= 0; col -= 1)
        {
          final CrosstabDimension column = columns.get(col);
          if (column.isPrintSummary())
          {
            final CrosstabCell crosstabCell = createDetailsCell();
            crosstabCell.setColumnField(column.getField());
            crosstabCell.setRowField(rowDimension.getField());
            crosstabCell.setName(column.getField() + "," + rowGroup.getField());
            cellBody.addElement(crosstabCell);
          }
        }
      }
      body = new CrosstabRowGroupBody(rowGroup);
    }
    return body;
  }

  private GroupBody createColumnGroups(final CrosstabCellBody cellBody)
  {
    GroupBody body = cellBody;
    for (int col = columns.size() - 1; col >= 0; col -= 1)
    {
      final CrosstabDimension column = columns.get(col);
      final CrosstabColumnGroup columnGroup = new CrosstabColumnGroup(body);
      columnGroup.setName(groupNamePrefix + column.getField());
      columnGroup.setField(column.getField());
      columnGroup.getTitleHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getTitleHeader().addElement(createLabel(column.getTitle(), column.getField()));
      columnGroup.getHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getHeader().addElement(createFieldItem(column.getField()));
      columnGroup.getSummaryHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getSummaryHeader().addElement(createLabel(column.getSummaryTitle(), null));
      columnGroup.setPrintSummary(column.isPrintSummary());

      if (column.isPrintSummary())
      {
        final CrosstabCell cell = createDetailsCell();
        cell.setColumnField(column.getField());
        cell.setName(column.getField());
        cellBody.addElement(cell);
      }
      body = new CrosstabColumnGroupBody(columnGroup);
    }
    return body;
  }

  private CrosstabCell createDetailsCell()
  {
    final CrosstabCell cell = new CrosstabCell();
    cell.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
    cell.getStyle().setStyleProperty(BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW);
    for (int i = 0; i < details.size(); i += 1)
    {
      final CrosstabDetail crosstabDetail = details.get(i);
      cell.addElement(createFieldItem(crosstabDetail.getField(), crosstabDetail.getAggregation(), true));
    }
    return cell;
  }

  private void setupDetailsHeader(final DetailsHeader cell)
  {
    cell.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
    cell.getStyle().setStyleProperty(BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW);
    for (int i = 0; i < details.size(); i += 1)
    {
      final CrosstabDetail crosstabDetail = details.get(i);
      String title = crosstabDetail.getTitle();
      if (StringUtils.isEmpty(title))
      {
        title = crosstabDetail.getField();
      }
      cell.addElement(createLabel(title, crosstabDetail.getField(), true));
    }
  }

  private Element createFieldItem(final String text)
  {
    return createFieldItem(text, null, false);
  }

  private Element createFieldItem(final String fieldName,
                                  final Class aggregationType,
                                  final boolean split)
  {
    final ElementType targetType;
    if (dataSchemaModel != null)
    {
      final DataAttributeContext context = dataSchemaModel.getDataAttributeContext();
      final DataAttributes attributes = dataSchemaModel.getDataSchema().getAttributes(fieldName);
      targetType = AutoGeneratorUtility.createFieldType(attributes, context);
    }
    else
    {
      targetType = TextFieldType.INSTANCE;
    }

    final Element element = new Element();
    element.setElementType(targetType);
    element.getElementType().configureDesignTimeDefaults(element, Locale.getDefault());

    if (targetType instanceof NumberFieldType)
    {
      element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, "0.00;-0.00");
    }

    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, split(split, minimumWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, minimumHeight);
    element.getStyle().setStyleProperty(ElementStyleKeys.WIDTH, split(split, prefWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.HEIGHT, prefHeight);
    element.getStyle().setStyleProperty(ElementStyleKeys.MAX_WIDTH, split(split, maximumWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.MAX_HEIGHT, maximumHeight);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, aggregationType);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, allowMetaDataStyling);
    return element;
  }

  private Element createLabel(final String text, final String labelFor)
  {
    return createLabel(text, labelFor, false);
  }

  private Element createLabel(final String text, final String labelFor, final boolean splitArea)
  {
    final Element element = new Element();
    element.setElementType(LabelType.INSTANCE);
    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, split(splitArea, minimumWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, minimumHeight);
    element.getStyle().setStyleProperty(ElementStyleKeys.WIDTH, split(splitArea, prefWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.HEIGHT, prefHeight);
    element.getStyle().setStyleProperty(ElementStyleKeys.MAX_WIDTH, split(splitArea, maximumWidth));
    element.getStyle().setStyleProperty(ElementStyleKeys.MAX_HEIGHT, maximumHeight);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, allowMetaDataStyling);
    if (StringUtils.isEmpty(labelFor))
    {
      element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, allowMetaDataAttributes);
    }
    else
    {
      element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, true);
    }
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, labelFor);
    return element;
  }

  private Float split(final boolean split, final Float value)
  {
    if (split == false)
    {
      return value;
    }

    if (value == null)
    {
      return null;
    }
    final float f = value;
    return f / Math.max(1, details.size());
  }
}
