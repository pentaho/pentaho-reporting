/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.sorting;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class SortingDataFactory extends CompoundDataFactory
{
  private static final Log logger = LogFactory.getLog(SortingDataFactory.class);

  private final PerformanceMonitorContext performanceMonitorContext;

  public SortingDataFactory(final DataFactory parent,
                            final PerformanceMonitorContext performanceMonitorContext)
  {
    ArgumentNullException.validate("parent", parent);
    ArgumentNullException.validate("performanceMonitorContext", performanceMonitorContext);

    this.performanceMonitorContext = performanceMonitorContext;
    add(parent);
  }

  protected TableModel postProcess(final String query, final DataRow parameters, final TableModel tableModel)
  {
    if (tableModel == null)
    {
      return null;
    }
    if (tableModel.getRowCount() == 1 || tableModel.getColumnCount() == 0)
    {
      return tableModel;
    }
    Object o = parameters.get(DataFactory.QUERY_SORT);
    if ((o instanceof List<?>) == false)
    {
      return tableModel;
    }
    List<SortConstraint> sort = validate((List<?>) o);
    List<SortConstraint> resolvedConstraints = resolveColumnAliases(tableModel, sort);
    if (resolvedConstraints.isEmpty())
    {
      return tableModel;
    }

    return sort(tableModel, resolvedConstraints);
  }

  private List<SortConstraint> resolveColumnAliases(final TableModel tableModel, final List<SortConstraint> sort)
  {
    ArrayList<SortConstraint> result = new ArrayList<SortConstraint>(sort.size());
    for (final SortConstraint constraint : sort)
    {
      String field = constraint.getField();
      if (StringUtils.isEmpty(field))
      {
        continue;
      }

      if (field.startsWith(ClassicEngineBoot.INDEX_COLUMN_PREFIX))
      {
        String idx = field.substring(ClassicEngineBoot.INDEX_COLUMN_PREFIX.length());
        try
        {
          int idxParsed = Integer.parseInt(idx);
          if (idxParsed >= 0 && idxParsed < tableModel.getColumnCount())
          {
            result.add(new SortConstraint(tableModel.getColumnName(idxParsed), constraint.isAscending()));
          }
          else
          {
            logger.debug("Invalid index on indexed column '" + field + "'");
          }
        }
        catch (final NumberFormatException iae)
        {
          logger.debug("Unable to parse non-decimal index on indexed column '" + field + "'", iae);
        }
      }
      else
      {
        result.add(constraint);
      }
    }
    return result;
  }

  private TableModel sort(final TableModel tableModel, final List<SortConstraint> sortConstraints)
  {
    logger.debug("Sorting by " + sortConstraints);
    PerformanceLoggingStopWatch stopWatch = this.performanceMonitorContext.createStopWatch(PerformanceTags.REPORT_QUERY_SORT);
    stopWatch.start();
    try
    {
      return new SortingTableModel(new MetaNormalizedTableModel(tableModel), sortConstraints);
    }
    finally
    {
      stopWatch.close();
    }
  }

  private List<SortConstraint> validate(final List<?> o)
  {
    List<SortConstraint> c = new ArrayList<SortConstraint>(o.size());
    for (final Object raw : o)
    {
      if (raw instanceof SortConstraint)
      {
        c.add((SortConstraint) raw);
      }
    }
    return c;
  }
}
