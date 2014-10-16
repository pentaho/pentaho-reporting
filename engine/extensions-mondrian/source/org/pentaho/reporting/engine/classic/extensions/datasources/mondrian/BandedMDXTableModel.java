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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.util.MemberAddingStrategy;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.util.ResultSetOrderMemberAddingStrategy;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.util.SortedMemberAddingStrategy;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
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
public class BandedMDXTableModel extends AbstractTableModel implements CloseableTableModel, MetaTableModel
{
  private static final Log logger = LogFactory.getLog(DenormalizedMDXTableModel.class);

  private Result resultSet;
  private int rowCount;
  private int columnCount;

  private int[] axesSize;
  private IntList columnToAxisPosition;
  private List<Member> columnToMemberMapping;
  private List<String> columnNames;
  private boolean membersOnAxisSorted;

  public BandedMDXTableModel(final Result resultSet, final int rowLimit)
  {
    this(resultSet, rowLimit, false);
  }

  public BandedMDXTableModel(final Result resultSet, final int rowLimit, final boolean membersOnAxisSorted)
  {
    ArgumentNullException.validate("resultSet", resultSet);

    this.resultSet = resultSet;
    this.membersOnAxisSorted = membersOnAxisSorted;

    // rowcount is the product of all axis-sizes. If an axis contains more than one member, then
    // Mondrian already performs the crossjoin for us.

    // column count is the sum of the maximum of all hierachy levels of all axis.

    final Axis[] axes = this.resultSet.getAxes();


    this.axesSize = computeItemsPerAxis(axes);
    this.rowCount = computeRowCount(axesSize);

    final int[] axesMembers = computeTotalColumnsPerAxis(axes);

    this.columnCount = computeColumnCount(axesMembers);
    this.columnToAxisPosition = computeColumnToAxisMapping(axes, axesMembers);
    this.columnToMemberMapping = Collections.unmodifiableList(computeColumnToMemberMapping(axes, axesMembers));
    this.columnNames = computeColumnNames(axes, columnToMemberMapping);

    if (rowLimit > 0)
    {
      rowCount = Math.min(rowLimit, rowCount);
    }
  }

  protected int computeColumnCount(final int[] axesMembers)
  {
    int columnCount;
    if (axesSize.length == 0)
    {
      columnCount = 1;
    }
    else // if (axesSize.length > 0)
    {
      columnCount = axesSize[0];
      for (int i = 1; i < axesMembers.length; i++)
      {
        columnCount += axesMembers[i];
      }
    }
    return columnCount;
  }

  protected ArrayList<Member> computeColumnToMemberMapping(final Axis[] axes, final int[] axesMembers)
  {
    final ArrayList<Member> columnToMemberMapper = new ArrayList<Member>();
    for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
    {
      final Axis axis = axes[axesIndex];
      final List<Position> positions = axis.getPositions();

      final MemberAddingStrategy strategy =
          membersOnAxisSorted ? new SortedMemberAddingStrategy(positions) : new ResultSetOrderMemberAddingStrategy();

      for (int positionsIndex = 0; positionsIndex < positions.size(); positionsIndex++)
      {
        final Position position = positions.get(positionsIndex);
        for (int positionIndex = 0; positionIndex < position.size(); positionIndex++)
        {
          Member m = position.get(positionIndex);
          computeDeepColumnNames(m, strategy);
        }
      }

      Collection<Member> columnNamesSet = strategy.values();
      if (columnNamesSet.size() != axesMembers[axesIndex])
      {
        logger.error("ERROR: Number of names is not equal the pre-counted number.");
      }

      columnToMemberMapper.addAll(columnNamesSet);
    }
    return columnToMemberMapper;
  }

  protected IntList computeColumnToAxisMapping(final Axis[] axes, final int[] axesMembers)
  {
    IntList columnToAxisPosition = new IntList(columnCount);
    for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
    {
      int memberCntAxis = axesMembers[axesIndex];
      for (int x = 0; x < memberCntAxis; x += 1)
      {
        columnToAxisPosition.add(axesIndex);
      }
    }
    return columnToAxisPosition;
  }

