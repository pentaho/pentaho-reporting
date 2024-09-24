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
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A data-collector that collects table-of-contents items at group-starts. The function collects these items accross
 * subreport boundaries.
 *
 * @author Thomas Morgner.
 */
public class TocDataGeneratorFunction extends AbstractFunction implements PageEventListener {
  private PageFunction pageFunction;
  private TypedTableModel model;
  private ArrayList<String> groups;
  private ArrayList<Integer> groupCount;
  private ArrayList<Object> groupValues;
  private String titleField;
  private FormulaExpression titleFormula;
  private String indexSeparator;
  private boolean collectDetails;

  private transient int groupIndex;
  private transient boolean rowAdded;
  private transient int rowCounter;
  private transient boolean initialized;
  private int dependencyLevel;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public TocDataGeneratorFunction() {
    this.groups = new ArrayList<String>();
    this.pageFunction = new PageFunction();
    this.indexSeparator = ".";
    this.model = new TypedTableModel();
    this.titleFormula = new FormulaExpression();
    this.dependencyLevel = LayoutProcess.LEVEL_COLLECT;
  }

  public void setDependencyLevel( final int dependencyLevel ) {
    this.dependencyLevel = dependencyLevel;
  }

  /**
   * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
   *
   * @return the level.
   */
  public int getDependencyLevel() {
    return dependencyLevel;
  }

  public String getIndexSeparator() {
    return indexSeparator;
  }

  public void setIndexSeparator( final String indexSeparator ) {
    this.indexSeparator = indexSeparator;
  }

  public String getTitleFormula() {
    return titleFormula.getFormula();
  }

  public void setTitleFormula( final String titleFormula ) {
    this.titleFormula.setFormula( titleFormula );
  }

  public String getTitleField() {
    return titleField;
  }

  public void setTitleField( final String titleField ) {
    this.titleField = titleField;
  }

  public boolean isCollectDetails() {
    return collectDetails;
  }

  public void setCollectDetails( final boolean collectDetails ) {
    this.collectDetails = collectDetails;
  }

  /**
   * Defines the field in the field-list at the given index.
   *
   * @param index the position in the list, where the field should be defined.
   * @param field the name of the field.
   */
  public void setGroup( final int index, final String field ) {
    if ( groups.size() == index ) {
      groups.add( field );
    } else {
      groups.set( index, field );
    }
    groupCount = null;
  }

  /**
   * Returns the defined field at the given index-position.
   *
   * @param index the position of the field name that should be queried.
   * @return the field name at the given position.
   */
  public String getGroup( final int index ) {
    return groups.get( index );
  }

  /**
   * Returns the number of groups defined in this expression.
   *
   * @return the number of groups.
   */
  public int getGroupCount() {
    return groups.size();
  }

  /**
   * Returns all defined groups as array of strings.
   *
   * @return all the groups.
   */
  public String[] getGroup() {
    return groups.toArray( new String[ groups.size() ] );
  }

