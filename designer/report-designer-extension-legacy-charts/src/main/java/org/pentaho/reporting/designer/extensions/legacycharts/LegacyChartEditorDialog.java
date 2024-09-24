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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.expressions.DynamicExpressionTableModel;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionPropertiesTableModel;
import org.pentaho.reporting.designer.core.util.ActionToggleButton;
import org.pentaho.reporting.designer.core.util.ExpressionListCellRenderer;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Locale;

public class LegacyChartEditorDialog extends CommonDialog {
  private class SelectChartExpressionAction extends AbstractAction implements ListDataListener {
    private Class expressionType;
    private Icon standardIcon;
    private Icon selectedIcon;

    private SelectChartExpressionAction( final Class expressionType ) {
      this.expressionType = expressionType;

      final ExpressionRegistry expressionRegistry = ExpressionRegistry.getInstance();
      final ExpressionMetaData metaData = expressionRegistry.getExpressionMetaData( expressionType.getName() );
      putValue( Action.NAME, metaData.getMetaAttribute( "short-name", Locale.getDefault() ) ); // NON-NLS

      final String defaultIcon = metaData.getMetaAttribute( "icon", Locale.getDefault() ); // NON-NLS
      if ( defaultIcon != null ) {
        final URL defaultIconUrl = LegacyChartEditorDialog.class.getResource( defaultIcon );
        if ( defaultIconUrl != null ) {
          standardIcon = new ImageIcon( defaultIconUrl );
          putValue( Action.SMALL_ICON, standardIcon );
        }
      }

      final String selectedIconProperty = metaData.getMetaAttribute( "selected-icon", Locale.getDefault() ); // NON-NLS
      if ( selectedIconProperty != null ) {
        final URL selectedIconUrl = LegacyChartEditorDialog.class.getResource( selectedIconProperty );
        if ( selectedIconUrl != null ) {
          selectedIcon = new ImageIcon( selectedIconUrl );
        }
      }
    }

    public void actionPerformed( final ActionEvent e ) {
      final ExpressionMetaData data =
        ExpressionRegistry.getInstance().getExpressionMetaData( expressionType.getName() );
      editModel.getChartExpressionsModel().setSelectedItem( data );
    }

    public void intervalAdded( final ListDataEvent e ) {
      // ignore
    }

    public void intervalRemoved( final ListDataEvent e ) {
      // ignore
    }

    public void contentsChanged( final ListDataEvent e ) {
      final ExpressionMetaData o = (ExpressionMetaData) editModel.getChartExpressionsModel().getSelectedItem();
      if ( o != null && expressionType.equals( o.getExpressionType() ) ) {
        putValue( "selected", Boolean.TRUE ); // NON-NLS
        putValue( Action.SMALL_ICON, selectedIcon );
      } else {
        putValue( "selected", Boolean.FALSE ); // NON-NLS
        putValue( Action.SMALL_ICON, standardIcon );
      }
    }
  }

  private class ChartExpressionChangeHandler implements PropertyChangeListener {
    private ChartExpressionChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( LegacyChartEditModel.CHART_EXPRESSION_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      final Expression o = (Expression) evt.getNewValue();
      if ( o == null ) {
        chartPropertiesTableModel.setData( EMPTY_EXPRESSION );

        dataSourceTabbedPane.setEnabledAt( 1, false );
        dataSourceTabbedPane.setSelectedIndex( 0 );
        return;
      }

      chartPropertiesTableModel.setData( new Expression[] { o } );

      final ChartType type = ChartType.getTypeByChartExpression( o.getClass() );
      if ( type == null ) {
        dataSourceTabbedPane.setEnabledAt( 1, false );
        dataSourceTabbedPane.setSelectedIndex( 0 );
        return;
      }

      dataSourceTabbedPane.setEnabledAt( 1, type.getSecondaryDataSourceProperty() != null );
    }
  }


  private class PrimaryDataSourceChangeHandler implements PropertyChangeListener {
    private PrimaryDataSourceChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( LegacyChartEditModel.PRIMARY_DATA_SOURCE_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      final Expression o = (Expression) evt.getNewValue();
      if ( o == null ) {
        primaryDataSourcePropertiesTableModel.setData( EMPTY_EXPRESSION );
        return;
      }

      primaryDataSourcePropertiesTableModel.setData( new Expression[] { o } );
    }
  }


