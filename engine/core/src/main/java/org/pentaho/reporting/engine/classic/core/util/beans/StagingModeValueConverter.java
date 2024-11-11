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


package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.util.StagingMode;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class StagingModeValueConverter implements ValueConverter {
  public StagingModeValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }

    if ( o instanceof StagingMode ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a StagingMode." );
    }

  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( StagingMode.MEMORY.toString().equals( o ) ) {
      return StagingMode.MEMORY;
    }
    if ( StagingMode.THRU.toString().equals( o ) ) {
      return StagingMode.THRU;
    }
    if ( StagingMode.TMPFILE.toString().equals( o ) ) {
      return StagingMode.TMPFILE;
    }
    throw new BeanException( "Invalid value specified for StagingMode" );
  }
}
