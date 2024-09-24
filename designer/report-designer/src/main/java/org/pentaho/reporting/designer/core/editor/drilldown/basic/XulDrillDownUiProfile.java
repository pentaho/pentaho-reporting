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

import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

import java.util.Arrays;
import java.util.Locale;

public abstract class XulDrillDownUiProfile implements DrillDownUiProfile {
  private DrillDownProfile drillDownProfile;
  private String[] names;

  public XulDrillDownUiProfile( final String name ) {
    //noinspection RedundantStringToString
    this( new String[] { name.toString() } );
  }

  public XulDrillDownUiProfile( final String[] names ) {
    init( names );
  }

  protected XulDrillDownUiProfile() {
  }

  protected void init( final String[] names ) {
    if ( names == null || names.length == 0 ) {
      throw new IllegalArgumentException();
    }

    drillDownProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( names[ 0 ] );
    if ( drillDownProfile == null ) {
      throw new IllegalArgumentException();
    }

    this.names = names;
  }

  public DrillDownUi createUI() {
    return new XulDrillDownUi( names, drillDownProfile.getAttribute( "group" ) );
  }

  public String getDisplayName() {
    return drillDownProfile.getGroupDisplayName( Locale.getDefault() );
  }

  public boolean canHandle( final String profileName ) {
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[ i ];
      if ( name.equals( profileName ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final XulDrillDownUiProfile that = (XulDrillDownUiProfile) o;

    if ( !Arrays.equals( names, that.names ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return Arrays.hashCode( names );
  }
}
