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

package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A data-collector that collects index information from the report. The information is collected on all group start,
 * group finished and detail band events.
 *
 * @author Thomas Morgner.
 */
public class IndexDataGeneratorFunction extends AbstractFunction implements PageEventListener {
  private static class IndexDataHolder implements Serializable {
    private Object data;
    private TreeSet<Integer> pages;

    private IndexDataHolder( final Object data ) {
      this.data = data;
      this.pages = new TreeSet<Integer>();
    }

    public void addPage( final int page ) {
      this.pages.add( IntegerCache.getInteger( page ) );
    }

    public Object getData() {
      return data;
    }

    public String getPagesText( final String indexSeparator,
                                final boolean condensedStyle ) {
      final Integer[] groupCount = pages.toArray( new Integer[ pages.size() ] );
      if ( condensedStyle ) {
        return IndexUtility.getCondensedIndexText( groupCount, indexSeparator );
      }
      return IndexUtility.getIndexText( groupCount, indexSeparator );
    }

    public Integer[] getPages() {
      return pages.toArray( new Integer[ pages.size() ] );
    }
  }

  private PageFunction pageFunction;
  private TypedTableModel model;

  private String dataField;
  private FormulaExpression dataFormula;

  private String indexSeparator;
  private boolean condensedStyle;

  private transient boolean initialized;
  private TreeMap<String, IndexDataHolder> dataStorage;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public IndexDataGeneratorFunction() {
    this.pageFunction = new PageFunction();
    this.indexSeparator = ".";
    this.model = new TypedTableModel();
    this.dataFormula = new FormulaExpression();
    this.dataStorage = new TreeMap<String, IndexDataHolder>();
  }

  public boolean isCondensedStyle() {
    return condensedStyle;
  }

  public void setCondensedStyle( final boolean condensedStyle ) {
    this.condensedStyle = condensedStyle;
  }

  public String getIndexSeparator() {
    return indexSeparator;
  }

  public void setIndexSeparator( final String indexSeparator ) {
    this.indexSeparator = indexSeparator;
  }

  public String getDataFormula() {
    return dataFormula.getFormula();
  }

  public void setDataFormula( final String titleFormula ) {
    this.dataFormula.setFormula( titleFormula );
  }

  public String getDataField() {
    return dataField;
  }

  public void setDataField( final String dataField ) {
    this.dataField = dataField;
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( initialized == false ) {
      initialized = true;
      model.addColumn( "item-data", Object.class );
      model.addColumn( "item-pages", String.class );
      model.addColumn( "item-pages-array", Integer[].class );
      model.addColumn( "item-key", String.class );
    }

    pageFunction.reportInitialized( event );
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    pageFunction.reportStarted( event );
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "index".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    final Object o = computeDataValue( event );
    if ( o == null ) {
      return;
    }

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      dataStorage.put( String.valueOf( o ), new IndexDataHolder( o ) );
    } else if ( FunctionUtilities.isLayoutLevel( event ) ) {
      final IndexDataHolder o1 = dataStorage.get( String.valueOf( o ) );
      if ( o1 == null ) {
        throw new IllegalStateException
          ( "Unable to compute index: Function values changed between prepare and layout run" );
      }
      o1.addPage( pageFunction.getPage() );
    }
  }

  private DataRow extractDataRow( final ReportEvent event ) {
    if ( event.isDeepTraversing() == false ) {
      return getDataRow();
    }

    return event.getOriginatingState().getDataRow();
  }

  private Object computeDataValue( final ReportEvent event ) {
    final DataRow dataRow = extractDataRow( event );
    if ( StringUtils.isEmpty( dataField ) == false ) {
      return dataRow.get( dataField );
    }
    try {
      this.dataFormula.setRuntime( new WrapperExpressionRuntime( dataRow, getRuntime() ) );
      return dataFormula.getValue();
    } finally {
      this.dataFormula.setRuntime( null );
    }
  }

  /**
   * Receives notification that a new page is being started.
   *
   * @param event The event.
   */
  public void pageStarted( final ReportEvent event ) {
    pageFunction.pageStarted( event );
  }

  /**
   * Receives notification that a page is completed.
   *
   * @param event The event.
   */
  public void pageFinished( final ReportEvent event ) {
    pageFunction.pageFinished( event );
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      final Iterator iterator = dataStorage.entrySet().iterator();
      while ( iterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) iterator.next();
        final String key = (String) entry.getKey();
        final IndexDataHolder data = (IndexDataHolder) entry.getValue();
        model.addRow(
          new Object[] { data.getData(), data.getPagesText( indexSeparator, condensedStyle ), data.getPages(), key } );
      }
    } else if ( FunctionUtilities.isLayoutLevel( event ) ) {
      final int rowCount = model.getRowCount();
      for ( int i = 0; i < rowCount; i++ ) {
        final String key = (String) model.getValueAt( i, 3 );
        final IndexDataHolder dataHolder = dataStorage.get( key );
        model.setValueAt( dataHolder.getPagesText( indexSeparator, condensedStyle ), i, 1 );
        model.setValueAt( dataHolder.getPages(), i, 2 );
      }
    }
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return model;
  }

  /**
   * Checks whether this expression is a deep-traversing expression. Deep-traversing expressions receive events from all
   * sub-reports. This returns false by default, as ordinary expressions have no need to be deep-traversing.
   *
   * @return false.
   */
  public boolean isDeepTraversing() {
    return true;
  }

  /**
   * Clones the expression.  The expression should be reinitialized after the cloning. <P> Expressions maintain no
   * state, cloning is done at the beginning of the report processing to disconnect the expression from any other object
   * space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final IndexDataGeneratorFunction o = (IndexDataGeneratorFunction) super.clone();
    o.dataFormula = (FormulaExpression) dataFormula.clone();
    o.pageFunction = (PageFunction) pageFunction.clone();
    return o;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final IndexDataGeneratorFunction instance = (IndexDataGeneratorFunction) super.getInstance();
    instance.model = new TypedTableModel();
    instance.pageFunction = (PageFunction) pageFunction.getInstance();
    instance.dataFormula = (FormulaExpression) dataFormula.getInstance();
    instance.dataStorage = new TreeMap<String, IndexDataHolder>();
    instance.initialized = false;
    return instance;
  }

  public TypedTableModel getModel() {
    return model;
  }
}
