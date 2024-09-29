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