  private class SecondaryDataSourceChangeHandler implements PropertyChangeListener {
    private SecondaryDataSourceChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( LegacyChartEditModel.SECONDARY_DATA_SOURCE_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      final Expression o = (Expression) evt.getNewValue();
      if ( o == null ) {
        secondaryDataSourcePropertiesTableModel.setData( EMPTY_EXPRESSION );
        return;
      }

      secondaryDataSourcePropertiesTableModel.setData( new Expression[] { o } );
    }
  }

  protected static class ChartExpressionPropertiesTableModel extends ExpressionPropertiesTableModel {
    public ChartExpressionPropertiesTableModel() {
      setFilterInlineExpressionProperty( true );
    }

    protected boolean isFiltered( final ExpressionPropertyMetaData metaData ) {
      if ( "linesDataSource".equals( metaData.getName() ) ) // NON-NLS
      {
        return true;
      }
      if ( "secondaryDataSet".equals( metaData.getName() ) ) // NON-NLS
      {
        return true;
      }
      if ( "dataSource".equals( metaData.getName() ) ) // NON-NLS
      {
        return true;
      }
      return super.isFiltered( metaData );
    }
  }

  private ExpressionPropertiesTableModel chartPropertiesTableModel;
  private ExpressionPropertiesTableModel primaryDataSourcePropertiesTableModel;
  private ExpressionPropertiesTableModel secondaryDataSourcePropertiesTableModel;
  private JTabbedPane dataSourceTabbedPane;
  private ElementMetaDataTable chartTable;
  private ElementMetaDataTable primaryDataSourceTable;
  private ElementMetaDataTable secondaryDataSourceTable;
  private LegacyChartEditModel editModel;
  private static final Expression[] EMPTY_EXPRESSION = new Expression[ 0 ];

  public LegacyChartEditorDialog() throws HeadlessException {
    init();
  }

