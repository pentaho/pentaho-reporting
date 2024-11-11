/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
