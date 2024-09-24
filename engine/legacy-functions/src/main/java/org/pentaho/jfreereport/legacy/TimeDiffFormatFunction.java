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

package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

/**
 * @deprecated These functions are no longer supported.
 */
public class TimeDiffFormatFunction extends AbstractFunction {
  private String field;
  private String formatted;


  public TimeDiffFormatFunction() {
  }


  public TimeDiffFormatFunction( final String name ) {
    this();
    setName( name );
  }

  public String getField() {
    return field;
  }


  public void setField( String field ) {
    this.field = field;
  }


  public void itemsAdvanced( final ReportEvent event ) {
    formatted = null;

    final Object fieldValue1 = getDataRow().get( getField() );
    if ( !( fieldValue1 instanceof Number ) ) {
      return;
    }
    long value1 = ( (Number) fieldValue1 ).longValue();
    format( value1 );
  }


  private void format( long value1 ) {
    long hours = value1 / ( 60 * 60 * 1000 );
    long rest = value1 - ( hours * 60 * 60 * 1000 );
    long minutes = rest / ( 60 * 1000 );
    rest -= minutes * 60 * 1000;
    long seconds = rest / 1000;

    String min = String.valueOf( minutes );
    String sec = String.valueOf( seconds );
    if ( min.length() < 2 ) {
      min = "0" + min;
    }
    if ( sec.length() < 2 ) {
      sec = "0" + sec;
    }
    formatted = hours + ":" + min + ":" + sec;
  }


  public Object getValue() {
    return formatted;
  }

}
