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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.Dataset;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creation-Date: 07.06.2007, 18:30:22
 *
 * @author Gretchen Moran
 */
public abstract class BaseCollectorFunction extends AbstractFunction implements ICollectorFunction {
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


  protected static final Log logger = LogFactory.getLog( BaseCollectorFunction.class );

  private Dataset dataset;
  private String group;
  private String resetGroup;
  private ArrayList results;
  private ArrayList seriesNames;
  private int currentIndex;
  private boolean summaryOnly;
  private boolean seriesColumn;
  private ReportStateKey processKey;

  protected BaseCollectorFunction() {
    this.seriesNames = new ArrayList();
    this.results = new ArrayList();
    seriesColumn = false;
    summaryOnly = false;
  }

  public void setSeriesName( final int index, final String field ) {
    if ( seriesNames.size() == index ) {
      seriesNames.add( field );
    } else {
      seriesNames.set( index, field );
    }
  }

  public String getSeriesName( final int index ) {
    return (String) seriesNames.get( index );
  }

  public int getSeriesNameCount() {
    return seriesNames.size();
  }

  public String[] getSeriesName() {
    return (String[]) seriesNames.toArray( new String[ seriesNames.size() ] );
  }

  public void setSeriesName( final String[] fields ) {
    this.seriesNames.clear();
    this.seriesNames.addAll( Arrays.asList( fields ) );
  }

  protected Dataset createNewDataset() {
    return getNewDataset();
  }

  public boolean isSummaryOnly() {
    return summaryOnly;
  }

  public void setSummaryOnly( final boolean value ) {
    summaryOnly = value;
  }

  public boolean isSeriesColumn() {
    return seriesColumn;
  }

  public void setSeriesColumn( final boolean value ) {
    seriesColumn = value;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup( final String group ) {
    this.group = group;
  }

  public String getResetGroup() {
    return resetGroup;
  }

  public void setResetGroup( final String resetGroup ) {
    this.resetGroup = resetGroup;
  }

  /*
   * ---------------------------------------------------------------- Now the function implementation ...
   */

  /**
   * @return the dataset
   */
  public Object getValue() {
    return this;
  }

  public Object getDatasourceValue() {
    return dataset;
  }

  public void reportInitialized( final ReportEvent event ) {
    currentIndex = -1;
    if ( processKey == null ) {
      processKey = event.getState().getProcessKey();
    }


    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      dataset = null;
      results.clear();
      if ( getResetGroup() == null ) {

        dataset = createNewDataset();
        results.add( dataset );

      }
    } else {
      // Activate the current group, which was filled in the prepare run.
      if ( getResetGroup() == null && results.isEmpty() == false ) {
        dataset = (AbstractDataset) results.get( 0 );
      }
    }
  }

  public void groupStarted( final ReportEvent event ) {
    final String localGroup = getGroup();
    if ( localGroup == null ) {
      return;
    }
    if ( !FunctionUtilities.isDefinedGroup( getResetGroup(), event ) ) {
      return;
    }
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      dataset = createNewDataset();
      results.add( dataset );
    } else if ( FunctionUtilities.isLayoutLevel( event ) ) {
      // Activate the current group, which was filled in the prepare run.
      currentIndex += 1;
      dataset = (AbstractDataset) results.get( currentIndex );
    }

  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final BaseCollectorFunction fn = (BaseCollectorFunction) super.getInstance();
    fn.dataset = null;
    fn.processKey = null;
    fn.results = new ArrayList();
    fn.seriesNames = (ArrayList) seriesNames.clone();
    return fn;
  }

  public Object getCacheKey() {
    return new CacheKey( processKey, currentIndex );
  }

  /**
   * @return
   * @deprecated This is not a getter and is an internal function! Protected and should have a different name!
   */
  public AbstractDataset getNewDataset() {
    return null;
  }

}
