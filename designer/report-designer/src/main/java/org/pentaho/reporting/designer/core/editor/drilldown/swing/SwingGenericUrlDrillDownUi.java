/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.Messages;
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
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Swing version of the generic-url-drilldown.xul dialog.
 */
public class SwingGenericUrlDrillDownUi implements DrillDownUi {

  /** Main panel of the dialog. */
  private JPanel editor;

  /** Tooltip and Target panel. */
  private TooltipAndTargetPanel tatPanel;

  /** Path field. */
  private JTextField pathField;

  /** Table for parameters input. */
  private DrillDownParameterTable table;

  /** Wrapper for drill down model. */
  private DrillDownModelWrapper wrapper;

  /** Current active render-context holder. */
  private ReportDesignerContext reportDesignerContext;

  private PathChangeHandler pathHandler;

  /**
   * Create Swing version of the generic-url-drilldown.xul dialog.
   */
  public SwingGenericUrlDrillDownUi() {
    SpringLayout layout = new SpringLayout();
    final int layoutIndent = 2;

    editor = new JPanel();
    editor.setLayout( layout );

    /** "Path:" label */
    JLabel pathLabel = new JLabel( Messages.getString( "DrillDownDialog.PathInput.Label" ) );
    editor.add( pathLabel );
    layout.putConstraint( SpringLayout.NORTH, pathLabel, layoutIndent, SpringLayout.NORTH, editor );
    layout.putConstraint( SpringLayout.EAST, pathLabel, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, pathLabel, layoutIndent, SpringLayout.WEST, editor );

    /** "Path:" input field */
    pathField = new JTextField();
    pathField.getDocument().addDocumentListener( new DocumentBindingListener() {
      @Override
      protected void setData( String data ) {
        getModel().setDrillDownPath( data );
      }
    } );
    editor.add( pathField );
    layout.putConstraint( SpringLayout.NORTH, pathField, layoutIndent, SpringLayout.SOUTH, pathLabel );
    layout.putConstraint( SpringLayout.EAST, pathField, 0, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, pathField, layoutIndent, SpringLayout.WEST, editor );

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
    layout.putConstraint( SpringLayout.NORTH, tatPanel, layoutIndent, SpringLayout.SOUTH, pathField );
    layout.putConstraint( SpringLayout.EAST, tatPanel, 0, SpringLayout.EAST, editor );
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
    wrapper = new DrillDownModelWrapper( model );
    model.setDrillDownConfig( SwingGenericUrlDrillDownUiProfile.NAME_DEFAULT );
    pathHandler = new PathChangeHandler();
    getModel().addPropertyChangeListener( DrillDownModel.DRILL_DOWN_PATH_PROPERTY, pathHandler );

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
    if ( getModel().getDrillDownPath() != null ) {
      pathField.setText( getModel().getDrillDownPath() );
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
    getModel().removePropertyChangeListener( DrillDownModel.DRILL_DOWN_PATH_PROPERTY, pathHandler );
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

  private class PathChangeHandler implements PropertyChangeListener {

    /**
     * {@inheritDoc}
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      final String path = getModel().getDrillDownPath();
      if ( StringUtils.isEmpty( path ) ) {
        return;
      }
      if ( path.matches( "\\w+\\://.*" ) ) { //NON-NLS
        getModel().setDrillDownConfig( "generic-url" ); //NON-NLS
      } else {
        getModel().setDrillDownConfig( "local-url" ); //NON-NLS
      }
    }
  }

}