  /**
   * Defines all groups as array. This completely replaces any previously defined groups.
   *
   * @param fields the new list of groups.
   */
  public void setGroup( final String[] fields ) {
    this.groups.clear();
    this.groups.addAll( Arrays.asList( fields ) );
    groupCount = null;
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
      model.addColumn( "item-title", Object.class );
      model.addColumn( "item-page", Number.class );
      model.addColumn( "item-index", String.class );
      model.addColumn( "item-index-array", Integer[].class );

      if ( groups.size() == 0 ) {
        // we cannot alter the structure of the model after the toc subreport report has been started,
        // so we have to reserve a reasonable amount of groupings here. If you have reports with more
        // than 40 groups, please contact me. I will be delighted to hear about your usage scenario.
        for ( int i = 0; i < 40; i++ ) {
          model.addColumn( "group-value-" + i, Object.class );
        }
      } else {
        for ( int i = 0; i < groups.size(); i++ ) {
          model.addColumn( "group-value-" + i, Object.class );
        }
      }
      groupCount = new ArrayList<Integer>( groups.size() );
      groupValues = new ArrayList<Object>( groups.size() );
    }

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      groupCount = new ArrayList<Integer>( groups.size() );
      groupValues = new ArrayList<Object>( groups.size() );
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
    rowAdded = false;
    groupIndex = -1;
    rowCounter = 0;
  }

  public void groupStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    final Group group = FunctionUtilities.getCurrentDeepTraverseGroup( event );
    if ( group instanceof RelationalGroup == false ) {
      return;
    }
    final RelationalGroup relationalGroup = (RelationalGroup) group;

    groupIndex += 1;
    if ( groupIndex < getGroupCount() ||
      getGroupCount() == 0 ) {
      rowAdded = false;

      final DataRow dataRow = extractDataRow( event );
      final Object groupValue;
      if ( groupIndex < getGroupCount() ) {
        final String fieldName = getGroup( groupIndex );
        if ( fieldName != null ) {
          groupValue = dataRow.get( fieldName );
        } else {
          groupValue = extractFieldFromGroup( relationalGroup, dataRow );
        }
      } else // group-count is zero .. means, collect all groups ..
      {
        groupValue = extractFieldFromGroup( relationalGroup, dataRow );
      }

      addOrUpdateValue( groupValue );
    }
  }

  private DataRow extractDataRow( final ReportEvent event ) {
    if ( event.isDeepTraversing() == false ) {
      return getDataRow();
    }

    return event.getOriginatingState().getDataRow();
  }

  private Object extractFieldFromGroup( final RelationalGroup relationalGroup,
                                        final DataRow dataRow ) {
    final Object groupValue;
    final String[] fields = relationalGroup.getFieldsArray();
    if ( fields.length == 0 ) {
      groupValue = null;
    } else if ( fields.length == 1 ) {
      groupValue = dataRow.get( fields[ 0 ] );
    } else {
      final Object[] localGroupValues = new Object[ fields.length ];
      for ( int i = 0; i < localGroupValues.length; i++ ) {
        localGroupValues[ i ] = dataRow.get( fields[ i ] );
      }
      groupValue = localGroupValues;
    }
    return groupValue;
  }

  /**
   * Receives notification that a group of item bands is about to be processed. <P> The next events will be
   * itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event The event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    if ( collectDetails ) {
      groupIndex += 1;
    }
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    if ( collectDetails ) {
      if ( groupIndex < getGroupCount() || getGroupCount() == 0 ) {
        rowAdded = false;
        final Object groupValue;
        if ( groupIndex < getGroupCount() ) {
          final String fieldName = getGroup( groupIndex );
          if ( fieldName != null ) {
            final DataRow dataRow = extractDataRow( event );
            groupValue = dataRow.get( fieldName );
          } else {
            groupValue = null;
          }
        } else {
          groupValue = null;
        }

        addOrUpdateValue( groupValue );
      }
    }

    collectOrUpdate( event );
  }

  private void collectOrUpdate( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      if ( rowAdded == false ) {
        if ( isValidGroupValues() == false ) {
          return;
        }

        final DataRow dataRow = extractDataRow( event );
        final Object titleValue = computeTitleValue( dataRow );
        final StringBuilder indexText = new StringBuilder();
        final Integer[] indexValues = new Integer[ groupCount.size() ];
        for ( int i = 0; i < groupCount.size(); i++ ) {
          if ( i != 0 ) {
            indexText.append( this.indexSeparator );
          }
          final Integer o = groupCount.get( i );
          indexValues[ i ] = o;
          indexText.append( o );
        }

        model.setValueAt( titleValue, rowCounter, 0 );
        model.setValueAt( new Integer( 9999 ), rowCounter, 1 );
        model.setValueAt( indexText.toString(), rowCounter, 2 );
        model.setValueAt( indexValues, rowCounter, 3 );

        for ( int i = 0; i < groupValues.size(); i++ ) {
          final Object groupValue = groupValues.get( i );
          model.setValueAt( groupValue, rowCounter, 4 + i );
        }
        rowCounter += 1;
        rowAdded = true;
      }
    } else if ( FunctionUtilities.isLayoutLevel( event ) ) {
      if ( rowAdded == false ) {
        if ( isValidGroupValues() == false ) {
          return;
        }
        model.setValueAt( pageFunction.getValue(), rowCounter, 1 );
        rowAdded = true;
        rowCounter += 1;
      }
    }
  }

  private boolean isValidGroupValues() {
    for ( int i = 0; i < groupValues.size(); i++ ) {
      final String s = (String) groupValues.get( i );
      if ( StringUtils.isEmpty( s ) == false ) {
        return true;
      }
    }
    return false;
  }

  private void addOrUpdateValue( final Object groupValue ) {
    if ( groupCount.size() == groupIndex ) {
      // new level entered
      groupCount.add( IntegerCache.getInteger( 1 ) );
      groupValues.add( groupValue );
    } else {
      final int lastIndex = groupCount.size() - 1;
      if ( lastIndex == groupIndex ) {
        // existing level increased
        final Integer o = groupCount.get( lastIndex );
        if ( o == null ) {
          throw new IllegalStateException();
        }
        groupCount.set( lastIndex, IntegerCache.getInteger( o.intValue() + 1 ) );
        groupValues.set( lastIndex, groupValue );
      } else {
        throw new IllegalStateException( "Out of index error: " + groupIndex + " " + groupCount.size() );
      }
    }
  }

  private Object computeTitleValue( final DataRow dataRow ) {
    if ( StringUtils.isEmpty( titleField ) == false ) {
      return dataRow.get( titleField );
    }
    try {
      this.titleFormula.setRuntime( new WrapperExpressionRuntime( dataRow, getRuntime() ) );
      return titleFormula.getValue();
    } finally {
      this.titleFormula.setRuntime( null );
    }
  }

  /**
   * Receives notification that a group of item bands has been completed. <P> The itemBand is finished, the report
   * starts to close open groups.
   *
   * @param event The event.
   */
  public void itemsFinished( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    // just to make sure that even a empty subreport leaves its mark ..
    collectOrUpdate( event );

    if ( collectDetails ) {
      if ( ( groupIndex + 2 ) == groupCount.size() ) {
        groupCount.remove( groupCount.size() - 1 );
        groupValues.remove( groupValues.size() - 1 );
      }
      groupIndex -= 1;
    }
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    final Group group = FunctionUtilities.getCurrentDeepTraverseGroup( event );
    if ( group instanceof RelationalGroup == false ) {
      return;
    }

    if ( ( groupIndex + 2 ) == groupCount.size() ) {
      groupCount.remove( groupCount.size() - 1 );
      groupValues.remove( groupValues.size() - 1 );
    }
    groupIndex -= 1;
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
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return model;
  }

  public TypedTableModel getModel() {
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
    final TocDataGeneratorFunction o = (TocDataGeneratorFunction) super.clone();
    o.titleFormula = (FormulaExpression) titleFormula.clone();
    o.pageFunction = (PageFunction) pageFunction.clone();
    if ( groupCount != null ) {
      o.groupCount = (ArrayList<Integer>) groupCount.clone();
      o.groupValues = (ArrayList<Object>) groupValues.clone();
    }
    return o;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TocDataGeneratorFunction instance = (TocDataGeneratorFunction) super.getInstance();
    instance.model = new TypedTableModel();
    instance.titleFormula = (FormulaExpression) titleFormula.getInstance();
    instance.pageFunction = (PageFunction) pageFunction.getInstance();
    instance.groups = (ArrayList<String>) groups.clone();
    instance.groupCount = null;
    instance.groupValues = null;
    return instance;
  }
}
