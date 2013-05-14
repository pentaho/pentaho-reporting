/*
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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport;

import java.util.ArrayList;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

public class RelationalReportBuilder
{
  public static class GroupDefinition
  {
    private String groupField;
    private boolean header;
    private boolean headerAggregation;
    private boolean footerAggregation;

    public GroupDefinition(final String groupField,
                           final boolean header,
                           final boolean headerAggregation,
                           final boolean footerAggregation)
    {
      this.groupField = groupField;
      this.header = header;
      this.headerAggregation = headerAggregation;
      this.footerAggregation = footerAggregation;
    }

    public String getGroupField()
    {
      return groupField;
    }

    public boolean isHeader()
    {
      return header;
    }

    public boolean isHeaderAggregation()
    {
      return headerAggregation;
    }

    public boolean isFooterAggregation()
    {
      return footerAggregation;
    }
  }

  public static class RelationalDetail
  {
    private String field;
    private Class aggregation;

    public RelationalDetail(final String fieldName)
    {
      this.field = fieldName;
      this.aggregation = ItemSumFunction.class;
    }

    public RelationalDetail(final String field, final Class aggregation)
    {
      this.field = field;
      this.aggregation = aggregation;
    }

    public String getField()
    {
      return field;
    }

    public void setField(final String field)
    {
      this.field = field;
    }

    public Class getAggregation()
    {
      return aggregation;
    }

    public void setAggregation(final Class aggregation)
    {
      this.aggregation = aggregation;
    }
  }

  private ArrayList<GroupDefinition> groups;
  private ArrayList<RelationalDetail> details;
  private DesignTimeDataSchemaModel dataSchemaModel;
  private String groupNamePrefix;

  public RelationalReportBuilder(final DesignTimeDataSchemaModel dataSchemaModel)
  {
    groups = new ArrayList<GroupDefinition>();
    details = new ArrayList<RelationalDetail>();
    this.dataSchemaModel = dataSchemaModel;
    this.groupNamePrefix = "";
  }

  public String getGroupNamePrefix()
  {
    return groupNamePrefix;
  }

  public void setGroupNamePrefix(final String groupNamePrefix)
  {
    this.groupNamePrefix = groupNamePrefix;
  }

  public void addGroup(final GroupDefinition g)
  {
    groups.add(g);
  }

  public void addGroup(final String field)
  {
    addGroup(field, true, true, true);
  }

  public void addGroup(final String field,
                       final boolean printHeader,
                       final boolean printHeaderAgg,
                       final boolean printFooterAgg)
  {
    groups.add(new GroupDefinition(field, printHeader, printHeaderAgg, printFooterAgg));
  }

  public void addDetails(final RelationalDetail detail)
  {
    details.add(detail);
  }

  public void addDetails(final String field, final Class aggregation)
  {
    details.add(new RelationalDetail(field, aggregation));
  }

  public MasterReport createReport()
  {
    final MasterReport report = new MasterReport();
    report.setRootGroup(create());
    return report;
  }

  public RelationalGroup create()
  {
    RelationalGroup rootGroup = null;
    RelationalGroup innerGroup = null;

    boolean headerPrinted = false ;

    if (groups.size() <= 0)
    {
      rootGroup = new RelationalGroup();
      innerGroup = rootGroup;
      headerPrinted = false;
    }
    else
    {
      for (int i = 0; i < groups.size(); i += 1)
      {
        headerPrinted |= groups.get(i).isHeader();
      }

      for (int i = 0; i < groups.size(); i += 1)
      {
        final GroupDefinition groupDefinition = groups.get(i);
        final RelationalGroup g = new RelationalGroup();
        g.addField(groupDefinition.getGroupField());
        configureGroupHeader(groupDefinition, g, headerPrinted);
        configureGroupFooter(groupDefinition, g, headerPrinted);
        if (rootGroup == null)
        {
          rootGroup = g;
        }
        if (innerGroup != null)
        {
          innerGroup.setBody(new SubGroupBody(g));
        }
        innerGroup = g;
      }
    }

    //noinspection ConstantConditions
    final GroupDataBody body = (GroupDataBody) innerGroup.getBody();
    final ItemBand band = body.getItemBand();
    band.setLayout("row");
    if (headerPrinted)
    {
      band.addElement(createLabel("Details"));
    }
    for (int d = 0; d < details.size(); d += 1)
    {
      final RelationalDetail relationalDetail = details.get(d);
      band.addElement(createFieldItem(relationalDetail.getField(), null));
    }
    return rootGroup;
  }

  private void configureGroupFooter(final GroupDefinition groupDefinition,
                                    final RelationalGroup g,
                                    final boolean headerPrinted)
  {
    final GroupFooter footer = g.getFooter();
    footer.setLayout("row");
    footer.setRepeat(true);
    if (groupDefinition.isHeader())
    {
      footer.addElement(createFieldItem(groupDefinition.getGroupField()));
    }
    else if (headerPrinted)
    {
      footer.addElement(createLabel("Footer"));
    }
    if (groupDefinition.isFooterAggregation())
    {
      for (int d = 0; d < details.size(); d += 1)
      {
        final RelationalDetail relationalDetail = details.get(d);
        footer.addElement(createFieldItem(relationalDetail.getField(), relationalDetail.getAggregation()));
      }
    }
  }

  private void configureGroupHeader(final GroupDefinition groupDefinition,
                                    final RelationalGroup g,
                                    final boolean headerPrinted)
  {
    final GroupHeader header = g.getHeader();
    header.setLayout("row");
    header.setRepeat(true);
    if (groupDefinition.isHeader())
    {
      header.addElement(createFieldItem(groupDefinition.getGroupField()));
    }
    else if (headerPrinted)
    {
      header.addElement(createLabel("Header"));
    }
    if (groupDefinition.isHeaderAggregation())
    {
      for (int d = 0; d < details.size(); d += 1)
      {
        final RelationalDetail relationalDetail = details.get(d);
        header.addElement(createFieldItem(relationalDetail.getField(), relationalDetail.getAggregation()));
      }
    }
  }

  private Element createFieldItem(final String text)
  {
    return createFieldItem(text, null);
  }

  private Element createFieldItem(final String fieldName,
                                  final Class aggregationType)
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
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 80f);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, aggregationType);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, Boolean.TRUE);
    return element;
  }

  private Element createLabel(final String text)
  {
    final Element element = new Element();
    element.setElementType(LabelType.INSTANCE);
    element.getElementType().configureDesignTimeDefaults(element, Locale.getDefault());

    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 80f);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);
    return element;
  }
}
