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

public class PentahoRemoteDrillDownController extends PentahoDrillDownController {
  public PentahoRemoteDrillDownController() {
  }

  protected DrillDownParameter[] filterParameter( final DrillDownParameter[] parameter ) {
    final PentahoPathModel pentahoPathWrapper = getPentahoPathWrapper();
    // modify the parameter model.
    final ArrayList<DrillDownParameter> list = new ArrayList<DrillDownParameter>();
    boolean solutionAdded = false;
    boolean pathAdded = false;
    boolean nameAdded = false;
    for ( int i = 0; i < parameter.length; i++ ) {
      final DrillDownParameter drillDownParameter = parameter[ i ];
      if ( "solution".equals( drillDownParameter.getName() ) ) {
        list.add( new DrillDownParameter( "solution", FormulaUtil.quoteString( pentahoPathWrapper.getSolution() ) ) );
        solutionAdded = true;
      } else if ( "path".equals( drillDownParameter.getName() ) ) {
        list.add( new DrillDownParameter( "path", FormulaUtil.quoteString( pentahoPathWrapper.getPath() ) ) );
        pathAdded = true;
      } else if ( "name".equals( drillDownParameter.getName() ) ) {
        list.add( new DrillDownParameter( "name", FormulaUtil.quoteString( pentahoPathWrapper.getName() ) ) );
        nameAdded = true;
      } else if ( "::pentaho-path".equals( drillDownParameter.getName() ) ) {
        assert true;  // No-op to satisfy checkstyle
      } else {
        list.add( drillDownParameter );
      }
    }
    if ( nameAdded == false ) {
      list.add( 0, new DrillDownParameter( "name", FormulaUtil.quoteString( pentahoPathWrapper.getName() ) ) );
    }
    if ( pathAdded == false ) {
      list.add( 0, new DrillDownParameter( "path", FormulaUtil.quoteString( pentahoPathWrapper.getPath() ) ) );
    }
    if ( solutionAdded == false ) {
      list.add( 0, new DrillDownParameter( "solution", FormulaUtil.quoteString( pentahoPathWrapper.getSolution() ) ) );
    }

    return super.filterParameter( list.toArray( new DrillDownParameter[ list.size() ] ) );
  }

  protected String getProfileName() {
    return "pentaho";
  }
}
