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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.jfree.chart.JFreeChart;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

public class LegacyChartEditModel {

  private class PrimaryDataSourceSelectionHandler implements ListDataListener {
    private PrimaryDataSourceSelectionHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {
      // ignored
    }

    public void intervalRemoved( final ListDataEvent e ) {
      // ignored
    }

    public void contentsChanged( final ListDataEvent e ) {
      final ExpressionMetaData o = (ExpressionMetaData) getPrimaryDataSourcesModel().getSelectedItem();
      if ( o == null ) {
        setPrimaryDataSource( null );
        return;
      }
      final Expression primaryDataSourceExpression = getPrimaryDataSource();
      if ( primaryDataSourceExpression != null && primaryDataSourceExpression.getClass()
        .equals( o.getExpressionType() ) ) {
        // no need to change anything ..
        return;
      }

      try {
        final Expression expression = (Expression) o.getExpressionType().newInstance();
        if ( primaryDataSourceExpression != null ) {
          propagateExpressionSettings( primaryDataSourceExpression, expression );
        }

        setPrimaryDataSource( expression.getInstance() );
      } catch ( final Exception e1 ) {
        // ignore the exception ..
        UncaughtExceptionsModel.getInstance().addException( e1 );
        setPrimaryDataSource( null );
      }
    }
  }

  private class SecondaryDataSourceSelectionHandler implements ListDataListener {
    private SecondaryDataSourceSelectionHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {
      // ignored
    }

    public void intervalRemoved( final ListDataEvent e ) {
      // ignored
    }

    public void contentsChanged( final ListDataEvent e ) {

      final ExpressionMetaData o = (ExpressionMetaData) getSecondaryDataSourcesModel().getSelectedItem();
      if ( o == null ) {
        setSecondaryDataSource( null );
        return;
      }

      final Expression secondaryDataSourceExpression = getSecondaryDataSource();
      if ( secondaryDataSourceExpression != null && secondaryDataSourceExpression.getClass()
        .equals( o.getExpressionType() ) ) {
        // no need to change anything ..
        return;
      }

      try {
        final Expression expression = (Expression) o.getExpressionType().newInstance();
        if ( secondaryDataSourceExpression != null ) {
          propagateExpressionSettings( secondaryDataSourceExpression, expression );
        }
        setSecondaryDataSource( expression.getInstance() );
      } catch ( final Exception e1 ) {
        // ignore the exception ..
        UncaughtExceptionsModel.getInstance().addException( e1 );
        setSecondaryDataSource( null );
      }
    }
  }


  private class ChartExpressionTypeSelectionHandler implements ListDataListener {
    private ChartExpressionTypeSelectionHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {
      // ignored
    }

    public void intervalRemoved( final ListDataEvent e ) {
      // ignored
    }

    public void contentsChanged( final ListDataEvent e ) {
      final ExpressionMetaData o = (ExpressionMetaData) getChartExpressionsModel().getSelectedItem();
      if ( o == null ) {
        setChartExpression( null );
        return;
      }

      final Expression chartExpression = getChartExpression();
      if ( chartExpression != null && chartExpression.getClass().equals( o.getExpressionType() ) ) {
        // no need to change anything ..
        return;
      }

      try {
        final Expression primaryDSExpression = getPrimaryDataSource();
        final Expression secondaryDSExpression = getSecondaryDataSource();

        final Expression newChartExpression = (Expression) o.getExpressionType().newInstance();
        propagateExpressionSettings( chartExpression, newChartExpression );
        setChartExpression( newChartExpression );

        final ExpressionMetaData thePrimaryModel = (ExpressionMetaData) getPrimaryDataSourcesModel().getSelectedItem();
        if ( thePrimaryModel != null && primaryDSExpression != null ) {
          if ( thePrimaryModel.getExpressionType().equals( primaryDSExpression.getClass() ) ) {
            setPrimaryDataSource( primaryDSExpression );
          } else {
            final Expression newPrimaryDataSource = (Expression) thePrimaryModel.getExpressionType().newInstance();
            propagateExpressionSettings( primaryDSExpression, newPrimaryDataSource );
            setPrimaryDataSource( newPrimaryDataSource );
          }
        }

        final ExpressionMetaData theSecondaryModel =
          (ExpressionMetaData) getSecondaryDataSourcesModel().getSelectedItem();
        if ( theSecondaryModel != null && secondaryDSExpression != null ) {
          if ( theSecondaryModel.getExpressionType().equals( secondaryDSExpression.getClass() ) ) {
            setSecondaryDataSource( secondaryDSExpression );
          } else {
            final Expression newSecondaryDataSource = (Expression) theSecondaryModel.getExpressionType().newInstance();
            propagateExpressionSettings( secondaryDSExpression, newSecondaryDataSource );
            setSecondaryDataSource( newSecondaryDataSource );
          }
        }
      } catch ( final Exception e1 ) {
        // ignore the exception ..
        UncaughtExceptionsModel.getInstance().addException( e1 );
        setChartExpression( null );
      }
    }
  }


