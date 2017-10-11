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
