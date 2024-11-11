/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskMetaData;

import java.io.Serializable;

public class ReportProcessTaskMetaDataCollection implements Serializable {
  private ReportProcessTaskMetaData[] expressionMetaData;

  public ReportProcessTaskMetaDataCollection( final ReportProcessTaskMetaData[] expressionMetaData ) {
    if ( expressionMetaData == null ) {
      throw new NullPointerException();
    }

    this.expressionMetaData = expressionMetaData.clone();
  }

  public ReportProcessTaskMetaData[] getMetaData() {
    return expressionMetaData.clone();
  }
}
