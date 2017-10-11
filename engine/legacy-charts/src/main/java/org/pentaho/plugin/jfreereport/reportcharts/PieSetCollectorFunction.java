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

import org.jfree.data.general.DefaultPieDataset;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated
 */
public class PieSetCollectorFunction extends AbstractFunction implements ICollectorFunction {

  // This COULD descend from BaseCollectorFunction, EXCEPT that this class
  // has a String seriesColumn field, and BaseCollectorFunction has
  // a boolean seriesColumn field. It's not worth it to require users
  // to change old reports because of the field name change, so we 
  // have left this class ugly for the time being. 

  private static final long serialVersionUID = -5778788510651234706L;

  // Things that change during the processing of the report
  private int currentIndex;
  private DefaultPieDataset pieDataset;
  private List results;

  // Things that come from the report definition
  private String seriesColumn;
  private String valueColumn;
  private String group;
  private String resetGroup;
  private boolean summaryOnly;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public PieSetCollectorFunction() {
    results = new ArrayList();
  }


  public String getSeriesColumn() {
    return seriesColumn;
  }

  public String getValueColumn() {
    return valueColumn;
  }

  public String getGroup() {
    return group;
  }

  public String getResetGroup() {
    return resetGroup;
  }

  public void setSeriesColumn( final String value ) {
    seriesColumn = value;
  }

  public void setValueColumn( final String value ) {
    valueColumn = value;
  }

  public void setGroup( final String value ) {
    group = value;
  }

  public void setResetGroup( final String value ) {
    resetGroup = value;
  }

  public boolean isSummaryOnly() {
    return summaryOnly;
  }

  public void setSummaryOnly( final boolean value ) {
    summaryOnly = value;
  }

  public Object getValue() {
    return this;
  }

  public void reportInitialized( final ReportEvent event ) {
    currentIndex = -1;

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      pieDataset = null;
      results.clear();
      if ( getResetGroup() == null ) {
        pieDataset = new DefaultPieDataset();
        results.add( pieDataset );
      }
    } else {
      // Activate the current group, which was filled in the prepare run.
      if ( getResetGroup() == null && results.size() > 0 ) {
        pieDataset = (DefaultPieDataset) results.get( 0 );
      }
    }
  }

  public void groupStarted( final ReportEvent event ) {

    final String localGroup = getGroup();
    if ( localGroup != null ) {
      if ( FunctionUtilities.isDefinedGroup( getResetGroup(), event ) ) {
        // reset ...
        if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
          pieDataset = new DefaultPieDataset();
          results.add( pieDataset );
        } else {
          if ( FunctionUtilities.isLayoutLevel( event ) ) {
            // Activate the current group, which was filled in the
            // prepare run.
            currentIndex += 1;
            pieDataset = (DefaultPieDataset) results.get( currentIndex );
          }
        }
      }
    }
  }

  public void itemsAdvanced( final ReportEvent reportEvent ) {

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, reportEvent ) == false ) {
      // we do not modify the created dataset if this is not the function
      // computation run. (FunctionLevel '0')
      return;
    }

    final DefaultPieDataset localPieDataset = pieDataset;

    if ( !isSummaryOnly() ) {
      final Object seriesObject = getDataRow().get( getSeriesColumn() );
      final Comparable seriesComparable;
      if ( seriesObject instanceof Comparable ) {
        seriesComparable = (Comparable) seriesObject;
      } else {
        // ok, we need some better error management here. Its a
        // prototype :)
        seriesComparable = "PIESETCOLL.USER_ERROR_CATEGORY_NOT_COMPARABLE"; //$NON-NLS-1$
      }
      final Object valueObject = getDataRow().get( getValueColumn() );

      Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;

      Object isThere = null;
      try {
        isThere = localPieDataset.getValue( seriesComparable );
      } catch ( Exception ignored ) {
      }
      if ( isThere != null ) {
        final double val = ( value != null ) ? value.doubleValue() : 0;
        value = new Double( val + ( (Number) isThere ).doubleValue() );
      }

      localPieDataset.setValue( seriesComparable, value );

    }
  }

  public void groupFinished( final ReportEvent reportEvent ) {

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, reportEvent ) == false ) {
      // we do not modify the created dataset if this is not the function
      // computation run. (FunctionLevel '0')
      return;
    }

    final DefaultPieDataset localPieDataset = pieDataset;

    if ( isSummaryOnly() ) {
      if ( FunctionUtilities.isDefinedGroup( getGroup(), reportEvent ) ) {
        // we can be sure that everything has been computed here. So
        // grab the
        // values and add them to the dataset.
        final Object seriesObject = getDataRow().get( getSeriesColumn() );
        final Comparable seriesComparable;
        if ( seriesObject instanceof Comparable ) {
          seriesComparable = (Comparable) seriesObject;
        } else {
          // ok, we need some better error management here. Its a
          // prototype :)
          seriesComparable = ( "PIESETCOLL.USER_ERROR_SERIES_NOT_COMPARABLE" ); //$NON-NLS-1$
        }
        final Object valueObject = getDataRow().get( getValueColumn() );

        final Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;
        localPieDataset.setValue( seriesComparable, value );
      }
    }
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final PieSetCollectorFunction fn = (PieSetCollectorFunction) super.getInstance();
    fn.pieDataset = null;
    fn.results = new ArrayList();
    fn.currentIndex = 0;
    return fn;
  }

  public Object getCacheKey() {
    return this.pieDataset;
  }

  public Object getDatasourceValue() {
    return this.pieDataset;
  }

}
