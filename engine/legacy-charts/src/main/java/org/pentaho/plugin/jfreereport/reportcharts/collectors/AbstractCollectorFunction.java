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

package org.pentaho.plugin.jfreereport.reportcharts.collectors;

import org.jfree.data.general.Dataset;
import org.pentaho.plugin.jfreereport.reportcharts.CollectorFunctionResult;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A base class for collector functions. The series name can be given as either an static text or a column name. If
 * given and not empty, a column name takes precedence over a static series name.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractCollectorFunction extends AbstractFunction {
  private static class CacheKey {
    private int index;
    private ReportStateKey stateKey;

    private CacheKey( final ReportStateKey stateKey, final int index ) {
      this.stateKey = stateKey;
      this.index = index;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( index != cacheKey.index ) {
        return false;
      }
      if ( stateKey != null ? !stateKey.equals( cacheKey.stateKey ) : cacheKey.stateKey != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = index;
      result = 31 * result + ( stateKey != null ? stateKey.hashCode() : 0 );
      return result;
    }
  }

  private static class StaticCollectorFunctionResult implements CollectorFunctionResult {
    private Dataset dataSet;
    private CacheKey cacheKey;

    private StaticCollectorFunctionResult( final Dataset dataSet, final CacheKey cacheKey ) {
      this.dataSet = dataSet;
      this.cacheKey = cacheKey;
    }

    public Dataset getDataSet() {
      return dataSet;
    }

    public Object getCacheKey() {
      return cacheKey;
    }
  }

  private ArrayList<String> seriesNames;
  private ArrayList<String> seriesColumns;
  private HashMap<ReportStateKey, Sequence<Dataset>> results;
  private String summaryGroup;
  private String resetGroup;

  private Boolean autoGenerateMissingSeriesNames;

  /**
   * The current row-count.
   */
  private transient Sequence<Dataset> result;
  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;
  private transient ReportStateKey groupStateKey;
  /**
   * The current group key is used to store the result for the current group.
   */
  private String crosstabFilterGroup;
  private transient int lastGroupSequenceNumber;

  public AbstractCollectorFunction() {
    results = new HashMap<ReportStateKey, Sequence<Dataset>>();
    seriesColumns = new ArrayList<String>();
    seriesNames = new ArrayList<String>();
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

  public boolean isSummaryDataSet() {
    return summaryGroup != null;
  }

  public void setSeriesName( final int index, final String field ) {
    if ( seriesNames.size() == index ) {
      seriesNames.add( field );
    } else {
      seriesNames.set( index, field );
    }
  }

  public String getSeriesName( final int index ) {
    return seriesNames.get( index );
  }

  public int getSeriesNameCount() {
    return seriesNames.size();
  }

  public String[] getSeriesName() {
    return seriesNames.toArray( new String[ seriesNames.size() ] );
  }

  public void setSeriesName( final String[] fields ) {
    this.seriesNames.clear();
    this.seriesNames.addAll( Arrays.asList( fields ) );
  }

  public void setSeriesColumn( final int index, final String field ) {
    if ( seriesColumns.size() == index ) {
      seriesColumns.add( field );
    } else {
      seriesColumns.set( index, field );
    }
  }

  public String getSeriesColumn( final int index ) {
    return seriesColumns.get( index );
  }

  public int getSeriesColumnCount() {
    return seriesColumns.size();
  }

  public String[] getSeriesColumn() {
    return seriesColumns.toArray( new String[ seriesColumns.size() ] );
  }

  public void setSeriesColumn( final String[] fields ) {
    this.seriesColumns.clear();
    this.seriesColumns.addAll( Arrays.asList( fields ) );
  }

  public String getResetGroup() {
    return resetGroup;
  }

  public void setResetGroup( final String resetGroup ) {
    this.resetGroup = resetGroup;
  }

  public String getSummaryGroup() {
    return summaryGroup;
  }

  public void setSummaryGroup( final String summaryGroup ) {
    this.summaryGroup = summaryGroup;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public final Object getValue() {
    final Dataset dataSet = getDataSet();
    if ( groupStateKey != null ) {
      return new StaticCollectorFunctionResult( dataSet, new CacheKey( groupStateKey, lastGroupSequenceNumber ) );
    }
    if ( globalStateKey != null ) {
      return new StaticCollectorFunctionResult( dataSet, new CacheKey( globalStateKey, lastGroupSequenceNumber ) );
    }
    return null;
  }


  public final void reportInitialized( final ReportEvent event ) {
    globalStateKey = event.getState().getProcessKey();
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      result = new Sequence<Dataset>();
      lastGroupSequenceNumber = 0;

      results.clear();
      results.put( globalStateKey, result );
    } else {
      result = results.get( globalStateKey );
      lastGroupSequenceNumber = 0;
    }
  }

  protected ReportStateKey getStateKey() {
    if ( groupStateKey == null ) {
      return globalStateKey;
    }
    return groupStateKey;
  }

  protected int getLastGroupSequenceNumber() {
    return lastGroupSequenceNumber;
  }

  public final void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getResetGroup(), event ) ) {
      groupStateKey = event.getState().getProcessKey();
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        result = new Sequence<Dataset>();
        lastGroupSequenceNumber = 0;

        results.put( globalStateKey, result );
        results.put( groupStateKey, result );
      } else {
        // Activate the current group, which was filled in the prepare run.
        result = results.get( groupStateKey );
      }
    }

    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public final void itemsAdvanced( final ReportEvent event ) {
    if ( isSummaryDataSet() ) {
      return;
    }
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) == false ) {
      return;
    }

    buildDataset();
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public final void groupFinished( final ReportEvent event ) {
    if ( isSummaryDataSet() == false ) {
      return;
    }

    if ( FunctionUtilities.isDefinedGroup( getSummaryGroup(), event ) == false ) {
      return;
    }

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) == false ) {
      return;
    }

    buildDataset();
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  protected void buildDataset() {
  }

  protected abstract Dataset createNewDataset();

  protected Dataset getDataSet() {
    if ( result == null ) {
      return null;
    }

    final Dataset dataset = result.get( lastGroupSequenceNumber );
    if ( dataset != null ) {
      return dataset;
    }

    final Dataset created = createNewDataset();
    result.set( lastGroupSequenceNumber, created );
    return created;
  }

  protected int getMaximumSeriesIndex() {
    return Math.max( seriesColumns.size(), seriesNames.size() );
  }

  public Boolean getAutoGenerateMissingSeriesNames() {
    return autoGenerateMissingSeriesNames;
  }

  public void setAutoGenerateMissingSeriesNames( final Boolean autoGenerateMissingSeriesNames ) {
    this.autoGenerateMissingSeriesNames = autoGenerateMissingSeriesNames;
  }

  protected String generateName( final int index ) {
    return "Series " + ( index + 1 );
  }

  protected Comparable querySeriesValue( final int index ) {
    if ( index < getSeriesColumnCount() ) {
      final String seriesColumn = getSeriesColumn( index );
      if ( StringUtils.isEmpty( seriesColumn ) == false ) {
        final Object o = getDataRow().get( seriesColumn );
        if ( o instanceof Comparable ) {
          return (Comparable) o;
        }
        if ( Boolean.FALSE.equals( autoGenerateMissingSeriesNames ) ) {
          return null;
        }
        return generateName( index );
      }
    }

    if ( index < getSeriesNameCount() ) {
      final String retval = getSeriesName( index );
      if ( retval != null ) {
        return retval;
      }
    }
    if ( Boolean.FALSE.equals( autoGenerateMissingSeriesNames ) ) {
      return null;
    }
    return generateName( index );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final AbstractCollectorFunction expression = (AbstractCollectorFunction) super.getInstance();
    expression.groupStateKey = null;
    expression.globalStateKey = null;
    expression.lastGroupSequenceNumber = -1;
    expression.results = new HashMap<ReportStateKey, Sequence<Dataset>>();
    expression.seriesColumns = (ArrayList<String>) seriesColumns.clone();
    expression.seriesNames = (ArrayList<String>) seriesNames.clone();
    return expression;
  }


}
