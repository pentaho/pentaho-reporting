/*
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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.ColumnWidth;

public class ColumnWidthWrapper extends ColumnWidth {
  private ColumnWidth backend;

  public ColumnWidthWrapper( final ColumnWidth backend ) {
    this.backend = backend;
  }

  @Override
  public boolean equals( final Object object ) {
    if ( object == null ) {
      return false;
    }
    if ( object instanceof ColumnWidth == false ) {
      return false;
    }
    return backend.equals( object );
  }

  public int hashCode() {
    int result = backend.getType().hashCode();
    final long bits = Double.doubleToLongBits( backend.getWidth() );
    result *= 23 + (int) ( bits ^ ( bits >>> 32 ) );
    return result;
  }

  public WidthType getType() {
    return backend.getType();
  }

  public double getWidth() {
    return backend.getWidth();
  }

  public void setType( final WidthType type ) {
    backend.setType( type );
  }

  public void setWidth( final double width ) {
    backend.setWidth( width );
  }
}
