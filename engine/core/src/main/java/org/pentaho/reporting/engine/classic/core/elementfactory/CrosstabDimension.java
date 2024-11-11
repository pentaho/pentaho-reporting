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
