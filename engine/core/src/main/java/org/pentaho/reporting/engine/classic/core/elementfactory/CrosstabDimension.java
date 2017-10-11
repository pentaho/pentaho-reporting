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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.io.Serializable;

public class CrosstabDimension implements Serializable, Cloneable {
  private String field;
  private String title;
  private boolean printSummary;
  private String summaryTitle;

  public CrosstabDimension( final String item ) {
    this.field = item;
    this.title = item;
    this.printSummary = false;
    this.summaryTitle = "Summary";
  }

  public CrosstabDimension( final String field, final String title ) {
    this( field, title, false, null );
  }

  public CrosstabDimension( final String field, final String title, final boolean printSummary,
      final String summaryTitle ) {
    this.field = field;
    this.title = title;
    this.printSummary = printSummary;
    this.summaryTitle = summaryTitle;
  }

  public String getField() {
    return field;
  }

  public void setField( final String field ) {
    this.field = field;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( final String title ) {
    this.title = title;
  }

  public boolean isPrintSummary() {
    return printSummary;
  }

  public void setPrintSummary( final boolean printSummary ) {
    this.printSummary = printSummary;
  }

  public String getSummaryTitle() {
    return summaryTitle;
  }

  public void setSummaryTitle( final String summaryTitle ) {
    this.summaryTitle = summaryTitle;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CrosstabDimension that = (CrosstabDimension) o;

    if ( printSummary != that.printSummary ) {
      return false;
    }
    if ( field != null ? !field.equals( that.field ) : that.field != null ) {
      return false;
    }
    if ( summaryTitle != null ? !summaryTitle.equals( that.summaryTitle ) : that.summaryTitle != null ) {
      return false;
    }
    if ( title != null ? !title.equals( that.title ) : that.title != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = field != null ? field.hashCode() : 0;
    result = 31 * result + ( title != null ? title.hashCode() : 0 );
    result = 31 * result + ( printSummary ? 1 : 0 );
    result = 31 * result + ( summaryTitle != null ? summaryTitle.hashCode() : 0 );
    return result;
  }

  public CrosstabDimension clone() {
    try {
      return (CrosstabDimension) super.clone();
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
