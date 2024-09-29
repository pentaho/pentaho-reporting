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

import java.util.Date;

/**
 * @deprecated These functions are no longer supported.
 */
public class TimeDiffAndFormatFunction extends AbstractFunction {
  private String formatted;
  private String field1;
  private String field2;


  public TimeDiffAndFormatFunction() {
  }

  public TimeDiffAndFormatFunction( final String name ) {
    this();
    setName( name );
  }

  public String getField1() {
    return field1;
  }

  public void setField1( String field1 ) {
    this.field1 = field1;
  }

  public String getField2() {
    return field2;
  }

  public void setField2( String field2 ) {
    this.field2 = field2;
  }

  public void itemsAdvanced( final ReportEvent event ) {
    formatted = null;

    final Object fieldValue1 = getDataRow().get( getField1() );
    final Object fieldValue2 = getDataRow().get( getField2() );
    long value1;
    if ( fieldValue1 instanceof Number ) {
      Number number = (Number) fieldValue1;
      value1 = number.longValue();
    } else if ( fieldValue1 instanceof Date ) {
      Date date = (Date) fieldValue1;
      value1 = date.getTime();
    } else {
      return;
    }

    long value2;
    if ( fieldValue2 instanceof Number ) {
      Number number = (Number) fieldValue2;
      value2 = number.longValue();
    } else if ( fieldValue2 instanceof Date ) {
      Date date = (Date) fieldValue2;
      value2 = date.getTime();
    } else {
      return;
    }

    long diff = value1 - value2;
    format( diff );
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