  protected int[] computeTotalColumnsPerAxis(final Axis[] axes)
  {
    final IntList[] membersPerAxis = new IntList[axes.length];
    // Axis contains (zero or more) positions, which contains (zero or more) members
    for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
    {
      final Axis axis = axes[axesIndex];
      membersPerAxis[axesIndex] = computeMemberCountForAxis(axis);
    }

    final int[] axesMembers = new int[axes.length];
    for (int idx = 1; idx < membersPerAxis.length; idx += 1)
    {
      IntList memberList = membersPerAxis[idx];

      int memberCount = 0;
      for (int i = 0; i < memberList.size(); i++)
      {
        memberCount += memberList.get(i);
      }
      axesMembers[idx] = memberCount;
    }
    return axesMembers;
  }

  protected IntList computeMemberCountForAxis(final Axis axis)
  {
    final List<Position> positions = axis.getPositions();

    final IntList memberList = new IntList(10);
    for (int positionsIndex = 0; positionsIndex < positions.size(); positionsIndex++)
    {
      final Position position = positions.get(positionsIndex);
      for (int positionIndex = 0; positionIndex < position.size(); positionIndex++)
      {
        final LinkedHashSet<String> columnNamesSet = new LinkedHashSet<String>();
        Member m = position.get(positionIndex);
        while (m != null)
        {
          final String name = m.getLevel().getUniqueName();
          if (columnNamesSet.contains(name) == false)
          {
            columnNamesSet.add(name);
          }
          m = m.getParentMember();
        }

        int hierarchyLevelCount = columnNamesSet.size();
        if (memberList.size() <= positionIndex)
        {
          memberList.add(hierarchyLevelCount);
        }
        else
        {
          final int existingLevel = memberList.get(positionIndex);
          if (existingLevel < hierarchyLevelCount)
          {
            memberList.set(positionIndex, hierarchyLevelCount);
          }
        }
      }
    }
    return memberList;
  }

  protected List<String> computeColumnNames(final Axis[] axes,
                                            final List<Member> columnToMemberMapper)
  {
    ArrayList<String> columnNames = new ArrayList<String>();
    for (final Member member : columnToMemberMapper)
    {
      columnNames.add(member.getLevel().getUniqueName());
    }
    if (axes.length > 0)
    {
      // now create the column names for the column-axis
      final Axis axis = axes[0];
      final List<Position> positions = axis.getPositions();
      for (int i = 0; i < positions.size(); i++)
      {
        final Position position = positions.get(i);
        columnNames.add(computeUniqueColumnName(position));
      }
    }
    else
    {
      columnNames.add("Measure");
    }
    return Collections.unmodifiableList(columnNames);
  }

  protected int computeRowCount(final int[] axesSize)
  {
    if (axesSize.length > 1)
    {
      int rowCount = axesSize[1];
      for (int i = 2; i < axesSize.length; i++)
      {
        final int size = axesSize[i];
        rowCount *= size;
      }
      return rowCount;
    }
    else
    {
      // special case of having only members on the column axis (but not on row or higher)
      // or having no member on any axis at all
      return 1;
    }
  }

  protected int[] computeItemsPerAxis(final Axis[] axes)
  {
    int[] axesSize = new int[axes.length];
    // process the column axis first ..
    if (axesSize.length > 0)
    {
      final Axis columnAxis = axes[0];
      axesSize[0] = columnAxis.getPositions().size();

      for (int axesIndex = axes.length - 1; axesIndex >= 1; axesIndex -= 1)
      {
        final Axis axis = axes[axesIndex];
        axesSize[axesIndex] = axis.getPositions().size();
      }
    }
    return axesSize;
  }

  /**
   * Computes a set of column names starting with the deepest parent up to the member actually found on the axis.
   *
   * @param m
   */
  protected void computeDeepColumnNames(Member m,
                                        MemberAddingStrategy memberToNameMapping)
  {
    final FastStack<Member> memberStack = new FastStack<Member>();
    while (m != null)
    {
      memberStack.push(m);
      m = m.getParentMember();
    }

    while (memberStack.isEmpty() == false)
    {
      m = memberStack.pop();
      memberToNameMapping.add(m);
    }
  }

