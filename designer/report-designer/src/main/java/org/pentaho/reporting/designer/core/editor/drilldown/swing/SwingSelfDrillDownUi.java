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


package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshEvent;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshListener;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiException;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.DrillDownModelWrapper;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Swing version of the self-drilldown.xul dialog.
 */
public class SwingSelfDrillDownUi implements DrillDownUi {

  /** Main panel of the dialog. */
  private JPanel editor;

  /** Tooltip and Target panel. */
  private TooltipAndTargetPanel tatPanel;

  /** Table for parameters input. */
  private DrillDownParameterTable table;

  /** Wrapper for drill down model. */
  private DrillDownModelWrapper wrapper;

  /** Current active render-context holder. */
  private ReportDesignerContext reportDesignerContext;

  /**
   * Create Swing version of the self-drilldown.xul dialog.
   */
  public SwingSelfDrillDownUi() {
    SpringLayout layout = new SpringLayout();
    final int layoutIndent = 2;

    editor = new JPanel();
    editor.setLayout( layout );

    /** "Target:" and "Tooltip:" input fields */
    tatPanel = new TooltipAndTargetPanel();
    tatPanel.getTargetComboBox().addItemListener( new ItemListener() {
      public void itemStateChanged( final ItemEvent e ) {
        getModel().setTargetFormula( e.getItem().toString() );
      }
    } );
    tatPanel.getTooltipPanel().addPropertyChangeListener( "formula", new PropertyChangeListener() {
      public void propertyChange( final PropertyChangeEvent evt ) {
        getModel().setTooltipFormula( evt.getNewValue().toString() );
      }
    } );
    editor.add( tatPanel );
    layout.putConstraint( SpringLayout.NORTH, tatPanel, layoutIndent, SpringLayout.NORTH, editor );
    layout.putConstraint( SpringLayout.EAST, tatPanel, -layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, tatPanel, layoutIndent, SpringLayout.WEST, editor );

    /** "Parameter:" input table */
    table = new DrillDownParameterTable();
    table.setShowRefreshButton( false );
    table.setAllowCustomParameter( true );
    table.setSingleTabMode( true );
    table.setShowHideParameterUiCheckbox( false );
    table.addDrillDownParameterRefreshListener( new UpdateParametersHandler() );
    table.addPropertyChangeListener( DrillDownParameterTable.DRILL_DOWN_PARAMETER_PROPERTY, new TableModelBinding() );

    editor.add( table );
    layout.putConstraint( SpringLayout.NORTH, table, layoutIndent, SpringLayout.SOUTH, tatPanel );
    layout.putConstraint( SpringLayout.EAST, table, -layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.SOUTH, table, -layoutIndent, SpringLayout.SOUTH, editor );
    layout.putConstraint( SpringLayout.WEST, table, layoutIndent, SpringLayout.WEST, editor );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Component getEditorPanel() {
    return editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DrillDownModel getModel() {
    return wrapper.getModel();
  }

  /**
   * Get wrapper for drill down model.
   *
   * @return DrillDownModelWrapper of this drill down dialog.
   */
  protected DrillDownModelWrapper getWrapper() {
    return wrapper;
  }

  /**
   * Get table for parameters input.
   *
   * @return DrillDownParameterTable of this drill down dialog.
   */
  protected DrillDownParameterTable getTable() {
    return table;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init( Component parent,
                    ReportDesignerContext reportDesignerContext,
                    DrillDownModel model,
                    String[] extraFields
  ) throws DrillDownUiException {
    this.reportDesignerContext = reportDesignerContext;
    this.wrapper = new DrillDownModelWrapper( model );
    model.setDrillDownConfig( SwingSelfDrillDownUiProfile.NAME_DEFAULT );

    // Check model and init default values
    if ( getModel().isLimitedEditor() ) {
      tatPanel.hideContent();
    }
    if ( getModel().getTooltipFormula() != null ) {
      tatPanel.getTooltipPanel().setFormula( getModel().getTooltipFormula() );
    }
    if ( getModel().getTargetFormula() != null ) {
      tatPanel.getTargetComboBox().setSelectedItem( getModel().getTargetFormula() );
    }

    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        getTable().refreshParameterData();
      }
    } );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deactivate() {
    // Nothing to deactivate --Kaa
  }

  /**
   * Get current active render-context holder.
   *
   * @return current active render-context holder.
   */
  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  /**
   * PropertyChangeListener for drill down parameter table.
   */
  private class TableModelBinding implements PropertyChangeListener {

    /**
     * {@inheritDoc}
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      wrapper.setDrillDownParameter( table.getDrillDownParameter() );
    }
  }

  /**
   * ParameterRefreshListener for drill down parameter table.
   */
  private class UpdateParametersHandler implements DrillDownParameterRefreshListener {

    /**
     * {@inheritDoc}
     */
    public void requestParameterRefresh( final DrillDownParameterRefreshEvent event ) {
      final HashMap<String, DrillDownParameter> entries = new HashMap<String, DrillDownParameter>();
      final DrillDownParameter[] originalParams = event.getParameter();
      for ( int i = 0; i < originalParams.length; i++ ) {
        final DrillDownParameter param = originalParams[ i ];
        param.setType( DrillDownParameter.Type.MANUAL );
        entries.put( param.getName(), param );
      }

      final ReportDocumentContext activeContext = getReportDesignerContext().getActiveContext();
      final MasterReport masterReportElement = activeContext.getContextRoot();
      final ReportParameterDefinition reportParams = masterReportElement.getParameterDefinition();
      final ParameterDefinitionEntry[] parameterDefinitionEntries = reportParams.getParameterDefinitions();

      for ( int i = 0; i < parameterDefinitionEntries.length; i++ ) {
        final ParameterDefinitionEntry entry = parameterDefinitionEntries[i];
        if ( entries.containsKey( entry.getName() ) == false ) {
          entries.put( entry.getName(),
                  new DrillDownParameter( entry.getName(), null, DrillDownParameter.Type.PREDEFINED, false, false ) );
        } else {
          final DrillDownParameter parameter = entries.get( entry.getName() );
          parameter.setType( DrillDownParameter.Type.PREDEFINED );
        }
      }

      final DrillDownParameter[] parameters = entries.values().toArray( new DrillDownParameter[ entries.size() ] );
      getWrapper().setDrillDownParameter( parameters );
      getTable().setDrillDownParameter( parameters );
    }
  }
}
