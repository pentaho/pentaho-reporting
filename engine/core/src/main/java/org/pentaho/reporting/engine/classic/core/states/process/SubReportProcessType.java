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


package org.pentaho.reporting.engine.classic.core.states.process;

public class SubReportProcessType {
  public static final SubReportProcessType INLINE = new SubReportProcessType( "inline" );
  public static final SubReportProcessType BANDED = new SubReportProcessType( "banded" );

  private String name;

  private SubReportProcessType( final String name ) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
