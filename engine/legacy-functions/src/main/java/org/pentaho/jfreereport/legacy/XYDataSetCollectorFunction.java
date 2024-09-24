/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.jfreereport.legacy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @deprecated These functions are no longer supported.
 */
public class XYDataSetCollectorFunction extends AbstractFunction {
  private static final Log LOG = LogFactory.getLog( XYDataSetCollectorFunction.class.getName() );

  private ArrayList seriesNames;
  private String domainColumn;
  private XYSeriesCollection xyDataset;
  private String resetGroup;
  private HashMap series;


  public XYDataSetCollectorFunction() {
    this.seriesNames = new ArrayList();
    series = new HashMap();

    xyDataset = new XYSeriesCollection();
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


  public String getDomainColumn() {
    return domainColumn;
  }


  public void setDomainColumn( String domainColumn ) {
    this.domainColumn = domainColumn;
  }


  public String getResetGroup() {
    return resetGroup;
  }


  public void setResetGroup( String resetGroup ) {
    this.resetGroup = resetGroup;
  }


  public Object getValue() {
    return xyDataset;
  }


  public void groupStarted( ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getResetGroup(), event ) ) {
      // reset ...
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        if ( LOG.isDebugEnabled() ) {
          LOG.debug( "XYDataSetCollectorFunction.groupStarted " );//NON-NLS
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        for ( int i = 0; i < getSeriesName().length; i++ ) {
          String s = getSeriesName()[ i ];
          XYSeries series = new XYSeries( s, true, true );
          xyDataset.addSeries( series );
          this.series.put( s, series );

        }
        this.xyDataset = xyDataset;
        //results.add(categoryDataset);
      } else {
        if ( FunctionUtilities.isLayoutLevel( event ) ) {
          // Activate the current group, which was filled in the prepare run.
          //currentIndex += 1;
          //categoryDataset = results.get(currentIndex);
        }
      }
    } else {
      // reset ...
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        if ( LOG.isDebugEnabled() ) {
          LOG.debug( "XYDataSetCollectorFunction.groupStarted " );//NON-NLS
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        for ( int i = 0; i < getSeriesName().length; i++ ) {
          String s = getSeriesName()[ i ];
          XYSeries series = new XYSeries( s, true, true );
          xyDataset.addSeries( series );
          this.series.put( s, series );
        }
        this.xyDataset = xyDataset;
        //results.add(categoryDataset);
      }
    }
  }


  public void itemsAdvanced( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      final Object domainValue = getDataRow().get( getDomainColumn() );
      if ( domainValue == null ) {
        return;
      }
      if ( !( domainValue instanceof Number ) ) {
        return;
      }

      final Number x = (Number) domainValue;
      for ( int i = 0; i < seriesNames.size(); i++ ) {
        String sn = (String) seriesNames.get( i );
        final Object o = getDataRow().get( sn );
        if ( o instanceof Number ) {
          Number y = (Number) o;
          final XYSeries o1 = (XYSeries) series.get( sn );
          o1.add( x, y );
        }
      }
    }

  }


  public Expression getInstance() {
    final XYDataSetCollectorFunction fn = (XYDataSetCollectorFunction) super.getInstance();
    fn.xyDataset = null;
    fn.series = new HashMap();
    return fn;
  }

}
