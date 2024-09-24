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
