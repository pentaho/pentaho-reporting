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


package org.pentaho.reporting.designer.core.inspections;

import javax.swing.*;
import java.util.Arrays;

/**
 * User: Martin Date: 01.02.2006 Time: 18:43:37
 */
public class InspectionResult {
  public enum Severity {
    HINT,
    WARNING,
    ERROR
  }

  private Inspection source;
  private LocationInfo[] locationInfos;
  private Severity severity;
  private String description;
  private Action quickFix;

  public InspectionResult( final Inspection source,
                           final Severity severity,
                           final String description,
                           final LocationInfo locationInfos,
                           final Action quickFix ) {
    this( source, severity, description, new LocationInfo[] { locationInfos } );
    this.quickFix = quickFix;
  }

  public InspectionResult( final Inspection source,
                           final Severity severity,
                           final String description,
                           final LocationInfo locationInfos ) {
    this( source, severity, description, new LocationInfo[] { locationInfos } );
  }

  public InspectionResult( final Inspection source,
                           final Severity severity,
                           final String description,
                           final LocationInfo[] locationInfos ) {
    if ( severity == null ) {
      throw new IllegalArgumentException( "severity must not be null" );
    }
    if ( description == null ) {
      throw new IllegalArgumentException( "description must not be null" );
    }
    if ( locationInfos == null ) {
      throw new IllegalArgumentException( "locationInfos must not be null" );
    }
    this.severity = severity;
    this.description = description;
    this.locationInfos = locationInfos.clone();
    this.source = source;
  }

  public Inspection getSource() {
    return source;
  }

  public Severity getSeverity() {
    return severity;
  }

  public String getDescription() {
    return description;
  }

  public LocationInfo[] getLocationInfos() {
    return locationInfos.clone();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final InspectionResult that = (InspectionResult) o;

    if ( !description.equals( that.description ) ) {
      return false;
    }
    if ( !Arrays.equals( locationInfos, that.locationInfos ) ) {
      return false;
    }
    if ( severity != that.severity ) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    int result;
    result = severity.hashCode();
    result = 29 * result + description.hashCode();
    result = 29 * result + Arrays.hashCode( locationInfos );
    return result;
  }

  public Action getQuickFix() {
    return quickFix;
  }

  public String toString() {
    return severity + ": " + description;
  }
}
