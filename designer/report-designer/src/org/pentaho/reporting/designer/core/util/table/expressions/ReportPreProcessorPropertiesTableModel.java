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

package org.pentaho.reporting.designer.core.util.table.expressions;

import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class ReportPreProcessorPropertiesTableModel
    extends AbstractTableModel implements ElementMetaDataTableModel, GroupingModel
{
  private static final Log logger = LogFactory.getLog(ReportPreProcessorPropertiesTableModel.class);

  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[0];
  private static final ReportPreProcessorPropertyMetaData[] EMPTY_METADATA = new ReportPreProcessorPropertyMetaData[0];

  private ReportPreProcessorPropertyMetaData[] metaData;
  private GroupingHeader[] groupings;
  private TableStyle tableStyle;
  private ReportPreProcessor elements;
  private BeanUtility editors;

  public ReportPreProcessorPropertiesTableModel()
  {
    tableStyle = TableStyle.GROUPED;
    this.elements = null;
    this.metaData = EMPTY_METADATA;
    this.groupings = EMPTY_GROUPINGS;
    this.editors = null;
  }

  public int getRowCount()
  {
    return metaData.length;
  }

  protected ReportPreProcessorPropertyMetaData getMetaData(final int row)
  {
    //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
    return metaData[row];
  }

  protected GroupingHeader getGroupings(final int row)
  {
    //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
    return groupings[row];
  }

  public TableStyle getTableStyle()
  {
    return tableStyle;
  }

  public void setTableStyle(final TableStyle tableStyle)
  {
    if (tableStyle == null)
    {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    try
    {
      updateData(getData());
    }
    catch (IntrospectionException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      try
      {
        updateData(null);
      }
      catch (IntrospectionException e1)
      {
        // now this cannot happen ..
        UncaughtExceptionsModel.getInstance().addException(e);
      }
    }
  }

  /** @noinspection unchecked*/
  protected void updateData(final ReportPreProcessor elements) throws IntrospectionException
  {
    this.elements = elements;
    if (elements == null)
    {
      this.editors = null;
    }
    else
    {
      this.editors = new BeanUtility(elements);
    }

    final ReportPreProcessorPropertyMetaData[] metaData = selectCommonAttributes();
    if (tableStyle == TableStyle.ASCENDING)
    {
      Arrays.sort(metaData, new PlainMetaDataComparator());
      this.groupings = new GroupingHeader[metaData.length];
      this.metaData = metaData;
    }
    else if (tableStyle == TableStyle.DESCENDING)
    {
      Arrays.sort(metaData, Collections.reverseOrder(new PlainMetaDataComparator()));
      this.groupings = new GroupingHeader[metaData.length];
      this.metaData = metaData;
    }
    else
    {
      Arrays.sort(metaData, new GroupedMetaDataComparator());

      int groupCount = 0;
      final Locale locale = Locale.getDefault();
      if (metaData.length > 0)
      {
        String oldValue = null;

        for (int i = 0; i < metaData.length; i++)
        {
          if (groupCount == 0)
          {
            groupCount = 1;
            final ReportPreProcessorPropertyMetaData firstdata = metaData[i];
            oldValue = firstdata.getGrouping(locale);
            continue;
          }

          final ReportPreProcessorPropertyMetaData data = metaData[i];
          final String grouping = data.getGrouping(locale);
          if ((ObjectUtilities.equal(oldValue, grouping)) == false)
          {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final ReportPreProcessorPropertyMetaData[] groupedMetaData = new ReportPreProcessorPropertyMetaData[metaData.length + groupCount];
      this.groupings = new GroupingHeader[groupedMetaData.length];
      int targetIdx = 0;
      GroupingHeader group = null;
      for (int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++)
      {
        final ReportPreProcessorPropertyMetaData data = metaData[sourceIdx];
        if (sourceIdx == 0)
        {
          group = new GroupingHeader(data.getGrouping(locale));
          groupings[targetIdx] = group;
          targetIdx += 1;
        }
        else
        {
          final String newgroup = data.getGrouping(locale);
          if ((ObjectUtilities.equal(newgroup, group.getHeaderText())) == false)
          {
            group = new GroupingHeader(newgroup);
            groupings[targetIdx] = group;
            targetIdx += 1;
          }
        }

        groupings[targetIdx] = group;
        groupedMetaData[targetIdx] = data;
        targetIdx += 1;
      }

      this.metaData = groupedMetaData;
    }

    fireTableDataChanged();
  }

  private ReportPreProcessorPropertyMetaData[] selectCommonAttributes()
  {
    if (elements == null)
    {
      return EMPTY_METADATA;
    }
    final String name = elements.getClass().getName();
    if (ReportPreProcessorRegistry.getInstance().isReportPreProcessorRegistered(name) == false)
    {
      return EMPTY_METADATA;
    }

    final ReportPreProcessorMetaData preProcessorMetaData =
        ReportPreProcessorRegistry.getInstance().getReportPreProcessorMetaData(name);
    return preProcessorMetaData.getPropertyDescriptions();
  }


  public void setData(final ReportPreProcessor elements)
  {
    try
    {
      updateData(elements);
    }
    catch (Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      try
      {
        updateData(null);
      }
      catch (IntrospectionException e1)
      {
        // this time it will not happen.
      }
    }
  }

  public ReportPreProcessor getData()
  {
    return elements;
  }

  public int getColumnCount()
  {
    return 2;
  }

  public String getColumnName(final int column)
  {
    switch (column)
    {
      case 0:
        return UtilMessages.getInstance().getString("ReportPreProcessorPropertiesTableModel.NameColumn");
      case 1:
        return UtilMessages.getInstance().getString("ReportPreProcessorPropertiesTableModel.ValueColumn");
      default:
        throw new IllegalArgumentException();
    }
  }

  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return getGroupings(rowIndex);
    }

    switch (columnIndex)
    {
      case 0:
        return metaData.getDisplayName(Locale.getDefault());
      case 1:
        return computeFullValue(metaData);
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return false;
    }

    switch (columnIndex)
    {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return;
    }

    switch (columnIndex)
    {
      case 0:
        return;
      case 1:
      {
        if (defineFullValue(metaData, aValue))
        {
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  private boolean defineFullValue(final ReportPreProcessorPropertyMetaData metaData,
                                  final Object value)
  {
    boolean changed = false;
    try
    {
      final BeanUtility element = editors;
      final Object attribute = element.getProperty(metaData.getName());
      if ((ObjectUtilities.equal(attribute, value)) == false)
      {
        changed = true;
      }

      if (changed)
      {
        final String name = metaData.getName();
        element.setProperty(name, value);
      }
    }
    catch (BeanException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
    }

    return changed;
  }

  private Object computeFullValue(final ReportPreProcessorPropertyMetaData metaData)
  {
    if (elements == null)
    {
      return null;
    }
    try
    {
      final BeanUtility element = this.editors;
      return element.getProperty(metaData.getName());
    }
    catch (BeanException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      return null;
    }
  }

  public Class getClassForCell(final int rowIndex, final int columnIndex)
  {
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return GroupingHeader.class;
    }

    switch (columnIndex)
    {
      case 0:
        return String.class;
      case 1:
        return metaData.getPropertyType();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public PropertyEditor getEditorForCell(final int aRowIndex, final int aColumnIndex)
  {
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(aRowIndex);
    if (metaData == null)
    {
      // a header row
      return null;
    }

    try
    {
      switch (aColumnIndex)
      {
        case 0:
          return null;
        case 1:
          final PropertyEditor editor = metaData.getEditor();
          if (editor != null)
          {
            return editor;
          }

          final Class editorClass = metaData.getBeanDescriptor().getPropertyEditorClass();
          if (editorClass != null)
          {
            return (PropertyEditor) editorClass.newInstance();
          }
          
          if (String.class.equals(metaData.getPropertyType()))
          {
            return null;
          }

          return FastPropertyEditorManager.findEditor(metaData.getPropertyType());
        default:
          throw new IndexOutOfBoundsException();
      }
    }
    catch (Exception e)
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("Failed to create property-editor", e); // NON-NLS
      }
      return null;
    }
  }

  public String getValueRole(final int row, final int column)
  {
    if (column != 1)
    {
      return null;
    }
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(row);
    if (metaData == null)
    {
      return null;
    }
    return metaData.getPropertyRole();
  }

  public String[] getExtraFields(final int row, final int column)
  {
    if (column == 0)
    {
      return null;
    }
    final ReportPreProcessorPropertyMetaData metaData = getMetaData(row);
    if (metaData == null)
    {
      return null;
    }
    return metaData.getExtraCalculationFields();
  }

  public GroupingHeader getGroupHeader(final int index)
  {
    return getGroupings(index);
  }

  public boolean isHeaderRow(final int index)
  {
    return metaData[index] == null;
  }
}