  public LegacyChartEditorDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public LegacyChartEditorDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }


  protected void init() {
    setTitle( Messages.getInstance().getString( "ChartEditorTitle" ) );

    editModel = new LegacyChartEditModel();
    editModel
      .addPropertyChangeListener( LegacyChartEditModel.CHART_EXPRESSION_PROPERTY, new ChartExpressionChangeHandler() );
    editModel.addPropertyChangeListener( LegacyChartEditModel.PRIMARY_DATA_SOURCE_PROPERTY,
      new PrimaryDataSourceChangeHandler() );
    editModel.addPropertyChangeListener( LegacyChartEditModel.SECONDARY_DATA_SOURCE_PROPERTY,
      new SecondaryDataSourceChangeHandler() );

    chartTable = new ElementMetaDataTable();
    chartPropertiesTableModel = new DynamicExpressionTableModel();

    primaryDataSourceTable = new ElementMetaDataTable();
    primaryDataSourcePropertiesTableModel = new ExpressionPropertiesTableModel();
    primaryDataSourcePropertiesTableModel.setFilterInlineExpressionProperty( true );

    secondaryDataSourceTable = new ElementMetaDataTable();
    secondaryDataSourcePropertiesTableModel = new ExpressionPropertiesTableModel();
    secondaryDataSourcePropertiesTableModel.setFilterInlineExpressionProperty( true );

    dataSourceTabbedPane = new JTabbedPane();
    dataSourceTabbedPane.add( Messages.getInstance().getString( "PrimaryDataSource" ), createPrimaryDataSourcePanel() );
    dataSourceTabbedPane
      .add( Messages.getInstance().getString( "SecondaryDataSource" ), createSecondaryDataSourcePanel() );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Chart.LegacyChartEditor";
  }

  protected Component createContentPane() {
    final JSplitPane expressionsPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
    expressionsPane.setLeftComponent( createChartPanel() );
    expressionsPane.setRightComponent( dataSourceTabbedPane );

    final JSplitPane previewPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
    previewPane.setBottomComponent( expressionsPane );
    final JPanel contentPane = new JPanel();
    final Border border = contentPane.getBorder();
    final Border margin = new EmptyBorder( 5, 20, 0, 0 );
    contentPane.setBorder( new CompoundBorder( border, margin ) );
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( createChartSelectorButtonPane(), BorderLayout.NORTH );
    contentPane.add( previewPane, BorderLayout.CENTER );

    previewPane.setDividerLocation( 300 );
    expressionsPane.setDividerLocation( 350 );

    return contentPane;
  }

  private JPanel createSecondaryDataSourcePanel() {
    final JPanel innerSecondaryDataSourcePanel = new JPanel( new BorderLayout() );
    innerSecondaryDataSourcePanel.add( new SortHeaderPanel( secondaryDataSourcePropertiesTableModel ),
      BorderLayout.NORTH );
    final JComboBox comboBox = new SmartComboBox( editModel.getSecondaryDataSourcesModel() );
    comboBox.setRenderer( new ExpressionListCellRenderer() );
    innerSecondaryDataSourcePanel.add( comboBox, BorderLayout.CENTER );

    final JPanel secondaryDataSourcePanel = new JPanel( new BorderLayout() );
    secondaryDataSourcePanel.add( innerSecondaryDataSourcePanel, BorderLayout.NORTH );
    secondaryDataSourcePanel.add( configureExpressionTable( secondaryDataSourceTable,
      secondaryDataSourcePropertiesTableModel ), BorderLayout.CENTER );
    return secondaryDataSourcePanel;
  }

  private JPanel createPrimaryDataSourcePanel() {
    final JPanel innerPrimaryDataSourcePanel = new JPanel( new BorderLayout() );
    innerPrimaryDataSourcePanel.add( new SortHeaderPanel( primaryDataSourcePropertiesTableModel ), BorderLayout.NORTH );
    final JComboBox comboBox = new SmartComboBox( editModel.getPrimaryDataSourcesModel() );
    comboBox.setRenderer( new ExpressionListCellRenderer() );
    innerPrimaryDataSourcePanel.add( comboBox, BorderLayout.CENTER );

    final JPanel primaryDataSourcePanel = new JPanel( new BorderLayout() );
    primaryDataSourcePanel.add( innerPrimaryDataSourcePanel, BorderLayout.NORTH );
    primaryDataSourcePanel
      .add( configureExpressionTable( primaryDataSourceTable, primaryDataSourcePropertiesTableModel ),
        BorderLayout.CENTER );
    return primaryDataSourcePanel;
  }

  private JPanel createChartPanel() {
    final JPanel innerChartExpressionPanel = new JPanel( new BorderLayout() );
    innerChartExpressionPanel.add( new SortHeaderPanel( chartPropertiesTableModel ), BorderLayout.NORTH );
    final JComboBox comboBox = new SmartComboBox( editModel.getChartExpressionsModel() );
    comboBox.setRenderer( new ExpressionListCellRenderer() );
    innerChartExpressionPanel.add( comboBox, BorderLayout.CENTER );

    final JPanel chartExpressionPanel = new JPanel( new BorderLayout() );
    chartExpressionPanel.add( innerChartExpressionPanel, BorderLayout.NORTH );
    chartExpressionPanel.add
      ( configureExpressionTable( chartTable, chartPropertiesTableModel ), BorderLayout.CENTER );
    return chartExpressionPanel;
  }

  private JComponent configureExpressionTable( final ElementMetaDataTable table,
                                               final GroupingModel dataModel ) {
    table.setModel( new GroupedMetaTableModel( dataModel ) );
    return new JScrollPane( table );
  }

  private Component createChartSelectorButtonPane() {
    final ChartType[] types = ChartType.values();
    final ButtonGroup buttonGroup = new ButtonGroup();
    final JPanel buttonCarrier = new JPanel();
    buttonCarrier.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );

    for ( int i = 0; i < types.length; i++ ) {
      final ChartType type = types[ i ];

      final SelectChartExpressionAction action = new SelectChartExpressionAction( type.getExpressionType() );
      editModel.getChartExpressionsModel().addListDataListener( action );

      final ActionToggleButton button = new ActionToggleButton();
      button.putClientProperty( "hideActionText", Boolean.TRUE ); // NON-NLS
      button.setAction( action );
      button.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
      buttonGroup.add( button );
      buttonCarrier.add( button );
    }

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( buttonCarrier, BorderLayout.NORTH );
    return panel;
  }

  public ChartEditingResult performEdit( final Element element,
                                         final ReportDesignerContext reportDesignerContext )
    throws CloneNotSupportedException {
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( reportDesignerContext == null ) {
      throw new NullPointerException();
    }
    if ( LegacyChartsUtil.isLegacyChartElement( element ) == false ) {
      return null;
    }
    try {
      chartTable.setReportDesignerContext( reportDesignerContext );
      primaryDataSourceTable.setReportDesignerContext( reportDesignerContext );
      secondaryDataSourceTable.setReportDesignerContext( reportDesignerContext );

      chartPropertiesTableModel.setActiveContext( reportDesignerContext.getActiveContext() );
      primaryDataSourcePropertiesTableModel.setActiveContext( reportDesignerContext.getActiveContext() );
      secondaryDataSourcePropertiesTableModel.setActiveContext( reportDesignerContext.getActiveContext() );

      final Element editableElement = element.derive();
      final Expression chartExpression =
        editableElement.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );

      final Expression originalPrimaryDataSourceExpression;
      final Expression originalSecondaryDataSourceExpression;
      if ( chartExpression != null ) {
        originalPrimaryDataSourceExpression = extractPrimaryDatasource( element );
        originalSecondaryDataSourceExpression = extractSecondaryDatasource( element );

        editModel.setChartExpression( chartExpression.getInstance() );
        if ( originalPrimaryDataSourceExpression != null ) {
          editModel.setPrimaryDataSource( originalPrimaryDataSourceExpression.getInstance() );
        } else {
          editModel.setPrimaryDataSource( null );
        }

        if ( originalSecondaryDataSourceExpression != null ) {
          editModel.setSecondaryDataSource( originalSecondaryDataSourceExpression.getInstance() );
        } else {
          editModel.setSecondaryDataSource( null );
        }

      } else {
        editModel.setChartExpression( null );
        editModel.setPrimaryDataSource( null );
        editModel.setSecondaryDataSource( null );
        originalPrimaryDataSourceExpression = null;
        originalSecondaryDataSourceExpression = null;
      }

      if ( editModel.getCurrentChartType() != null ) {
        final ChartType chartType = editModel.getCurrentChartType();
        if ( editModel.getPrimaryDataSource() == null ) {
          final Class dataSourceImplementation = chartType.getPreferredPrimaryDataSourceImplementation();
          final ExpressionMetaData data =
            ExpressionRegistry.getInstance().getExpressionMetaData( dataSourceImplementation.getName() );
          editModel.getPrimaryDataSourcesModel().setSelectedItem( data );
        }
        if ( editModel.getSecondaryDataSource() == null ) {
          final Class dataSourceImplementation = chartType.getPreferredSecondaryDataSourceImplementation();
          if ( dataSourceImplementation != null ) {
            final ExpressionMetaData data =
              ExpressionRegistry.getInstance().getExpressionMetaData( dataSourceImplementation.getName() );
            editModel.getSecondaryDataSourcesModel().setSelectedItem( data );
          }
        }
      }

      if ( performEdit() == false ) {
        return null;
      }

      secondaryDataSourceTable.stopEditing();
      primaryDataSourceTable.stopEditing();
      chartTable.stopEditing();

      return new ChartEditingResult
        ( chartExpression, originalPrimaryDataSourceExpression, originalSecondaryDataSourceExpression,
          editModel.getChartExpression(), editModel.getPrimaryDataSource(), editModel.getSecondaryDataSource() );
    } finally {
      chartTable.setReportDesignerContext( null );
      primaryDataSourceTable.setReportDesignerContext( null );
      secondaryDataSourceTable.setReportDesignerContext( null );

      chartPropertiesTableModel.setActiveContext( null );
      primaryDataSourcePropertiesTableModel.setActiveContext( null );
      secondaryDataSourcePropertiesTableModel.setActiveContext( null );
    }
  }

  private Expression extractSecondaryDatasource( final Element element ) {
    final Expression originalSecondaryDataSourceExpression;
    final Object secondaryDataSource = element.getAttribute
      ( LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.SECONDARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE );
    if ( secondaryDataSource instanceof Expression ) {
      originalSecondaryDataSourceExpression = (Expression) secondaryDataSource;
    } else {
      originalSecondaryDataSourceExpression = null;
    }
    return originalSecondaryDataSourceExpression;
  }

  private Expression extractPrimaryDatasource( final Element element ) {
    final Expression originalPrimaryDataSourceExpression;
    final Object primaryDataSourceRaw = element.getAttribute
      ( LegacyChartElementModule.NAMESPACE, LegacyChartElementModule.PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE );
    if ( primaryDataSourceRaw instanceof Expression ) {
      originalPrimaryDataSourceExpression = (Expression) primaryDataSourceRaw;
    } else {
      originalPrimaryDataSourceExpression = null;
    }
    return originalPrimaryDataSourceExpression;
  }
}
