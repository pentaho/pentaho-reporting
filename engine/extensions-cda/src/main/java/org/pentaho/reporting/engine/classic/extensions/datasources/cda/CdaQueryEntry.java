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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;

import java.io.Serializable;
import java.util.Arrays;

public class CdaQueryEntry implements Serializable {
  private String id;
  private String name;
  private ParameterMapping[] parameters;

  public CdaQueryEntry( final String logicalQueryName, final String cdaId ) {
    if ( logicalQueryName == null ) {
      throw new NullPointerException();
    }
    if ( cdaId == null ) {
      throw new NullPointerException();
    }
    this.name = logicalQueryName;
    this.id = cdaId;
    this.parameters = new ParameterMapping[ 0 ];
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId( final String id ) {
    if ( id == null ) {
      throw new NullPointerException();
    }
    this.id = id;
  }

  public ParameterMapping[] getParameters() {
    return parameters.clone();
  }

  public void setParameters( final ParameterMapping[] parameters ) {
    this.parameters = parameters.clone();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CdaQueryEntry that = (CdaQueryEntry) o;

    if ( !id.equals( that.id ) ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }
    if ( !Arrays.equals( parameters, that.parameters ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + Arrays.hashCode( parameters );
    return result;
  }
}
