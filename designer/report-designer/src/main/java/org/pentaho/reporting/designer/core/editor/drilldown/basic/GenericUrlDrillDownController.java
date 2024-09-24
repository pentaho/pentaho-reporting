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
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.dom.Document;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GenericUrlDrillDownController extends DefaultXulDrillDownController {
  private class PathChangeHandler implements PropertyChangeListener {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      final String path = getModel().getDrillDownPath();
      if ( StringUtils.isEmpty( path ) ) {
        return;
      }
      if ( path.matches( "\\w+\\://.*" ) )//NON-NLS
      {
        getModel().setDrillDownConfig( "generic-url" );//NON-NLS
      } else {
        getModel().setDrillDownConfig( "local-url" );//NON-NLS
      }
    }
  }


  private class CheckEmptyPathHandler implements PropertyChangeListener {
    private XulComponent paramTableElement;

    private CheckEmptyPathHandler( final XulComponent paramTableElement ) {
      this.paramTableElement = paramTableElement;
      propertyChange( null );
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( StringUtils.isEmpty( getWrapper().getDrillDownPath() ) ) {
        paramTableElement.setDisabled( true );
      } else {
        paramTableElement.setDisabled( false );
      }
    }
  }

  private PathChangeHandler pathHandler;

  public GenericUrlDrillDownController() {
  }

  public void init( final ReportDesignerContext reportDesignerContext,
                    final DrillDownModel model,
                    final String[] fields ) {
    super.init( reportDesignerContext, model, fields );
    pathHandler = new PathChangeHandler();
    getModel().addPropertyChangeListener( DrillDownModel.DRILL_DOWN_PATH_PROPERTY, pathHandler );
    configureDisableTableOnEmptyFile();
  }

  protected void configureDisableTableOnEmptyFile() {
    final Document doc = getXulDomContainer().getDocumentRoot();
    final XulComponent paramTableElement = doc.getElementById( "parameter-table" );//NON-NLS
    if ( paramTableElement instanceof XulDrillDownParameterTable == false ) {
      return;
    }

    getWrapper().getModel().addPropertyChangeListener
      ( DrillDownModel.DRILL_DOWN_PATH_PROPERTY, new CheckEmptyPathHandler( paramTableElement ) );

  }

  public void deactivate() {
    getModel().removePropertyChangeListener( DrillDownModel.DRILL_DOWN_PATH_PROPERTY, pathHandler );
  }
}
