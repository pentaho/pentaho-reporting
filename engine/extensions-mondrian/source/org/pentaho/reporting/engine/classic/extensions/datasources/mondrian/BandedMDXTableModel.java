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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

import mondrian.olap.Axis;
import mondrian.olap.Cell;
import mondrian.olap.Dimension;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * This tablemodel performs some preprocessing to get multi-dimensional resultset (with row and column headers) into a
 * classical table-structure. The query must be a two-dimensional query or the whole process will break.
 * <p/>
 * This class exists for legacy reasons to provide existing reports the same view on MDX data as implemented in the
 * Pentaho-Platform and the Report-Designer. It can also be somewhat useful if you have a requirement to produce banded
 * reporting over a MDX data source.
 *
 * @author : Thomas Morgner
 */
@SuppressWarnings({"UnnecessaryUnboxing"})
public class BandedMDXTableModel extends AbstractTableModel
    implements CloseableTableModel, MetaTableModel
{
  private static final Log logger = LogFactory.getLog(DenormalizedMDXTableModel.class);

  private boolean noMeasures;
  private Result resultSet;
  private int rowCount;
  private int columnCount;
  private String[] columnNames;
  private int[] axesSize;
  private int[] columnToAxisPosition;
  private Dimension[] columnToDimensionMapping;

  public BandedMDXTableModel(final Result resultSet, final int rowLimit)
  {
    if (resultSet == null)
    {
      throw new NullPointerException("ResultSet returned was null");
    }
    this.resultSet = resultSet;

    // rowcount is the product of all axis-sizes. If an axis contains more than one member, then
    // Mondrian already performs the crossjoin for us.

    // column count is the sum of the maximum of all hierachy levels of all axis.

    final Axis[] axes = this.resultSet.getAxes();
    this.rowCount = 0;
    this.axesSize = new int[axes.length];
    final int[] axesMembers = new int[axes.length];
    final List<Dimension>[] dimensionsForMembersPerAxis = new List[axes.length];
    final List<Integer>[] membersPerAxis = new List[axes.length];

    // process the column axis first ..
    if (axesSize.length > 0)
    {
      final Axis axis = axes[0];
      final List<Position> positions = axis.getPositions();

      axesSize[0] = positions.size();
      if (positions.isEmpty())
      {
        noMeasures = true;
      }
    }

    // Axis contains (zero or more) positions, which contains (zero or more) members
    for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
    {
      final Axis axis = axes[axesIndex];
      final List<Position> positions = axis.getPositions();

      axesSize[axesIndex] = positions.size();
      if (positions.isEmpty())
      {
        noMeasures = true;
      }

      final ArrayList<Integer> memberList = new ArrayList<Integer>();
      final ArrayList<Dimension> dimensionsForMembers = new ArrayList<Dimension>();
      for (int positionsIndex = 0; positionsIndex < positions.size(); positionsIndex++)
      {
        final Position position = positions.get(positionsIndex);
        for (int positionIndex = 0; positionIndex < position.size(); positionIndex++)
        {
          final LinkedHashSet<String> columnNamesSet = new LinkedHashSet<String>();
          Member m = position.get(positionIndex);
          final Dimension dimension = m.getDimension();
          int hierarchyLevelCount = 0;
          while (m != null)
          {
            final String name = m.getLevel().getUniqueName();
            if (columnNamesSet.contains(name) == false)
            {
              columnNamesSet.add(name);
            }
            m = m.getParentMember();
          }

          hierarchyLevelCount = columnNamesSet.size();

          if (memberList.size() <= positionIndex)
          {
            memberList.add(hierarchyLevelCount);
            dimensionsForMembers.add(dimension);
          }
          else
          {
            final Integer existingLevel = memberList.get(positionIndex);
            if (existingLevel.intValue() < hierarchyLevelCount)
            {
              memberList.set(positionIndex, hierarchyLevelCount);
              dimensionsForMembers.set(positionIndex, dimension);
            }
          }
        }
      }

      int memberCount = 0;
      for (int i = 0; i < memberList.size(); i++)
      {
        memberCount += memberList.get(i);
      }
      axesMembers[axesIndex] = memberCount;
      dimensionsForMembersPerAxis[axesIndex] = dimensionsForMembers;
      membersPerAxis[axesIndex] = memberList;
    }

    if (axesSize.length > 1)
    {
      rowCount = axesSize[1];
      for (int i = 2; i < axesSize.length; i++)
      {
        final int size = axesSize[i];
        rowCount *= size;
      }
    }
    if (noMeasures == false)
    {
      rowCount = Math.max(1, rowCount);
    }
    if (axesSize.length == 0)
    {
      columnCount = 1;
    }
    else if (axesSize.length > 0)
    {
      columnCount = axesSize[0];
    }
    for (int i = 1; i < axesMembers.length; i++)
    {
      columnCount += axesMembers[i];
    }

    columnNames = new String[columnCount];
    columnToDimensionMapping = new Dimension[columnCount];
    columnToAxisPosition = new int[columnCount];

    int columnIndex = 0;
    int dimColIndex = 0;

    final FastStack memberStack = new FastStack();
    for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
    {
      final Axis axis = axes[axesIndex];
      final List<Position> positions = axis.getPositions();
      final LinkedHashSet<String> columnNamesSet = new LinkedHashSet<String>();
      for (int positionsIndex = 0; positionsIndex < positions.size(); positionsIndex++)
      {
        final Position position = positions.get(positionsIndex);
        for (int positionIndex = 0; positionIndex < position.size(); positionIndex++)
        {
          memberStack.clear();
          Member m = position.get(positionIndex);
          while (m != null)
          {
            memberStack.push(m);
            m = m.getParentMember();
          }

          while (memberStack.isEmpty() == false)
          {
            m = (Member) memberStack.pop();
            final String name = m.getLevel().getUniqueName();
            if (columnNamesSet.contains(name) == false)
            {
              columnNamesSet.add(name);
            }
          }
        }
      }

      if (columnNamesSet.size() != axesMembers[axesIndex])
      {
        logger.error("ERROR: Number of names is not equal the pre-counted number.");
      }

      final List<Dimension> dimForMemberPerAxis = dimensionsForMembersPerAxis[axesIndex];
      final List<Integer> memberCntPerAxis = membersPerAxis[axesIndex];
      for (int i = 0; i < memberCntPerAxis.size(); i++)
      {
        final Integer count = memberCntPerAxis.get(i);
        final Dimension dim = dimForMemberPerAxis.get(i);
        for (int x = 0; x < count.intValue(); x += 1)
        {
          this.columnToDimensionMapping[dimColIndex + x] = dim;
          this.columnToAxisPosition[dimColIndex + x] = axesIndex;
        }
        dimColIndex = count.intValue() + dimColIndex;
      }

      final String[] names = columnNamesSet.toArray(new String[columnNamesSet.size()]);
      System.arraycopy(names, 0, this.columnNames, columnIndex, names.length);
      columnIndex += names.length;
    }

    if (axesSize.length > 0)
    {
      // now create the column names for the column-axis
      final Axis axis = axes[0];
      final List<Position> positions = axis.getPositions();
      for (int i = 0; i < positions.size(); i++)
      {
        final Position position = positions.get(i);
        final StringBuffer positionName = new StringBuffer(100);
        for (int j = 0; j < position.size(); j++)
        {
          if (j != 0)
          {
            positionName.append('/');
          }
          final Member member = position.get(j);
          positionName.append(MondrianUtil.getUniqueMemberName(member));
        }
        columnNames[columnIndex] = positionName.toString();
        columnIndex += 1;
      }
    }
    if (axesSize.length == 0)
    {
      columnNames[0] = "Measure";
    }
    if (rowLimit > 0)
    {
      rowCount = Math.min(rowLimit, rowCount);
    }
  }

  public int getRowCount()
  {
    return rowCount;
  }

  public int getColumnCount()
  {
    return columnCount;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(final int column)
  {
    return columnNames[column];
  }

  public Object getValueAt(final int rowIndex,
                           final int columnIndex)
  {
    if (columnIndex >= columnNames.length)
    {
      throw new IndexOutOfBoundsException();
    }

    final int[] cellKey = computeCellKey(rowIndex, columnIndex);

    // user asked for a dimension ...
    final Dimension dimension = columnToDimensionMapping[columnIndex];
    if (dimension == null)
    {
      final Cell cell = resultSet.getCell(cellKey);
      if (cell.isNull())
      {
        return null;
      }
      return cell.getValue();
    }

    Set<Member> candidates = getCandidateMembers(dimension, columnIndex, cellKey);
    String name = null;
    for (Member candidate : candidates)
    {
      name = candidate.getParentMember() == null ? candidate.getName() : null;
      Member contextMember = searchContextMemberOfParents(candidate, columnIndex);
      if (contextMember != null)
      {
        return contextMember.getName();
      }
    }
    return name;
  }

  public Class<?> getColumnClass(final int columnIndex)
  {
    if (getRowCount() == 0)
    {
      return Object.class;
    }
    try
    {
      final Object targetClassObj = getValueAt(0, columnIndex);
      if (targetClassObj == null)
      {
        return Object.class;
      }
      else
      {
        return targetClassObj.getClass();
      }
    }
    catch (Exception e)
    {
      return Object.class;
    }
  }

  private int[] computeCellKey(final int rowIndex, final int columnIndex)
  {
    final int correctedColIndex;
    if (axesSize.length > 0)
    {
      final int startOfColumnIndex = columnCount - axesSize[0];
      if (columnIndex < startOfColumnIndex)
      {
        // this is a query for a axis-header
        correctedColIndex = -1;
      }
      else
      {
        correctedColIndex = columnIndex - startOfColumnIndex;
      }
    }
    else
    {
      correctedColIndex = 0;
    }

    final int[] cellKey = new int[axesSize.length];
    int tmpRowIdx = rowIndex;
    if (axesSize.length > 0)
    {
      cellKey[0] = correctedColIndex;
    }
    for (int i = 1; i < axesSize.length; i++)
    {
      final int axisSize = axesSize[i];
      if (axisSize == 0)
      {
        cellKey[i] = 0;
      }
      else
      {
        final int pos = tmpRowIdx % axisSize;
        cellKey[i] = pos;
        tmpRowIdx = tmpRowIdx / axisSize;
      }
    }
    return cellKey;
  }

  private Set<Member> getCandidateMembers(final Dimension dimension,
                                          final int columnIndex,
                                          final int[] cellKey)
  {
    Set<Member> resultMembers = new LinkedHashSet<Member>();

    final int axisIndex = columnToAxisPosition[columnIndex];
    final Axis[] axes = resultSet.getAxes();
    final Axis axis = axes[axisIndex];
    final int posIndex = cellKey[axisIndex];
    final List<Position> positionList = axis.getPositions();
    if (positionList.isEmpty())
    {
      return resultMembers;
    }

    final Position position = positionList.get(posIndex);
    for (int i = 0; i < position.size(); i++)
    {
      final Member member = position.get(i);
      if (dimension.equals(member.getDimension()))
      {
        resultMembers.add(member);
      }
    }
    return resultMembers;
  }

  public void close()
  {
    resultSet.close();
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Meta-data models that only describe meta-data for columns can ignore the row-parameter.
   *
   * @param rowIndex    the row of the cell for which the meta-data is queried.
   * @param columnIndex the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes(final int rowIndex, final int columnIndex)
  {
    if (columnIndex >= columnNames.length)
    {
      throw new IndexOutOfBoundsException();
    }

    final int[] cellKey = computeCellKey(rowIndex, columnIndex);

    // user asked for a dimension ...
    final Dimension dimension = columnToDimensionMapping[columnIndex];
    if (dimension == null)
    {
      final Cell cell = resultSet.getCell(cellKey);
      return new MDXMetaDataCellAttributes(EmptyDataAttributes.INSTANCE, cell);
    }

    Set<Member> candidates = getCandidateMembers(dimension, columnIndex, cellKey);
    for (Member candidate : candidates)
    {
      Member contextMember = searchContextMemberOfParents(candidate, columnIndex);
      if (contextMember != null)
      {
        return new MDXMetaDataMemberAttributes(EmptyDataAttributes.INSTANCE, contextMember);
      }
    }
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported()
  {
    return true;
  }

  public DataAttributes getColumnAttributes(final int column)
  {
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table attributes.
   */
  public DataAttributes getTableAttributes()
  {
    final DefaultDataAttributes dataAttributes = new DefaultDataAttributes();
    dataAttributes.setMetaAttribute(MetaAttributeNames.Core.NAMESPACE,
        MetaAttributeNames.Core.CROSSTAB_MODE, DefaultConceptQueryMapper.INSTANCE, MetaAttributeNames.Core.CROSSTAB_VALUE_NORMALIZED);
    return dataAttributes;
  }

  private Member searchContextMemberOfParents(final Member member, final int columnIndex)
  {
    Member candidate = member;
    while (candidate != null)
    {
      if (candidate.getLevel().getUniqueName().equals(getColumnName(columnIndex)))
      {
        return candidate;
      }
      candidate = candidate.getParentMember();
    }
    return null;
  }

}