  /**
   * Column axis members can be nested (having multiple dimensions or multiple levels of the same dimension) and
   * thus the Member's unique name is not necessarily unique across the whole context (same year mentioned for
   * different product lines, for example). So we need to compute that name recursively.
   *
   * @param position The OLAP position, a list of members uniquely specifying a cell-position.
   * @return the computed name, usually jus a concat of all levels.
   */
  protected String computeUniqueColumnName(final Position position)
  {
    final StringBuilder positionName = new StringBuilder(100);
    for (int j = 0; j < position.size(); j++)
    {
      if (j != 0)
      {
        positionName.append('/');
      }
      final Member member = position.get(j);
      positionName.append(MondrianUtil.getUniqueMemberName(member));
    }
    return positionName.toString();
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
    return columnNames.get(column);
  }

  public Object getValueAt(final int rowIndex,
                           final int columnIndex)
  {
    if (columnIndex >= columnNames.size())
    {
      throw new IndexOutOfBoundsException();
    }


    if (isMeasureColumn(columnIndex))
    {
      final int[] cellKey = computeCellKey(rowIndex, columnIndex);
      final Cell cell = resultSet.getCell(cellKey);
      if (cell.isNull())
      {
        return null;
      }
      return cell.getValue();
    }

    final int[] cellKey = computeCellKey(rowIndex, columnIndex);

    Member candidateMember = getCandidateMembers(columnIndex, cellKey);
    if (candidateMember != null)
    {
      return candidateMember.getName();
    }
    return null;
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
    final int[] cellKey = new int[axesSize.length];
    if (axesSize.length == 0)
    {
      return cellKey;
    }

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

    cellKey[0] = correctedColIndex;

    int tmpRowIdx = rowIndex;
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

  private Member getCandidateMembers(final int columnIndex,
                                     final int[] cellKey)
  {

    final int axisIndex = columnToAxisPosition.get(columnIndex);
    final Axis[] axes = resultSet.getAxes();
    final Axis axis = axes[axisIndex];

    final List<Position> positionList = axis.getPositions();
    if (positionList.isEmpty())
    {
      return null;
    }

    final int posIndex = cellKey[axisIndex];
    final Position position = positionList.get(posIndex);

    final Member memberByName = findMemberByName(position, columnIndex);
    if (memberByName != null)
    {
      return memberByName;
    }
    return findRootMember(position, columnIndex);
  }

  private Member findRootMember(final List<Member> position, final int columnIndex)
  {
    final Dimension dimension = columnToMemberMapping.get(columnIndex).getDimension();
    for (int i = 0; i < position.size(); i++)
    {
      final Member member = position.get(i);
      if (dimension.equals(member.getDimension()))
      {
        if (member.getParentMember() == null)
        {
          return member;
        }
      }
    }
    return null;
  }

  private Member findMemberByName(final List<Member> position, final int columnIndex)
  {
    final Dimension dimension = columnToMemberMapping.get(columnIndex).getDimension();
    for (int i = 0; i < position.size(); i++)
    {
      final Member member = position.get(i);
      if (dimension.equals(member.getDimension()))
      {
        Member match = searchContextMemberOfParents(member, columnIndex);
        if (match != null)
        {
          return match;
        }
      }
    }
    return null;
  }

  private Member searchContextMemberOfParents(final Member member, final int columnIndex)
  {
    String columnName = getColumnName(columnIndex);

    Member candidate = member;
    while (candidate != null)
    {
      if (candidate.getLevel().getUniqueName().equals(columnName))
      {
        return candidate;
      }
      candidate = candidate.getParentMember();
    }
    return null;
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
    if (columnIndex >= columnNames.size())
    {
      throw new IndexOutOfBoundsException();
    }

    if (isMeasureColumn(columnIndex))
    {
      final int[] cellKey = computeCellKey(rowIndex, columnIndex);
      final Cell cell = resultSet.getCell(cellKey);
      return new MDXMetaDataCellAttributes(EmptyDataAttributes.INSTANCE, cell);
    }

    final int[] cellKey = computeCellKey(rowIndex, columnIndex);
    Member contextMember = getCandidateMembers(columnIndex, cellKey);
    if (contextMember != null)
    {
      return new MDXMetaDataMemberAttributes(EmptyDataAttributes.INSTANCE, contextMember);
    }
    return EmptyDataAttributes.INSTANCE;
  }

  private boolean isMeasureColumn(final int columnIndex)
  {
    if (columnIndex >= columnToMemberMapping.size())
    {
      return true;
    }
    return false;
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

}
