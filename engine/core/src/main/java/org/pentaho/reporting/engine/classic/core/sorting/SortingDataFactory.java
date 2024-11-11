/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.sorting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class SortingDataFactory extends CompoundDataFactory {
  private static final Log logger = LogFactory.getLog( SortingDataFactory.class );

  private final PerformanceMonitorContext performanceMonitorContext;

  public SortingDataFactory( final DataFactory parent, final PerformanceMonitorContext performanceMonitorContext ) {
    ArgumentNullException.validate( "parent", parent );
    ArgumentNullException.validate( "performanceMonitorContext", performanceMonitorContext );

    this.performanceMonitorContext = performanceMonitorContext;
    add( parent );
  }

  protected TableModel postProcess( final String query, final DataRow parameters, final TableModel tableModel ) {
    if ( tableModel == null ) {
      logger.debug( "No data, therefore no sorting." );
      return null;
    }
    if ( tableModel.getRowCount() == 1 || tableModel.getColumnCount() == 0 ) {
      logger.debug( "Empty data, therefore no sorting." );
      return tableModel;
    }
    Object o = parameters.get( DataFactory.QUERY_SORT );
    if ( ( o instanceof List<?> ) == false ) {
      logger.debug( "Sort constraints are not in list format." );
      return tableModel;
    }
    List<SortConstraint> sort = validate( (List<?>) o );
    List<SortConstraint> resolvedConstraints = resolveColumnAliases( tableModel, sort );
    if ( resolvedConstraints.isEmpty() ) {
      logger.debug( "Resolved sort constraints are empty." );
      return tableModel;
    }

    return sort( tableModel, resolvedConstraints );
  }

  private List<SortConstraint> resolveColumnAliases( final TableModel tableModel, final List<SortConstraint> sort ) {
    ArrayList<SortConstraint> result = new ArrayList<SortConstraint>( sort.size() );
    for ( final SortConstraint constraint : sort ) {
      String field = constraint.getField();
      if ( StringUtils.isEmpty( field ) ) {
        DebugLog.log( "Sort field is empty" );
        continue;
      }

      if ( field.startsWith( ClassicEngineBoot.INDEX_COLUMN_PREFIX ) ) {
        String idx = field.substring( ClassicEngineBoot.INDEX_COLUMN_PREFIX.length() );
        try {
          int idxParsed = Integer.parseInt( idx );
          if ( idxParsed >= 0 && idxParsed < tableModel.getColumnCount() ) {
            String columnName = tableModel.getColumnName( idxParsed );
            if ( !StringUtils.isEmpty( columnName ) ) {
              result.add( new SortConstraint( columnName, constraint.isAscending() ) );
            } else {
              DebugLog.log( "Resolved column name for column at index " + idxParsed + " is empty." );
            }
          } else {
            logger.debug( "Invalid index on indexed column '" + field + "'" );
          }
        } catch ( final NumberFormatException iae ) {
          logger.debug( "Unable to parse non-decimal index on indexed column '" + field + "'", iae );
        }
      } else {
        result.add( constraint );
      }
    }
    return result;
  }

  private TableModel sort( final TableModel tableModel, final List<SortConstraint> sortConstraints ) {
    logger.debug( "Sorting by " + sortConstraints );
    PerformanceLoggingStopWatch stopWatch =
        this.performanceMonitorContext.createStopWatch( PerformanceTags.REPORT_QUERY_SORT );
    stopWatch.start();
    try {
      return new SortingTableModel( new MetaNormalizedTableModel( tableModel ), sortConstraints );
    } finally {
      stopWatch.close();
    }
  }

  private List<SortConstraint> validate( final List<?> o ) {
    List<SortConstraint> c = new ArrayList<SortConstraint>( o.size() );
    for ( final Object raw : o ) {
      if ( raw instanceof SortConstraint ) {
        c.add( (SortConstraint) raw );
      }
    }
    return c;
  }
}
