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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import java.util.ArrayList;

public class PentahoSugarDrillDownController extends PentahoDrillDownController {
  public PentahoSugarDrillDownController() {
  }

  protected DrillDownParameter[] filterParameter( final DrillDownParameter[] parameter ) {
    final PentahoPathModel pentahoPathWrapper = getPentahoPathWrapper();
    // modify the parameter model.
    final ArrayList<DrillDownParameter> list = new ArrayList<DrillDownParameter>();
    boolean pathAdded = false;
    for ( int i = 0; i < parameter.length; i++ ) {
      final DrillDownParameter drillDownParameter = parameter[i];
      if ( "::pentaho-path".equals( drillDownParameter.getName() ) && pathAdded == false ) {
        list.add( new DrillDownParameter( "::pentaho-path", FormulaUtil.quoteString( pentahoPathWrapper.getLocalPath() ) ) );
        pathAdded = true;
      } else {
        if ( !( "solution".equals( drillDownParameter.getName() ) || "path".equals( drillDownParameter.getName() ) || "name"
            .equals( drillDownParameter.getName() ) ) ) {
          list.add( drillDownParameter );
        }
      }
    }
    if ( pathAdded == false ) {
      list.add( 0, new DrillDownParameter( "::pentaho-path", FormulaUtil
          .quoteString( pentahoPathWrapper.getLocalPath() ) ) );
    }
    return super.filterParameter( list.toArray( new DrillDownParameter[list.size()] ) );

  }

  protected String getProfileName() {
    return "pentaho-sugar";
  }

}
