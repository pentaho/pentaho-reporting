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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshEvent;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshListener;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiException;
import org.pentaho.reporting.designer.core.editor.drilldown.Messages;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.DrillDownModelWrapper;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Swing version of the self-drilldown.xul dialog.
 */
public class SwingSelfDrillDownUi implements DrillDownUi {

  /** Main panel of the dialog. */
  private JPanel editor;

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
    final int layoutIndent = 0;

    editor = new JPanel();
    editor.setLayout( layout );

    table = new DrillDownParameterTable();
    table.setTitle( Messages.getString( "DrillDownDialog.ParameterTable.Title" ) ); // NON-NLS
    table.setShowRefreshButton( false );
    table.setAllowCustomParameter( true );
    table.setSingleTabMode( true );
    table.setShowHideParameterUiCheckbox( false );
    table.addDrillDownParameterRefreshListener( new UpdateParametersHandler() );
    table.addPropertyChangeListener( DrillDownParameterTable.DRILL_DOWN_PARAMETER_PROPERTY, new TableModelBinding() );

    editor.add( table );
    layout.putConstraint( SpringLayout.NORTH, table, layoutIndent, SpringLayout.NORTH, editor );
    layout.putConstraint( SpringLayout.EAST, table, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.SOUTH, table, layoutIndent, SpringLayout.SOUTH, editor );
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
    model.setDrillDownConfig( SwingSelfDrillDownUiProfile.NAME );

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
      wrapper.setDrillDownParameter( filterParameter( table.getDrillDownParameter() ) );
    }

    private DrillDownParameter[] filterParameter( final DrillDownParameter[] parameter ) {
      final ArrayList<DrillDownParameter> list = new ArrayList<DrillDownParameter>( parameter.length );
      for ( int i = 0; i < parameter.length; i++ ) {
        final DrillDownParameter downParameter = parameter[ i ];
        if ( StringUtils.isEmpty( downParameter.getFormulaFragment() ) ) {
          continue;
        }

        list.add( downParameter );
      }
      return list.toArray( new DrillDownParameter[ list.size() ] );
    }

  }

  /**
   * ParameterRefreshListener for drill down parameter table.
   */
  private class UpdateParametersHandler implements DrillDownParameterRefreshListener {
    private UpdateParametersHandler() {
    }

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
