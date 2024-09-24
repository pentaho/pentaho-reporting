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

package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

import java.util.Date;

/**
 * @deprecated These functions are no longer supported.
 */
public class TimeDiffFunction extends AbstractFunction {
  private long diff;
  private boolean valid;

  private String field1;
  private String field2;


  public TimeDiffFunction() {
    diff = 0;
  }


  public TimeDiffFunction( final String name ) {
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
    valid = false;

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

    diff = value1 - value2;
    valid = true;
  }


  public Object getValue() {
    if ( valid ) {
      return new Long( diff );
    }
    return null;
  }
}
