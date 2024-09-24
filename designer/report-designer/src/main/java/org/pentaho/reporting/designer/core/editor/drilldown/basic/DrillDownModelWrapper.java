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

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.ui.xul.XulEventSource;

import java.beans.PropertyChangeListener;

public class DrillDownModelWrapper implements XulEventSource {
  private DrillDownModel model;

  public DrillDownModelWrapper( final DrillDownModel model ) {
    if ( model == null ) {
      throw new NullPointerException();
    }
    this.model = model;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    model.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    model.removePropertyChangeListener( listener );
  }

  public void setDrillDownConfig( final String drillDownConfig ) {
    model.setDrillDownConfig( drillDownConfig );
    model.firePropertyChange( "preview", null, getPreview() );
  }

  public String getDrillDownConfig() {
    return model.getDrillDownConfig();
  }

  public String getDrillDownPath() {
    return model.getDrillDownPath();
  }

  public String getTooltipFormula() {
    return model.getTooltipFormula();
  }

  public void setTooltipFormula( final String tooltipFormula ) {
    model.setTooltipFormula( tooltipFormula );
  }

  public String getTargetFormula() {
    return model.getTargetFormula();
  }

  public void setTargetFormula( final String targetFormula ) {
    model.setTargetFormula( targetFormula );
  }

  public void setDrillDownPath( final String drillDownPath ) {
    model.setDrillDownPath( drillDownPath );
    model.firePropertyChange( "preview", null, getPreview() );
  }

  public DrillDownParameter[] getDrillDownParameter() {
    return model.getDrillDownParameter();
  }

  public void setDrillDownParameter( final DrillDownParameter[] drillDownParameters ) {
    model.setDrillDownParameter( drillDownParameters );
    model.firePropertyChange( "preview", null, getPreview() );
  }

  public String getPreview() {
    return model.getDrillDownFormula();
  }

  public void refresh() {
    model.refresh();
    model.firePropertyChange( "preview", null, getPreview() );
  }

  public void clear() {
    model.clear();
    model.firePropertyChange( "preview", null, getPreview() );
  }

  public DrillDownModel getModel() {
    return model;
  }


}