  public static final String PRIMARY_DATA_SOURCE_PROPERTY = "primaryDataSource";
  public static final String SECONDARY_DATA_SOURCE_PROPERTY = "secondaryDataSource";
  public static final String CHART_EXPRESSION_PROPERTY = "chartExpression";

  private PropertyChangeSupport propertyChangeSupport;
  private Expression chartExpression;
  private Expression primaryDataSource;
  private Expression secondaryDataSource;
  private ChartType currentChartType;
  private DefaultComboBoxModel primaryDataSourcesModel;
  private DefaultComboBoxModel secondaryDataSourcesModel;
  private DefaultComboBoxModel chartExpressionsModel;

  public LegacyChartEditModel() {
    propertyChangeSupport = new PropertyChangeSupport( this );

    chartExpressionsModel = new DefaultComboBoxModel();
    chartExpressionsModel.addListDataListener( new ChartExpressionTypeSelectionHandler() );
    primaryDataSourcesModel = new DefaultComboBoxModel();
    primaryDataSourcesModel.addListDataListener( new PrimaryDataSourceSelectionHandler() );
    secondaryDataSourcesModel = new DefaultComboBoxModel();
    secondaryDataSourcesModel.addListDataListener( new SecondaryDataSourceSelectionHandler() );

    populateExpressionSelectorModel( chartExpressionsModel, JFreeChart.class );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public Expression getChartExpression() {
    return chartExpression;
  }

  public void setChartExpression( final Expression chartExpression ) {
    final Expression oldChartExpression = this.chartExpression;
    this.chartExpression = chartExpression;
    this.currentChartType = null;
    propertyChangeSupport.firePropertyChange( CHART_EXPRESSION_PROPERTY, oldChartExpression, chartExpression );

    if ( this.chartExpression != null ) {
      final String key = this.chartExpression.getClass().getName();
      final ExpressionMetaData newMetaData = ExpressionRegistry.getInstance().getExpressionMetaData( key );
      if ( newMetaData == null ) {
        chartExpressionsModel.setSelectedItem( null );
      } else {
        final Object selectedMetaData = chartExpressionsModel.getSelectedItem();
        if ( selectedMetaData instanceof ExpressionMetaData ) {
          final ExpressionMetaData metaData = (ExpressionMetaData) selectedMetaData;
          if ( metaData.getExpressionType().equals( newMetaData.getExpressionType() ) == false ) {
            chartExpressionsModel.setSelectedItem( newMetaData );
          }
        } else {
          chartExpressionsModel.setSelectedItem( newMetaData );
        }
      }
    } else {
      chartExpressionsModel.setSelectedItem( null );
    }

    updateExpressionDataSources();
  }

  private void updateExpressionDataSources() {
    final ChartType type = getCurrentChartType();
    if ( type == null ) {
      populateExpressionSelectorModel( primaryDataSourcesModel, null );
      populateExpressionSelectorModel( secondaryDataSourcesModel, null );
      setPrimaryDataSource( null );
      setSecondaryDataSource( null );
      return;
    }

    final ChartDataSource datasource = type.getDatasource();
    if ( datasource != null ) {
      final Expression primaryDataSource = getPrimaryDataSource();
      if ( isValidType( primaryDataSource, datasource.getResultType() ) == false ) {
        populateExpressionSelectorModel( primaryDataSourcesModel, datasource.getResultType() );
        final ExpressionMetaData data =
          ExpressionRegistry.getInstance().getExpressionMetaData
            ( type.getPreferredPrimaryDataSourceImplementation().getName() );
        primaryDataSourcesModel.setSelectedItem( data );
      }
    } else {
      populateExpressionSelectorModel( primaryDataSourcesModel, null );
      setPrimaryDataSource( null );
    }

    final ChartDataSource secondaryDataSource = type.getSecondaryDataSource();
    if ( secondaryDataSource != null ) {
      final Expression dataSource = getSecondaryDataSource();
      if ( isValidType( dataSource, secondaryDataSource.getResultType() ) == false ) {
        populateExpressionSelectorModel( secondaryDataSourcesModel, secondaryDataSource.getResultType() );
        final ExpressionMetaData data =
          ExpressionRegistry.getInstance().getExpressionMetaData
            ( type.getPreferredSecondaryDataSourceImplementation().getName() );
        secondaryDataSourcesModel.setSelectedItem( data );
      }
    } else {
      populateExpressionSelectorModel( secondaryDataSourcesModel, null );
      setSecondaryDataSource( null );
    }
  }

  private boolean isValidType( final Expression expression, final Class resultType ) {
    if ( expression == null ) {
      return false;
    }
    if ( ExpressionRegistry.getInstance().isExpressionRegistered( expression.getClass().getName() ) == false ) {
      return false;
    }

    final ExpressionMetaData metaData =
      ExpressionRegistry.getInstance().getExpressionMetaData( expression.getClass().getName() );
    if ( resultType.isAssignableFrom( metaData.getResultType() ) ) {
      return true;
    }
    return false;
  }

  public Expression getPrimaryDataSource() {
    return primaryDataSource;
  }

  public void setPrimaryDataSource( final Expression primaryDataSource ) {
    final Expression oldExpression = this.primaryDataSource;
    this.primaryDataSource = primaryDataSource;

    propertyChangeSupport.firePropertyChange( PRIMARY_DATA_SOURCE_PROPERTY, oldExpression, primaryDataSource );

    if ( primaryDataSource != null ) {
      final ExpressionMetaData data =
        ExpressionRegistry.getInstance().getExpressionMetaData( primaryDataSource.getClass().getName() );
      primaryDataSourcesModel.setSelectedItem( data );
    } else {
      primaryDataSourcesModel.setSelectedItem( null );
    }
  }

  public Expression getSecondaryDataSource() {
    return secondaryDataSource;
  }

  public void setSecondaryDataSource( final Expression secondaryDataSource ) {
    final Expression oldExpression = this.secondaryDataSource;
    this.secondaryDataSource = secondaryDataSource;
    propertyChangeSupport.firePropertyChange( SECONDARY_DATA_SOURCE_PROPERTY, oldExpression, secondaryDataSource );

    if ( secondaryDataSource != null ) {
      final ExpressionMetaData data =
        ExpressionRegistry.getInstance().getExpressionMetaData( secondaryDataSource.getClass().getName() );
      secondaryDataSourcesModel.setSelectedItem( data );
    } else {
      secondaryDataSourcesModel.setSelectedItem( null );
    }
  }

  public ChartType getCurrentChartType() {
    if ( currentChartType == null && chartExpression != null ) {
      currentChartType = ChartType.getTypeByChartExpression( chartExpression.getClass() );
    }
    return currentChartType;
  }

  public ComboBoxModel getPrimaryDataSourcesModel() {
    return primaryDataSourcesModel;
  }

  public ComboBoxModel getSecondaryDataSourcesModel() {
    return secondaryDataSourcesModel;
  }

  public ComboBoxModel getChartExpressionsModel() {
    return chartExpressionsModel;
  }

  private void propagateExpressionSettings( final Expression source,
                                            final Expression destination ) {
    if ( source == null || destination == null ) {
      return;
    }

    try {
      final BeanUtility buSource = new BeanUtility( source );
      final BeanUtility buDest = new BeanUtility( destination );
      final String[] strings = buSource.getProperties();
      for ( int i = 0; i < strings.length; i++ ) {
        try {
          final String propertyName = strings[ i ];
          final Object value = buSource.getProperty( propertyName );
          buDest.setProperty( propertyName, value );
        } catch ( BeanException e ) {
          // ignore ..
        }
      }
    } catch ( Exception e ) {
      // ignore ..
    }
/*
// We do not need to set a name, names get autogenerated later ..
    final String name = source.getName();
    if (name == null)
    {
      final ExpressionCollection expressionCollection = activeContext.getReportDefinition().getExpressions();
      final int expressionIndex = expressionCollection.size() + 1;
      destination.setName("Chart Function " + expressionIndex);
    }
    else
    {
      destination.setName(name);
    }
*/
  }

  private void populateExpressionSelectorModel( final DefaultComboBoxModel model, final Class resultType ) {
    model.removeAllElements();
    if ( resultType == null ) {
      model.setSelectedItem( null );
      return;
    }

    final ExpressionMetaData[] allExpressionMetaDatas = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    Arrays.sort( allExpressionMetaDatas, new GroupedMetaDataComparator() );
    for ( int i = 0; i < allExpressionMetaDatas.length; i++ ) {
      final ExpressionMetaData data = allExpressionMetaDatas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }
      if ( StructureFunction.class.isAssignableFrom( data.getExpressionType() ) ) {
        continue;
      }

      if ( resultType.isAssignableFrom( data.getResultType() ) ) {
        model.addElement( data );
      }
    }

    model.setSelectedItem( null );
  }

}
