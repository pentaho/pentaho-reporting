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

package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.dom.Document;

import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class DefaultXulDrillDownController implements XulDrillDownController {
  private class TableModelBinding implements PropertyChangeListener {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      wrapper.setDrillDownParameter( table.getDrillDownParameter() );
    }
  }

  protected class RefreshParameterTask implements Runnable {
    public RefreshParameterTask() {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      refresh();
    }
  }

  private String name;
  private XulDomContainer xulDomContainer;
  private DrillDownParameterTable table;
  private DrillDownModelWrapper wrapper;
  private ReportDesignerContext reportDesignerContext;

  public DefaultXulDrillDownController() {
  }

  protected DrillDownParameter[] filterParameter( final DrillDownParameter[] parameter ) {
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

  public DrillDownModel getModel() {
    return wrapper.getModel();
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    this.name = name;
  }

  public XulDomContainer getXulDomContainer() {
    return xulDomContainer;
  }

  public void setXulDomContainer( final XulDomContainer xulDomContainer ) {
    this.xulDomContainer = xulDomContainer;
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void init( final ReportDesignerContext reportDesignerContext,
                    final DrillDownModel model,
                    final String[] fields ) {
    if ( model == null ) {
      throw new NullPointerException();
    }
    if ( reportDesignerContext == null ) {
      throw new NullPointerException();
    }
    if ( fields == null ) {
      throw new NullPointerException();
    }

    this.reportDesignerContext = reportDesignerContext;

    final Document doc = this.xulDomContainer.getDocumentRoot();
    final DefaultBindingFactory bindingFactory = new DefaultBindingFactory();
    bindingFactory.setDocument( doc );
    bindingFactory.setBindingType( Binding.Type.BI_DIRECTIONAL );
    wrapper = new DrillDownModelWrapper( model );
    final XulComponent pathElement = doc.getElementById( "path" ); //NON-NLS
    if ( pathElement != null ) {
      bindingFactory.createBinding( wrapper, DrillDownModel.DRILL_DOWN_PATH_PROPERTY, "path", "value" ); //NON-NLS
    }
    final XulComponent configElement = doc.getElementById( "config" ); //NON-NLS
    if ( configElement != null ) {
      bindingFactory.createBinding( wrapper, DrillDownModel.DRILL_DOWN_CONFIG_PROPERTY, "config", "value" ); //NON-NLS
    }
    final XulComponent linkTargetElement = doc.getElementById( "link-target" ); //NON-NLS
    if ( linkTargetElement != null ) {
      bindingFactory.createBinding( wrapper, DrillDownModel.TARGET_FORMULA_PROPERTY, "link-target", "value" ); //NON-NLS
    }
    final XulComponent linkTooltipElement = doc.getElementById( "link-tooltip" ); //NON-NLS
    if ( linkTooltipElement != null ) {
      bindingFactory
        .createBinding( wrapper, DrillDownModel.TOOLTIP_FORMULA_PROPERTY, "link-tooltip", "value" ); //NON-NLS
    }
    final XulComponent previewElement = doc.getElementById( "preview" ); //NON-NLS
    if ( previewElement != null ) {
      final BindingFactory singleSourceBinding = new DefaultBindingFactory();
      singleSourceBinding.setBindingType( Binding.Type.ONE_WAY );
      singleSourceBinding.setDocument( doc );
      singleSourceBinding.createBinding( wrapper, "preview", "preview", "value" ); //NON-NLS
    }

    // we manage the binding between the table and the outside world manually
    wrapper.refresh();
    final XulComponent paramTableElement = doc.getElementById( "parameter-table" ); //NON-NLS
    if ( paramTableElement instanceof XulDrillDownParameterTable ) {
      final XulDrillDownParameterTable parameterTable = (XulDrillDownParameterTable) paramTableElement;
      table = parameterTable.getTable();
      table.setExtraFields( fields );
      table.setReportDesignerContext( reportDesignerContext );
      table.setDrillDownParameter( model.getDrillDownParameter() );
      table.setHideParameterUi( model.getDrillDownConfig().endsWith( "-no-parameter" ) );
      table.addPropertyChangeListener( DrillDownParameterTable.DRILL_DOWN_PARAMETER_PROPERTY, new TableModelBinding() );
    }

    if ( model.isLimitedEditor() ) {
      final XulComponent tooltipAndTargetElement = doc.getElementById( "tooltip-and-target-panel" ); //NON-NLS
      if ( tooltipAndTargetElement != null ) {
        tooltipAndTargetElement.setVisible( false );
      }
    }

    SwingUtilities.invokeLater( new RefreshParameterTask() );
  }

  protected DrillDownModelWrapper getWrapper() {
    return wrapper;
  }

  /**
   * A generic way of returning data from event handlers... can we do better than this? Handle return values from
   * invoked methods? possibly?
   *
   * @return any data associated with events that have been executed.
   */
  public Object getData() {
    return getModel();
  }

  /**
   * A generic way of passing data to the event handler. It seems we should maybe accept parameters instead of doing
   * this.
   *
   * @param data any data events may want to operate on.
   */
  public void setData( final Object data ) {

  }

  protected DrillDownParameterTable getTable() {
    return table;
  }

  public void refresh() {
    getTable().refreshParameterData();
  }

  public void deactivate() {

  }
}
